package com.mindJellyProject.mindjelly.common;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.common
 * @description : API 관리하는 공용 Retrofit Client
 * @modification : 2026-05-12(KTDS) OkHttp AuthInterceptor 적용
 * @date : 2025-01-03

 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 * 2026-05-12     KTDS            JWT Bearer 토큰 주입 인터셉터 연결
 */
public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String BASE_SERVER_URL = "http://10.0.2.2:8080";
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient(Context context) {
        Context appContext = context.getApplicationContext();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(appContext))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    // RetrofitClient 인스턴스 생성
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    // API 인터페이스 제공 메서드
    public static <T> T createService(Class<T> serviceClass, Context context) {
        return getInstance(context).retrofit.create(serviceClass);
    }

    // 기존 Repository들이 Context 주입으로 마이그레이션되기 전까지 컴파일을 유지하는 임시 경로
    @Deprecated
    public static <T> T createService(Class<T> serviceClass) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(serviceClass);
    }

    // 로그아웃이나 토큰 갱신 시 싱글턴 재초기화
    public static synchronized void reset() {
        instance = null;
    }
}
