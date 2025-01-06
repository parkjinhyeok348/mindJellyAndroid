package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageSaveReqDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit
 * @description : AgedEmoImage Service
 * @modification : 2025-01-06(Jinhyeok) 수정
 * @date : 2025-01-06
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-06     Jinhyeok        주석 생성
 */
public interface AgedEmoImageService {
    @POST("/agedEmoImage")
    Call<AgedEmoImageResDTO> createAgedEmoImage(@Body AgedEmoImageSaveReqDTO reqDTO);

    @GET("/agedEmoImage/{agedEmoId}")
    Call<List<AgedEmoImageResDTO>> getAgedEmoImageList(@Path("agedEmoId") Long agedEmoId);
}
