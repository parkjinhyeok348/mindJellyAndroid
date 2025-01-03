package com.mindJellyProject.mindjelly.basicEmo.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.basicEmo.model
 * @description : 기본 감정 상세 정보를 가져오기 위한 BasicEmo Response DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class BasicEmoResDTO {
    @SerializedName("emoId")
    private Long emoId;

    @SerializedName("emoName")
    private String emoName;

    @SerializedName("emoIcon")
    private String emoIcon;

    public BasicEmoResDTO(Long emoId, String emoName, String emoIcon) {
        this.emoId = emoId;
        this.emoName = emoName;
        this.emoIcon = emoIcon;
    }

    public Long getEmoId() {
        return emoId;
    }

    public void setEmoId(Long emoId) {
        this.emoId = emoId;
    }

    public String getEmoName() {
        return emoName;
    }

    public void setEmoName(String emoName) {
        this.emoName = emoName;
    }

    public String getEmoIcon() {
        return emoIcon;
    }

    public void setEmoIcon(String emoIcon) {
        this.emoIcon = emoIcon;
    }
}
