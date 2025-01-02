package com.mindJellyProject.mindjelly.users.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.model
 * @description : 비밀번호 찾기에 사용되는 Request DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class FindPasswordReqDTO {

    @SerializedName("email")
    private String email;
    @SerializedName("userName")
    private String userName;
    @SerializedName("mobilePhoneNumber")
    private String mobilePhoneNumber;

    public FindPasswordReqDTO(String email, String userName, String mobilePhoneNumber) {
        this.email = email;
        this.userName = userName;
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }
}
