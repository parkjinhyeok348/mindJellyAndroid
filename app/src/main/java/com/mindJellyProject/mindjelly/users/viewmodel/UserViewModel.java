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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;
import com.mindJellyProject.mindjelly.users.retrofit.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<Users> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> emailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> checkDuplicateEmailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> checkDuplicatePhoneLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> checkDuplicateNickNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    // 생성자에서 UserRepository 초기화
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 생성
    public void createUser(UserSaveReqDTO userSaveReqDTO) {
        loadingLiveData.setValue(true);
        userRepository.createUser(userSaveReqDTO).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("User creation failed");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 사용자 업데이트
    public void updateUser(Long userId, UserUpdateReqDTO reqDTO) {
        loadingLiveData.setValue(true);
        userRepository.updateUser(userId, reqDTO).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("User update failed");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 사용자 프로필 조회
    public void getUserProfile(Long userId) {
        loadingLiveData.setValue(true);
        userRepository.getUserProfile(userId).enqueue(new Callback<UserUpdateReqDTO>() {
            @Override
            public void onResponse(Call<UserUpdateReqDTO> call, Response<UserUpdateReqDTO> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    emailLiveData.setValue(response.body().getNickName());
                } else {
                    errorLiveData.setValue("User profile retrieval failed");
                }
            }

            @Override
            public void onFailure(Call<UserUpdateReqDTO> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 이메일 중복 체크
    public void checkDuplicateEmail(String email) {
        userRepository.checkDuplicateEmail(email).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    checkDuplicateEmailLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Email check failed");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 전화번호 중복 체크
    public void checkDuplicatePhoneNumber(String phoneNumber) {
        userRepository.checkDuplicateMobilePhoneNumber(phoneNumber).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    checkDuplicatePhoneLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Phone number check failed");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 닉네임 중복 체크
    public void checkDuplicateNickName(String nickName) {
        userRepository.checkDuplicateNickName(nickName).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    checkDuplicateNickNameLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Nickname check failed");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    // 로딩 상태 LiveData
    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    // 에러 메시지 LiveData
    public LiveData<String> getErrorMessage() {
        return errorLiveData;
    }

    // 사용자 LiveData
    public LiveData<Users> getUser() {
        return userLiveData;
    }

    // 이메일 LiveData
    public LiveData<String> getEmail() {
        return emailLiveData;
    }

    // 중복 이메일 체크 결과 LiveData
    public LiveData<Boolean> getCheckDuplicateEmail() {
        return checkDuplicateEmailLiveData;
    }

    // 중복 전화번호 체크 결과 LiveData
    public LiveData<Boolean> getCheckDuplicatePhoneNumber() {
        return checkDuplicatePhoneLiveData;
    }

    // 중복 닉네임 체크 결과 LiveData
    public LiveData<Boolean> getCheckDuplicateNickName() {
        return checkDuplicateNickNameLiveData;
    }
}

