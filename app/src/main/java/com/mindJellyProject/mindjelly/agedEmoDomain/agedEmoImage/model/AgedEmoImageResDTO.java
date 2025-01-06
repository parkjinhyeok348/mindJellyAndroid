package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model
 * @description : AgedEmoImage 상세 정보 출력하기 위한 AgedEmoImage Response DTO
 * @modification : 2025-01-06(Jinhyeok) 수정
 * @date : 2025-01-06
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-06     Jinhyeok        주석 생성
 */
public class AgedEmoImageResDTO {
    @SerializedName("agedEmoImageListId")
    private Long agedEmoImageListId;

    @SerializedName("agedEmoId")
    private Long agedEmoId;

    @SerializedName("imageName")
    private String imageName;

    public AgedEmoImageResDTO(Long agedEmoImageListId, Long agedEmoId, String imageName) {
        this.agedEmoImageListId = agedEmoImageListId;
        this.agedEmoId = agedEmoId;
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getAgedEmoId() {
        return agedEmoId;
    }

    public void setAgedEmoId(Long agedEmoId) {
        this.agedEmoId = agedEmoId;
    }

    public Long getAgedEmoImageListId() {
        return agedEmoImageListId;
    }

    public void setAgedEmoImageListId(Long agedEmoImageListId) {
        this.agedEmoImageListId = agedEmoImageListId;
    }
}
