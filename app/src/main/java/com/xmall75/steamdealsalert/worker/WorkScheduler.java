package com.xmall75.steamdealsalert.worker;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class WorkScheduler {

    public static final String UNIQUE_NAME = "steam_deals_check";
    private static final int MIN_INTERVAL_MINUTES = 15;

    private WorkScheduler() {}

    public static void schedule(Context context, int intervalMinutes, boolean replace) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                FreeGameWorker.class,
                Math.max(MIN_INTERVAL_MINUTES, intervalMinutes),
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                replace ? ExistingPeriodicWorkPolicy.UPDATE : ExistingPeriodicWorkPolicy.KEEP,
                request);
    }
}
