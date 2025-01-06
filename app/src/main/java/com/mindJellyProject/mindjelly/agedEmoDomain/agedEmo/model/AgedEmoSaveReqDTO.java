package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model
 * @description : AgedEmo 생성하기 위한 AgedEmo Request DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoSaveReqDTO {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("jellyCombId")
    private Long jellyCombId;

    @SerializedName("agedEmoName")
    private String agedEmoName;

    @SerializedName("content")
    private String content;

    @SerializedName("createDate")
    private String createDate;

    @SerializedName("agedEmoImages")
    private List<AgedEmoImage> agedEmoImages = new ArrayList<>();

    public AgedEmoSaveReqDTO(Long userId, Long jellyCombId, String agedEmoName, String content, String createDate, List<AgedEmoImage> agedEmoImages) {
        this.userId = userId;
        this.jellyCombId = jellyCombId;
        this.agedEmoName = agedEmoName;
        this.content = content;
        this.createDate = createDate;
        this.agedEmoImages = agedEmoImages;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJellyCombId() {
        return jellyCombId;
    }

    public void setJellyCombId(Long jellyCombId) {
        this.jellyCombId = jellyCombId;
    }

    public String getAgedEmoName() {
        return agedEmoName;
    }

    public void setAgedEmoName(String agedEmoName) {
        this.agedEmoName = agedEmoName;
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
