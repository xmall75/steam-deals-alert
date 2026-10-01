package com.xmall75.steamdealsalert.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.xmall75.steamdealsalert.data.local.AppDatabase;
import com.xmall75.steamdealsalert.data.local.DealDao;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.data.remote.ApiClient;
import com.xmall75.steamdealsalert.data.remote.CheapSharkApi;
import com.xmall75.steamdealsalert.data.remote.DealDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class DealRepository {

    private final AppDatabase db;
    private final DealDao dao;
    private final CheapSharkApi api;

    public DealRepository(Context context) {
        this.db = AppDatabase.getInstance(context);
        this.dao = db.dealDao();
        this.api = ApiClient.get();
    }

    public LiveData<List<DealEntity>> observeActive() {
        return dao.observeActive();
    }

    public List<DealEntity> sync(double maxPrice, int minRatingPercent) throws IOException {
        Response<List<DealDto>> response = api.getDeals(maxPrice).execute();
        List<DealDto> body = response.body();
        if (!response.isSuccessful() || body == null) {
            throw new IOException("HTTP " + response.code());
        }

        final long now = System.currentTimeMillis();

        return db.runInTransaction(() -> {
            Set<String> seen = new HashSet<>();
            List<String> activeIds = new ArrayList<>();

            for (DealDto dto : body) {
                if (!DealFilter.isValidDeal(dto, maxPrice, minRatingPercent)) continue;
                if (dto.steamAppID == null || dto.steamAppID.trim().isEmpty()) continue;
                if (!seen.add(dto.steamAppID)) continue;

                DealEntity entity = dao.getById(dto.steamAppID);
                if (entity == null) {
                    entity = new DealEntity();
                    entity.steamAppId = dto.steamAppID;
                    entity.firstSeenAt = now;
                    entity.notified = false;
                } else if (!entity.active) {
                    entity.firstSeenAt = now;
                    entity.notified = false;
                }

                entity.title = dto.title != null ? dto.title : "Unknown Title";
                entity.salePrice = DealFilter.toDouble(dto.salePrice);
                entity.normalPrice = DealFilter.toDouble(dto.normalPrice);
                entity.ratingPercent = DealFilter.toInt(dto.steamRatingPercent);
                entity.ratingText = dto.steamRatingText;
                entity.thumb = dto.thumb;
                entity.active = true;

                dao.upsert(entity);
                activeIds.add(entity.steamAppId);
            }

            if (activeIds.isEmpty()) {
                dao.deactivateAll();
            } else {
                dao.deactivateNotIn(activeIds);
            }

            return dao.getPendingNotify();
        });
    }

    public void markNotified(List<DealEntity> deals) {
        if (deals == null || deals.isEmpty()) return;

        db.runInTransaction(() -> {
            List<String> ids = new ArrayList<>();
            for (DealEntity d : deals) {
                if (d != null && d.steamAppId != null) {
                    ids.add(d.steamAppId);
                }
            }
            if (!ids.isEmpty()) {
                dao.markNotified(ids);
            }
        });
    }
}