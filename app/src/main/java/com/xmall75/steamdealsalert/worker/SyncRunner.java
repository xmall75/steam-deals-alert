package com.xmall75.steamdealsalert.worker;

import android.content.Context;

import com.xmall75.steamdealsalert.data.DealRepository;
import com.xmall75.steamdealsalert.data.Prefs;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.notification.NotificationHelper;

import java.io.IOException;
import java.util.List;

public final class SyncRunner {

    private SyncRunner() {}

    public static synchronized int syncAndNotify(Context context) throws IOException {
        Context app = context.getApplicationContext();
        DealRepository repository = new DealRepository(app);

        double maxPrice = Prefs.getMaxPrice(app);
        int minRating = Prefs.getMinRating(app);

        List<DealEntity> pending = repository.sync(maxPrice, minRating);

        if (pending.isEmpty()) return 0;

        if (NotificationHelper.canNotify(app)) {
            for (DealEntity deal : pending) {
                NotificationHelper.notifyDeal(app, deal);
            }
            repository.markNotified(pending);
        }

        return pending.size();
    }
}