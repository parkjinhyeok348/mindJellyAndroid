package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Phase2
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.model
 * @description : 숙성 시작(PATCH /api/jelly/{id}) 요청 DTO
 *                status 필드는 "AGING"으로 고정 — 클라이언트가 임의 상태로 전환 불가 (T-02A-01)
 * @date : 2026-05-14
 */
public class JellyStartAgingReqDTO {

    /**
     * status는 "AGING"으로 고정. setter를 노출하지 않아 외부에서 변경 불가.
     * Threat T-02A-01: Tampering mitigation — 클라이언트가 임의 상태 전환 차단.
     */
    @SerializedName("status")
    private final String status = "AGING";

    /**
     * 기본 생성자 — Gson 역직렬화 및 일반 인스턴스 생성 모두 사용 가능.
     */
    public JellyStartAgingReqDTO() {
        // status는 "AGING"으로 필드 초기화됨
    }

    public String getStatus() {
        return status;
    }
}
