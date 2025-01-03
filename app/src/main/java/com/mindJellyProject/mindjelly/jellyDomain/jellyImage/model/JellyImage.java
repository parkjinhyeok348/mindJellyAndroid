package com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model
 * @description : JellyImage Model
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class JellyImage {
    @SerializedName("jellyImageListId")
    private Long jellyImageListId;

    @SerializedName("jellyId")
    private Long jellyId;

    @SerializedName("imageName")
    private String imageName;

    public JellyImage(Long jellyImageListId, Long jellyId, String imageName) {
        this.jellyImageListId = jellyImageListId;
        this.jellyId = jellyId;
        this.imageName = imageName;
    }

    public Long getJellyImageListId() {
        return jellyImageListId;
    }

    public void setJellyImageListId(Long jellyImageListId) {
        this.jellyImageListId = jellyImageListId;
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
