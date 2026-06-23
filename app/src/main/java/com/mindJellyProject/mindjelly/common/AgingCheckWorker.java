package com.mindJellyProject.mindjelly.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AgingCheckWorker extends Worker {

    public static final String PREFS_NAME       = "aging_worker_prefs";
    public static final String KEY_NOTIFIED_IDS = "notified_jelly_ids";
    public static final String CHANNEL_ID       = "aging_complete_channel";

    public AgingCheckWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.success();
    }
}
