package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.retrofit;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmo;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateResDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.retrofit
 * @description : AgedEmo Service
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public interface AgedEmoService {
    @POST("/agedEmo")
    Call<AgedEmo> createAgedEmo(@Body AgedEmoSaveReqDTO agedEmoSaveReqDTO);

    @GET("/agedEmo/{agedEmoId}/update-info")
    Call<AgedEmoUpdateResDTO> getAgedEmoInfo(@Path("agedEmoId") Long agedEmoId);

    @PUT("/agedEmo/{agedEmoId}")
    Call<AgedEmo> updateAgedEmo(@Path("agedEmoId") Long agedEmoId, @Body AgedEmoUpdateReqDTO reqDTO);

    @GET("/agedEmo/{agedEmoId}")
    Call<AgedEmoResDTO> getAgedEmoById(@Path("agedEmoId") Long agedEmoId);

    @GET("/agedEmo/user/{userId}")
    Call<List<AgedEmoMuseumResDTO>> getAgedEmoList(@Path("userId") Long userId);
}
