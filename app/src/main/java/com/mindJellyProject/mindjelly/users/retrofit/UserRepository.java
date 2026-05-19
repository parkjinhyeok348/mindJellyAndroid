package com.mindJellyProject.mindjelly.users.retrofit;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.users.model.FindPasswordReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserLoginReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.retrofit
 * @description : User Repository
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 */
public class UserRepository {

    private final UserService userService;

    public UserRepository(Context context) {
        this.userService = RetrofitClient.createService(UserService.class, context);
    }

    // 사용자 생성
    public LiveData<Resource<Users>> createUser(UserSaveReqDTO userSaveReqDTO) {
        MutableLiveData<Resource<Users>> resultLiveData = new MutableLiveData<>();

        userService.createUser(userSaveReqDTO).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                resultLiveData.postValue(Resource.error(t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 사용자 업데이트
    public LiveData<Resource<Users>> updateUser(Long userId, UserUpdateReqDTO reqDTO) {
        MutableLiveData<Resource<Users>> resultLiveData = new MutableLiveData<>();
        userService.updateUser(userId, reqDTO).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 사용자 프로필 조회
    public LiveData<Resource<UserUpdateReqDTO>> getUserProfile(Long userId) {
        MutableLiveData<Resource<UserUpdateReqDTO>> resultLiveData = new MutableLiveData<>();
        userService.getUserProfile(userId).enqueue(new Callback<UserUpdateReqDTO>() {
            @Override
            public void onResponse(Call<UserUpdateReqDTO> call, Response<UserUpdateReqDTO> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<UserUpdateReqDTO> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 이메일 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateEmail(String email) {
        MutableLiveData<Resource<Boolean>> resultLiveData = new MutableLiveData<>();
        userService.checkDuplicateEmail(email).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error(getLoginErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 닉네임 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateNickName(String nickName) {
        MutableLiveData<Resource<Boolean>> resultLiveData = new MutableLiveData<>();
        userService.checkDuplicateNickName(nickName).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 휴대폰 번호 중복 체크
    public LiveData<Resource<Boolean>> checkDuplicateMobilePhoneNumber(String mobilePhoneNumber) {
        MutableLiveData<Resource<Boolean>> resultLiveData = new MutableLiveData<>();
        userService.checkDuplicateMobilePhoneNumber(mobilePhoneNumber).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 이메일 찾기
    public LiveData<Resource<String>> findEmail(String username, String mobilePhoneNumber) {
        MutableLiveData<Resource<String>> resultLiveData = new MutableLiveData<>();
        userService.findEmail(username, mobilePhoneNumber).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 비밀번호 찾기
    public LiveData<Resource<String>> findPassword(FindPasswordReqDTO reqDTO) {
        MutableLiveData<Resource<String>> resultLiveData = new MutableLiveData<>();
        userService.findPassword(reqDTO).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 로그인
    public LiveData<Resource<String>> login(UserLoginReqDTO reqDTO) {
        MutableLiveData<Resource<String>> resultLiveData = new MutableLiveData<>();
        userService.login(reqDTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();
                        resultLiveData.postValue(Resource.success(token));
                    } catch (IOException e) {
                        resultLiveData.postValue(Resource.error("토큰 파싱 실패: " + e.getMessage()));
                    }
                } else {
                    resultLiveData.postValue(Resource.error(getLoginErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    private String getLoginErrorMessage(Response<?> response) {
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            try {
                String body = errorBody.string();
                if (body != null && !body.trim().isEmpty()) {
                    return body;
                }
            } catch (IOException ignored) {
                // Fall through to Retrofit response message.
            }
        }
        return "Error: " + response.message();
    }

    // 사용자 삭제
    public LiveData<Resource<Void>> deleteUser(Long userId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        userService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(null));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }
}
