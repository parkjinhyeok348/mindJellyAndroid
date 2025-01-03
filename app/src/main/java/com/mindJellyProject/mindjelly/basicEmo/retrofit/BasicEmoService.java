package com.mindJellyProject.mindjelly.basicEmo.retrofit;

import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.basicEmo.retrofit
 * @description : BasicEmo Service
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public interface BasicEmoService {
    @GET("/basicEmo/{emoId}")
    Call<BasicEmoResDTO> getBasicEmoById(@Path("emoId") Long emoId);

    @GET("/basicEmo")
    Call<List<BasicEmoResDTO>> getAllBasicEmoList();
}
