package com.mindJellyProject.mindjelly.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * @author : KTDS
 * @className : com.mindJellyProject.mindjelly.common
 * @description : JWT 세션 토큰을 암호화 저장하고 사용자 ID를 관리하는 클래스
 * @modification : 2026-05-12(KTDS) 생성
 * @date : 2026-05-12
 */
public class SessionManager {
    private static final String PREF_NAME = "mindjelly_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String TAG = "SessionManager";

    private static SessionManager instance;
    private final SharedPreferences encryptedPrefs;

    private SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("암호화 세션 저장소 초기화 실패", e);
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            Context appContext = context.getApplicationContext();
            instance = new SessionManager(appContext);
        }
        return instance;
    }

    public void saveToken(String token) {
        encryptedPrefs.edit()
                .putString(KEY_TOKEN, token)
                .apply();
        extractAndSaveUserId(token);
    }

    private void extractAndSaveUserId(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length < 2) {
                Log.e(TAG, "userId 추출 실패");
                return;
            }

            String payload = tokenParts[1];
            byte[] decoded = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            JSONObject payloadJson = new JSONObject(new String(decoded, StandardCharsets.UTF_8));
            long userId = payloadJson.getLong("userId");

            encryptedPrefs.edit()
                    .putLong(KEY_USER_ID, userId)
                    .apply();
        } catch (JSONException | IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, "userId 추출 실패", e);
        }
    }

    public String getToken() {
        return encryptedPrefs.getString(KEY_TOKEN, null);
    }

    public long getUserId() {
        return encryptedPrefs.getLong(KEY_USER_ID, -1L);
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    public void clear() {
        encryptedPrefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .apply();
    }
}
