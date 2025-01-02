package com.mindJellyProject.mindjelly.users.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.model
 * @description : 로그인에 사용되는 User Request DTO
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class UserLoginReqDTO {
    @SerializedName("email")
    private String email; // 이메일

    @SerializedName("password")
    private String password; // 비밀번호

    public UserLoginReqDTO(String email, String password) {
        this.email = email;
        this.password = password;
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
}
