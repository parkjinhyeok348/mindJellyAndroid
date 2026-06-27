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

        if (response.code() == 401 || isExpiredToken403(response)) {
            SessionManager.getInstance(appContext).clear();
            RetrofitClient.reset();
            Intent intent = new Intent(appContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        }

        return response;
    }

    // 403이 토큰 만료에 의한 것인지 응답 바디로 구분
    // 백엔드가 비즈니스 로직 거부(젤리 조합 등)에도 403을 사용하므로 키워드로 필터링
    private boolean isExpiredToken403(Response response) {
        if (response.code() != 403) return false;
        try {
            okhttp3.ResponseBody peeked = response.peekBody(512);
            if (peeked == null) return false;
            String body = peeked.string().toLowerCase();
            return body.contains("expired") || body.contains("jwt") || body.contains("token");
        } catch (Exception e) {
            return false;
        }
    }
}
