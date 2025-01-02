package com.mindJellyProject.mindjelly.users.retrofit;

import com.mindJellyProject.mindjelly.users.model.FindPasswordReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserLoginReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.users.retrofit
 * @description : User Api Service
 * @modification : 2025-01-02(Jinhyeok) 수정
 * @date : 2025-01-02
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-02     Jinhyeok        주석 생성
 */
public interface UserService {
    @POST("/users")
    Call<Users> createUser(@Body UserSaveReqDTO userSaveReqDTO);

    @PUT("/users/{userId}")
    Call<Users> updateUser(@Path("userId") Long userId, @Body UserUpdateReqDTO reqDTO);

    @GET("/users/{userId}/profile")
    Call<UserUpdateReqDTO> getUserProfile(@Path("userId") Long userId);

    @GET("/users/check-email")
    Call<Boolean> checkDuplicateEmail(@Query("email") String email);

    @GET("/users/check-phone")
    Call<Boolean> checkDuplicateMobilePhoneNumber(@Query("mobilePhoneNumber") String mobilePhoneNumber);

    @GET("/users/check-nickname")
    Call<Boolean> checkDuplicateNickName(@Query("nickName") String nickName);

    @POST("/users/find-email")
    Call<String> findEmail(@Query("username") String username, @Query("mobilePhoneNumber") String mobilePhoneNumber);

    @POST("/users/find-password")
    Call<String> findPassword(@Body FindPasswordReqDTO reqDTO);

    @POST("/users/login")
    Call<String> login(@Body UserLoginReqDTO reqDTO);

    @DELETE("/users/{userId}")
    Call<Void> deleteUser(@Path("userId") Long userId);
}
