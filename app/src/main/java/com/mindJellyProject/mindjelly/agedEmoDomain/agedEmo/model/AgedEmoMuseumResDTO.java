package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model
 * @description : 젤리 뮤지엄에 출력하기위해 사용하는 AgedEmo Response DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoMuseumResDTO {
    @SerializedName("agedEmoId")
    private Long agedEmoId;

    @SerializedName("jellyCombId")
    private Long jellyCombId;

    @SerializedName("createDate")
    private String createDate;

    public AgedEmoMuseumResDTO(Long agedEmoId, String createDate, Long jellyCombId) {
        this.agedEmoId = agedEmoId;
        this.createDate = createDate;
        this.jellyCombId = jellyCombId;
    }

    public Long getAgedEmoId() {
        return agedEmoId;
    }

    public void setAgedEmoId(Long agedEmoId) {
        this.agedEmoId = agedEmoId;
    }

    public Long getJellyCombId() {
        return jellyCombId;
    }

    public void setJellyCombId(Long jellyCombId) {
        this.jellyCombId = jellyCombId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
