package com.mindJellyProject.mindjelly.users.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.model
 * @description : User 상세정보 업데이트를 위한 Request DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class UserUpdateReqDTO {
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("profileImage")
    private String profileImage;
    @SerializedName("isMarketing")
    private Boolean isMarketing;

    public UserUpdateReqDTO(String nickName, String profileImage, Boolean isMarketing) {
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.isMarketing = isMarketing;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Boolean getMarketing() {
        return isMarketing;
    }

    public void setMarketing(Boolean marketing) {
        isMarketing = marketing;
    }
}
