package com.xmall75.steamdealsalert.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

public class FreeGameWorker extends Worker {

    private static final int MAX_ATTEMPTS = 3;

    public FreeGameWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            SyncRunner.syncAndNotify(getApplicationContext());
            return Result.success();
        } catch (IOException e) {
            return getRunAttemptCount() < MAX_ATTEMPTS ? Result.retry() : Result.success();
        }
    }
}
