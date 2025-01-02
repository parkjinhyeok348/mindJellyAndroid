package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model
 * @description : 젤리 서랍에서 보일 jelly 정보를 가져오는 Jelly Response DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class JellyDrawerResDTO {
    @SerializedName("jellyId")
    private Long jellyId;
    @SerializedName("jellyCombId")
    private Long jellyCombId;
    @SerializedName("isAging")
    private Boolean isAging;
    @SerializedName("createDate")
    private String createDate;

    public JellyDrawerResDTO(Long jellyId, Long jellyCombId, Boolean isAging, String createDate) {
        this.jellyId = jellyId;
        this.jellyCombId = jellyCombId;
        this.isAging = isAging;
        this.createDate = createDate;
    }

    public Long getJellyId() {
        return jellyId;
    }

    public void setJellyId(Long jellyId) {
        this.jellyId = jellyId;
    }

    public Long getJellyCombId() {
        return jellyCombId;
    }

    public void setJellyCombId(Long jellyCombId) {
        this.jellyCombId = jellyCombId;
    }

    public Boolean getAging() {
        return isAging;
    }

    public void setAging(Boolean aging) {
        isAging = aging;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
