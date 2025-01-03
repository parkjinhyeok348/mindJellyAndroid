package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImage;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.model
 * @description : 젤리 정보를 수정하기 위한 Jelly Request DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class JellyUpdateReqDTO {
    @SerializedName("jellyName")
    private String jellyName;
    @SerializedName("content")
    private String content;
    @SerializedName("jellyImages")
    private List<JellyImage> jellyImages;

    public JellyUpdateReqDTO(String jellyName, String content, List<JellyImage> jellyImages) {
        this.jellyName = jellyName;
        this.content = content;
        this.jellyImages = jellyImages;
    }

    public String getJellyName() {
        return jellyName;
    }

    public void setJellyName(String jellyName) {
        this.jellyName = jellyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<JellyImage> getJellyImages() {
        return jellyImages;
    }

    public void setJellyImages(List<JellyImage> jellyImages) {
        this.jellyImages = jellyImages;
    }
}
