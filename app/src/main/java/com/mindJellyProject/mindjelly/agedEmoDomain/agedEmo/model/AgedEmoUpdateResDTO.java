package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model
 * @description : AgedEmo 상세 정보 수정하기 위한 AgedEmo Response DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoUpdateResDTO {
    @SerializedName("content")
    private String content;

    @SerializedName("createDate")
    private String createDate;

    @SerializedName("agedEmoImages")
    private List<AgedEmoImage> agedEmoImages = new ArrayList<>();

    public AgedEmoUpdateResDTO(String content, String createDate, List<AgedEmoImage> agedEmoImages) {
        this.content = content;
        this.createDate = createDate;
        this.agedEmoImages = agedEmoImages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<AgedEmoImage> getAgedEmoImages() {
        return agedEmoImages;
    }

    public void setAgedEmoImages(List<AgedEmoImage> agedEmoImages) {
        this.agedEmoImages = agedEmoImages;
    }
}
