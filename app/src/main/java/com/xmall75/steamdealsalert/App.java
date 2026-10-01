package com.xmall75.steamdealsalert;

import android.app.Application;

import com.xmall75.steamdealsalert.data.Prefs;
import com.xmall75.steamdealsalert.notification.NotificationHelper;
import com.xmall75.steamdealsalert.worker.WorkScheduler;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createChannel(this);
        WorkScheduler.schedule(this, Prefs.getIntervalMinutes(this), false);
    }
}
