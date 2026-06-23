package com.mindJellyProject.mindjelly.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingCompleteActivity;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit.JellyService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

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
        Context ctx = getApplicationContext();
        long userId = SessionManager.getInstance(ctx).getUserId();
        if (userId <= 0) return Result.success();

        try {
            JellyService service = RetrofitClient.createService(JellyService.class, ctx);
            Response<List<JellyDrawerResDTO>> response = service.getJellyList(userId).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return response.code() == 401 ? Result.success() : Result.retry();
            }

            List<JellyDrawerResDTO> completed = AgingFilter.filterCompleted(
                    response.body(), LocalDate.now());

            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Set<String> notified = new HashSet<>(
                    prefs.getStringSet(KEY_NOTIFIED_IDS, new HashSet<>()));

            for (JellyDrawerResDTO jelly : completed) {
                String idStr = String.valueOf(jelly.getJellyId());
                if (notified.contains(idStr)) continue;
                sendNotification(ctx, jelly);
                notified.add(idStr);
            }
            prefs.edit().putStringSet(KEY_NOTIFIED_IDS, notified).apply();
            return Result.success();

        } catch (Exception e) {
            return Result.retry();
        }
    }

    private void sendNotification(Context ctx, JellyDrawerResDTO jelly) {
        Intent intent = new Intent(ctx, AgingCompleteActivity.class);
        intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_ID,      jelly.getJellyId());
        intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_COMB_ID, jelly.getJellyCombId());
        intent.putExtra(AgingCompleteActivity.EXTRA_EMO1_NAME,     jelly.getEmo1Name());
        intent.putExtra(AgingCompleteActivity.EXTRA_EMO2_NAME,     jelly.getEmo2Name());
        intent.putExtra(AgingCompleteActivity.EXTRA_CREATE_DATE,   jelly.getCreateDate());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(
                ctx, jelly.getJellyId().intValue(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("젤리가 숙성됐어요! 🍬")
                .setContentText(safe(jelly.getEmo1Name()) + " + " + safe(jelly.getEmo2Name())
                        + " 조합이 완성됐어요")
                .setAutoCancel(true)
                .setContentIntent(pi);

        ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(jelly.getJellyId().intValue(), builder.build());
    }

    private String safe(String s) { return s != null ? s : "-"; }
}
