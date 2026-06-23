package com.mindJellyProject.mindjelly.common;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public final class WorkManagerScheduler {
    private static final String WORK_TAG = "aging_check_work";

    private WorkManagerScheduler() {}

    public static void scheduleOnce(Context context) {
        long delayMinutes = minutesToNextNineAm(LocalTime.now());
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                AgingCheckWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .addTag(WORK_TAG)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request);
    }

    public static long minutesToNextNineAm(LocalTime now) {
        LocalTime target = LocalTime.of(9, 0);
        long minutes = Duration.between(now, target).toMinutes();
        if (minutes <= 0) minutes += 24L * 60;
        return minutes;
    }
}
