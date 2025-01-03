package com.mindJellyProject.mindjelly.jellyDomain.jellyImage.retrofit;

import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageSaveReqDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyImage.retrofit
 * @description : JellyImage Service
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public interface JellyImageService {
    @POST("/jellyImage")
    Call<JellyImageResDTO> createJellyImage(@Body JellyImageSaveReqDTO reqDTO);

    @GET("/jellyImage/{jellyId}")
    Call<List<JellyImageResDTO>> getJellyImageList(@Path("jellyId") Long jellyId);
}
