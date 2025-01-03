package com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model
 * @description : 젤리 이미지를 저장하기 위한 JellyImage Request DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyImageSaveReqDTO {
    @SerializedName("jellyId")
    private Long jellyId;

    @SerializedName("imageName")
    private String imageName;

    public JellyImageSaveReqDTO(Long jellyId, String imageName) {
        this.jellyId = jellyId;
        this.imageName = imageName;
    }

    public Long getJellyId() {
        return jellyId;
    }

    public void setJellyId(Long jellyId) {
        this.jellyId = jellyId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
