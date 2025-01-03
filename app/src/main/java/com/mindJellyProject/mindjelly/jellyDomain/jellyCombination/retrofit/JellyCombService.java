package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit;

import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit
 * @description :jelly Combination Service
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public interface JellyCombService {

    @GET("/jellyComb/{jellyCombId}")
    Call<JellyCombResDTO> getJellyCombById(@Path("jellyCombId") Long jellyCombId);

    @GET("/jellyComb/jelly-icon/{firstEmo}/{secondEmo}/{isAwaken}")
    Call<String> getJellyIcon(@Path("firstEmo") Long firstEmo, @Path("secondEmo") Long secondEmo, @Path("isAwaken") Boolean isAwaken);
}
