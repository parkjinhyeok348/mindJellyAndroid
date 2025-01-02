package com.mindJellyProject.mindjelly.users.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.model
 * @description : User 생성 Request DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class UserSaveReqDTO {
    @SerializedName("mobilePhoneNumber")
    private String mobilePhoneNumber; // 전화번호

    @SerializedName("email")
    private String email; // 이메일

    @SerializedName("password")
    private String password; // 비밀번호

    @SerializedName("userName")
    private String userName; // 실명

    @SerializedName("nickName")
    private String nickName; // 닉네임

    @SerializedName("gender")
    private Boolean gender; // 성별

    @SerializedName("birthDate")
    private String birthDate; // 생일날짜

    @SerializedName("profileImage")
    private String profileImage; // 프로필 이미지

    @SerializedName("ageRange")
    private String ageRange; // 연령대

    @SerializedName("isMarketing")
    private Boolean isMarketing; // 마케팅 정보 수신 동의

    public UserSaveReqDTO(String mobilePhoneNumber, String email, String password, String userName, String nickName,
                          Boolean gender, String birthDate, String profileImage, String ageRange, Boolean isMarketing) {
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.ageRange = ageRange;
        this.isMarketing = isMarketing;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Boolean getMarketing() {
        return isMarketing;
    }

    public void setMarketing(Boolean marketing) {
        isMarketing = marketing;
    }
}
