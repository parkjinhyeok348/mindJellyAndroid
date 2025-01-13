package com.mindJellyProject.mindjelly.common;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.common
 * @description : API 관리하는 공용 Retrofit Client
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03

 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.0.16:8080/";

    // Retrofit 인스턴스 생성
    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // API 인터페이스 제공 메서드
    public static <T> T createService(Class<T> serviceClass) {
        return getInstance().create(serviceClass);
    }
}
