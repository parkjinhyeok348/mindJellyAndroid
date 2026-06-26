package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.model
 * @description : 젤리 서랍에서 보일 jelly 정보를 가져오는 Jelly Response DTO
 * @modification : 2026-05-14(Phase2) Phase 2 — emo1Name/emo1Icon/emo2Name/emo2Icon/status 필드 추가
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 * 2026-05-14     Phase2          emo1Name, emo1Icon, emo2Name, emo2Icon, status 필드 추가 (DRAW-01, DRAW-02)
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
    @SerializedName("jellyIcon")
    private String jellyIcon;
    @SerializedName("emo1Name")
    private String emo1Name;
    @SerializedName("emo1Icon")
    private String emo1Icon;
    @SerializedName("emo2Name")
    private String emo2Name;
    @SerializedName("emo2Icon")
    private String emo2Icon;
    @SerializedName("status")
    private String status;

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

    public String getJellyIcon() {
        return jellyIcon;
    }

    public void setJellyIcon(String jellyIcon) {
        this.jellyIcon = jellyIcon;
    }

    public String getEmo1Name() {
        return emo1Name;
    }

    public void setEmo1Name(String emo1Name) {
        this.emo1Name = emo1Name;
    }

    public String getEmo1Icon() {
        return emo1Icon;
    }

    public void setEmo1Icon(String emo1Icon) {
        this.emo1Icon = emo1Icon;
    }

    public String getEmo2Name() {
        return emo2Name;
    }

    public void setEmo2Name(String emo2Name) {
        this.emo2Name = emo2Name;
    }

    public String getEmo2Icon() {
        return emo2Icon;
    }

    public void setEmo2Icon(String emo2Icon) {
        this.emo2Icon = emo2Icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
