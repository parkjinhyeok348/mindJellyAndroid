package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit;

import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit
 * @description : jelly Combination Service
 * @modification : 2026-05-14(Phase2) getJellyCombId 엔드포인트 추가 (Pitfall 1 해결)
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 * 2026-05-14     Phase2          getJellyCombId(Long, Long) 추가 — jellyCombId 취득용
 */
public interface JellyCombService {

    @GET("/jellyComb/{jellyCombId}")
    Call<JellyCombResDTO> getJellyCombById(@Path("jellyCombId") Long jellyCombId);

    @GET("/jellyComb/jelly-icon/{firstEmo}/{secondEmo}/{isAwaken}")
    Call<String> getJellyIcon(@Path("firstEmo") Long firstEmo,
                              @Path("secondEmo") Long secondEmo,
                              @Path("isAwaken") Boolean isAwaken);

    /**
     * 두 감정 조합으로 jellyCombId를 조회한다 (RESEARCH.md Pitfall 1 해결).
     * JellySaveReqDTO.jellyCombId 필드를 채우기 위해 사용.
     *
     * TODO: 실제 호출 전 백엔드 API 존재 여부 확인 필요.
     *       엔드포인트가 없을 경우 getJellyIcon 응답에 jellyCombId 포함 요청 또는
     *       대체 방안 협의 필요.
     */
    @GET("/jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}")
    Call<Long> getJellyCombId(@Path("firstEmo") Long firstEmo,
                              @Path("secondEmo") Long secondEmo);
}
