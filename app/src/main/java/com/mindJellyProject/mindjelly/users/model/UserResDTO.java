package com.mindJellyProject.mindjelly.users.model;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.model
 * @description : User의 상세 정보를 받아오는 User Response DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmo;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;

import java.util.List;

public class UserResDTO {

    @SerializedName("userId")
    private Long userId;

    @SerializedName("mobilePhoneNumber")
    private String mobilePhoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("userName")
    private String userName;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("gender")
    private Boolean gender;

    @SerializedName("birthDate")
    private String birthDate;

    @SerializedName("profileImage")
    private String profileImage;

    @SerializedName("point")
    private int point;

    @SerializedName("ageRange")
    private String ageRange;

    @SerializedName("isMarketing")
    private Boolean isMarketing;

    @SerializedName("jellyList")
    private List<Jelly> jellyList;

    @SerializedName("agedEmoList")
    private List<AgedEmo> agedEmoList;

    public UserResDTO(Long userId, String mobilePhoneNumber, String email, String password, String userName, String nickName,
                      Boolean gender, String birthDate, String profileImage, int point, String ageRange, Boolean isMarketing,
                      List<Jelly> jellyList, List<AgedEmo> agedEmoList) {
        this.userId = userId;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.point = point;
        this.ageRange = ageRange;
        this.isMarketing = isMarketing;
        this.jellyList = jellyList;
        this.agedEmoList = agedEmoList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Boolean getIsMarketing() {
        return isMarketing;
    }

    public void setIsMarketing(Boolean isMarketing) {
        this.isMarketing = isMarketing;
    }

    public List<Jelly> getJellyList() {
        return jellyList;
    }

    public void setJellyList(List<Jelly> jellyList) {
        this.jellyList = jellyList;
    }

    public List<AgedEmo> getAgedEmoList() {
        return agedEmoList;
    }

    public void setAgedEmoList(List<AgedEmo> agedEmoList) {
        this.agedEmoList = agedEmoList;
    }
}
