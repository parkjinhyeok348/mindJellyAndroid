package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model
 * @description : AgedEmo 상세 정보를 출력하기 위한 AgedEmo Response DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoResDTO {
    @SerializedName("agedEmoId")
    private Long agedEmoId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("jellyCombId")
    private Long jellyCombId;

    @SerializedName("agedEmoName")
    private String agedEmoName;

    @SerializedName("content")
    private String content; // 젤리에 남길 메모

    @SerializedName("createDate")
    private String createDate; // 생성 날짜를 문자열로 직렬화

    @SerializedName("agedEmoImages")
    private List<AgedEmoImage> agedEmoImages = new ArrayList<>(); // 젤리에 들어갈 사진 리스트

    public AgedEmoResDTO(Long agedEmoId, Long userId, Long jellyCombId, String agedEmoName, String content, String createDate, List<AgedEmoImage> agedEmoImages) {
        this.agedEmoId = agedEmoId;
        this.userId = userId;
        this.jellyCombId = jellyCombId;
        this.agedEmoName = agedEmoName;
        this.content = content;
        this.createDate = createDate;
        this.agedEmoImages = agedEmoImages;
    }

    public Long getAgedEmoId() {
        return agedEmoId;
    }

    public void setAgedEmoId(Long agedEmoId) {
        this.agedEmoId = agedEmoId;
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
