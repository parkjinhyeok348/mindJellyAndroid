package com.mindJellyProject.mindjelly.common;

import android.content.Context;
import android.content.Intent;

import com.mindJellyProject.mindjelly.users.view.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author : KTDS
 * @className : com.mindJellyProject.mindjelly.common
 * @description : API 요청에 JWT Bearer 토큰을 주입하는 OkHttp 인터셉터
 * @modification : 2026-05-12(KTDS) 생성
 * @date : 2026-05-12
 */
public class AuthInterceptor implements Interceptor {
    private final Context appContext;

    public AuthInterceptor(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = SessionManager.getInstance(appContext).getToken();
        Request request = originalRequest;

        if (token != null) {
            request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }

        Response response = chain.proceed(request);

        if (response.code() == 401 || response.code() == 403) {
            SessionManager.getInstance(appContext).clear();
            RetrofitClient.reset();
            Intent intent = new Intent(appContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        }

        return response;
    }
}
