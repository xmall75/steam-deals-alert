package com.xmall75.steamdealsalert.worker;

import android.content.Context;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.DealRepository;
import com.xmall75.steamdealsalert.data.Prefs;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.notification.NotificationHelper;
import com.xmall75.steamdealsalert.service.DealsForegroundService;

import java.io.IOException;
import java.util.List;

public final class SyncRunner {

    private SyncRunner() {}

    public static synchronized int syncAndNotify(Context context) throws IOException {
        Context app = context.getApplicationContext();
        DealRepository repository = new DealRepository(app);

        List<DealEntity> pending = repository.sync(Prefs.getMaxPrice(app), Prefs.getMinRating(app));

        if (pending.isEmpty()) {
            DealsForegroundService.updateStatus(app, "All discounted games are notified");
            return 0;
        }

        if (NotificationHelper.canNotify(app)) {
            for (DealEntity deal : pending) {
                NotificationHelper.notifyDeal(app, deal);
            }
            repository.markNotified(pending);

            String statusMsg = pending.size() + " new games are on sales!";
            DealsForegroundService.updateStatus(app, statusMsg);
        } else {
            DealsForegroundService.updateStatus(app, String.valueOf(R.string.status_no_permission));
        }

        return pending.size();
    }
}