package com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyStartAgingReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateResDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit
 * @description : Jelly Service
 * @modification : 2026-05-14(Phase2) startAging PATCH 엔드포인트 추가 (D-03, DRAW-03)
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 * 2026-05-14     Phase2          @PATCH("/jelly/{jellyId}") startAging 추가
 */
public interface JellyService {

    @POST("/jelly")
    Call<Jelly> createJelly(@Body JellySaveReqDTO jellySaveReqDTO);

    @GET("/jelly/{jellyId}/info")
    Call<JellyUpdateResDTO> getJellyInfo(@Path("jellyId") Long jellyId);

    @PUT("/jelly/{jellyId}")
    Call<Jelly> updateJelly(@Path("jellyId") Long jellyId, @Body JellyUpdateReqDTO reqDTO);

    @GET("/jelly/{jellyId}")
    Call<JellyResDTO> getJellyById(@Path("jellyId") Long jellyId);

    @GET("jelly/user/{userId}")
    Call<List<JellyDrawerResDTO>> getJellyList(@Path("userId") Long userId);

    /**
     * 숙성 시작 API — PATCH /jelly/{jellyId}
     * Body: { "status": "AGING" }
     * 응답 형태 무관, isSuccessful() 체크만 사용 (D-03)
     */
    @PATCH("/jelly/{jellyId}")
    Call<ResponseBody> startAging(@Path("jellyId") Long jellyId, @Body JellyStartAgingReqDTO reqDTO);

    // 젤리 삭제 — DELETE /jelly/{jellyId}. 응답 본문 무관, isSuccessful() 체크만 사용
    @DELETE("/jelly/{jellyId}")
    Call<ResponseBody> deleteJelly(@Path("jellyId") Long jellyId);
}
