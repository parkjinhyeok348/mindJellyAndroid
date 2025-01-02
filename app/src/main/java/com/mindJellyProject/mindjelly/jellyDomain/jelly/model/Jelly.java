package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImage;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.model
 * @description : Jelly Model
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class Jelly {
    @SerializedName("jellyId")
    private Long jellyId;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("jellyCombId")
    private Long jellyCombId;
    @SerializedName("jellyName")
    private String jellyName;
    @SerializedName("content")
    private String content;
    @SerializedName("isAging")
    private Boolean isAging;
    @SerializedName("agingPeriod")
    private String agingPeriod;
    @SerializedName("createDate")
    private String createDate;
    @SerializedName("jellyImages")
    private List<JellyImage> jellyImages;

    public Jelly(Long jellyId, Long userId, Long jellyCombId, String jellyName, String content,
                 Boolean isAging, String agingPeriod, String createDate, List<JellyImage> jellyImages) {
        this.jellyId = jellyId;
        this.userId = userId;
        this.jellyCombId = jellyCombId;
        this.jellyName = jellyName;
        this.content = content;
        this.isAging = isAging;
        this.agingPeriod = agingPeriod;
        this.createDate = createDate;
        this.jellyImages = jellyImages;
    }

    public Long getJellyId() {
        return jellyId;
    }

    public void setJellyId(Long jellyId) {
        this.jellyId = jellyId;
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

    public Boolean getAging() {
        return isAging;
    }

    public void setAging(Boolean aging) {
        isAging = aging;
    }

    public String getAgingPeriod() {
        return agingPeriod;
    }

    public void setAgingPeriod(String agingPeriod) {
        this.agingPeriod = agingPeriod;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<JellyImage> getJellyImages() {
        return jellyImages;
    }

    public void setJellyImages(List<JellyImage> jellyImages) {
        this.jellyImages = jellyImages;
    }
}
