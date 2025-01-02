package com.mindJellyProject.mindjelly.users.retrofit;

import com.mindJellyProject.mindjelly.users.model.FindPasswordReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserLoginReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;

import retrofit2.Call;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.retrofit
 * @description : User Repository
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public class UserRepository {

    private final UserService userService;

    // 생성자에서 UserService 초기화
    public UserRepository(UserService userService) {
        this.userService = userService;
    }

    // 사용자 생성
    public Call<Users> createUser(UserSaveReqDTO userSaveReqDTO) {
        return userService.createUser(userSaveReqDTO);
    }

    // 사용자 업데이트
    public Call<Users> updateUser(Long userId, UserUpdateReqDTO reqDTO) {
        return userService.updateUser(userId, reqDTO);
    }

    // 사용자 프로필 조회
    public Call<UserUpdateReqDTO> getUserProfile(Long userId) {
        return userService.getUserProfile(userId);
    }

    // 이메일 중복 체크
    public Call<Boolean> checkDuplicateEmail(String email) {
        return userService.checkDuplicateEmail(email);
    }

    // 전화번호 중복 체크
    public Call<Boolean> checkDuplicateMobilePhoneNumber(String mobilePhoneNumber) {
        return userService.checkDuplicateMobilePhoneNumber(mobilePhoneNumber);
    }

    // 닉네임 중복 체크
    public Call<Boolean> checkDuplicateNickName(String nickName) {
        return userService.checkDuplicateNickName(nickName);
    }

    // 이메일 찾기
    public Call<String> findEmail(String username, String mobilePhoneNumber) {
        return userService.findEmail(username, mobilePhoneNumber);
    }

    // 비밀번호 찾기
    public Call<String> findPassword(FindPasswordReqDTO reqDTO) {
        return userService.findPassword(reqDTO);
    }

    // 로그인
    public Call<String> login(UserLoginReqDTO reqDTO) {
        return userService.login(reqDTO);
    }

    // 사용자 삭제
    public Call<Void> deleteUser(Long userId) {
        return userService.deleteUser(userId);
    }
}

