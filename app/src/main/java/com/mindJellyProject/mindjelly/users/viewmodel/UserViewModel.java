package com.mindJellyProject.mindjelly.users.viewmodel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.viewmodel
 * @description : User ViewModel
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02

 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.users.model.FindPasswordReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserLoginReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;
import com.mindJellyProject.mindjelly.users.retrofit.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    public UserViewModel() {
        this.userRepository = new UserRepository();
    }

    // 사용자 생성
    public LiveData<Resource<Users>> createUser(UserSaveReqDTO userSaveReqDTO) {
        return userRepository.createUser(userSaveReqDTO);
    }

    // 사용자 업데이트
    public LiveData<Resource<Users>> updateUser(Long userId, UserUpdateReqDTO reqDTO) {
        return userRepository.updateUser(userId, reqDTO);
    }

    // 사용자 프로필 조회
    public LiveData<Resource<UserUpdateReqDTO>> getUserProfile(Long userId) {
        return userRepository.getUserProfile(userId);
    }

    // 이메일 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateEmail(String email) {
        return userRepository.checkDuplicateEmail(email);
    }

    // 전화번호 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateMobilePhoneNumber(String mobilePhoneNumber) {
        return userRepository.checkDuplicateMobilePhoneNumber(mobilePhoneNumber);
    }

    // 닉네임 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateNickName(String nickName) {
        return userRepository.checkDuplicateNickName(nickName);
    }

    // 이메일 찾기
    public LiveData<Resource<String>> findEmail(String username, String mobilePhoneNumber) {
        return userRepository.findEmail(username, mobilePhoneNumber);
    }

    // 비밀번호 찾기
    public LiveData<Resource<String>> findPassword(FindPasswordReqDTO reqDTO) {
        return userRepository.findPassword(reqDTO);
    }

    // 로그인
    public LiveData<Resource<String>> login(UserLoginReqDTO reqDTO) {
        return userRepository.login(reqDTO);
    }

    // 사용자 삭제
    public LiveData<Resource<Void>> deleteUser(Long userId) {
        return userRepository.deleteUser(userId);
    }
}

