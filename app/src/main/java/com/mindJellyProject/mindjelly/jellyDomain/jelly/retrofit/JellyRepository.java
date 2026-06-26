package com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.RepositoryError;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.common.TodayJellyFilter;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyStartAgingReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateResDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit
 * @description : Jelly Repository
 * @modification : 2026-05-14(Phase2) startAging 메서드 추가 (DRAW-03)
 * @date : 2025-01-03
 */
public class JellyRepository {
    private final JellyService jellyService;

    public JellyRepository(Context context) {
        this.jellyService = RetrofitClient.createService(JellyService.class, context);
    }

    // 젤리 생성
    public LiveData<Resource<Jelly>> createJelly(JellySaveReqDTO reqDTO) {
        MutableLiveData<Resource<Jelly>> liveData = new MutableLiveData<>();
        jellyService.createJelly(reqDTO).enqueue(new Callback<Jelly>() {
            @Override
            public void onResponse(Call<Jelly> call, Response<Jelly> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Jelly> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 젤리 정보 출력
    public LiveData<Resource<JellyUpdateResDTO>> getJellyInfo(Long jellyId) {
        MutableLiveData<Resource<JellyUpdateResDTO>> liveData = new MutableLiveData<>();
        jellyService.getJellyInfo(jellyId).enqueue(new Callback<JellyUpdateResDTO>() {
            @Override
            public void onResponse(Call<JellyUpdateResDTO> call, Response<JellyUpdateResDTO> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyUpdateResDTO> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 젤리 정보 수정
    public LiveData<Resource<Jelly>> updateJelly(Long jellyId, JellyUpdateReqDTO reqDTO) {
        MutableLiveData<Resource<Jelly>> liveData = new MutableLiveData<>();
        jellyService.updateJelly(jellyId, reqDTO).enqueue(new Callback<Jelly>() {
            @Override
            public void onResponse(Call<Jelly> call, Response<Jelly> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Jelly> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 젤리 삭제
    public LiveData<Resource<ResponseBody>> deleteJelly(Long jellyId) {
        MutableLiveData<Resource<ResponseBody>> liveData = new MutableLiveData<>();
        jellyService.deleteJelly(jellyId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // JellyId로 젤리 상세 정보 출력
    public LiveData<Resource<JellyResDTO>> getJellyById(Long jellyId) {
        MutableLiveData<Resource<JellyResDTO>> liveData = new MutableLiveData<>();
        jellyService.getJellyById(jellyId).enqueue(new Callback<JellyResDTO>() {
            @Override
            public void onResponse(Call<JellyResDTO> call, Response<JellyResDTO> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyResDTO> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 젤리 서랍에 보일 젤리 리스트 출력
    public LiveData<Resource<List<JellyDrawerResDTO>>> getJellyList(Long userId) {
        MutableLiveData<Resource<List<JellyDrawerResDTO>>> liveData = new MutableLiveData<>();
        jellyService.getJellyList(userId).enqueue(new Callback<List<JellyDrawerResDTO>>() {
            @Override
            public void onResponse(Call<List<JellyDrawerResDTO>> call, Response<List<JellyDrawerResDTO>> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<JellyDrawerResDTO>> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 숙성 시작 — PATCH /api/jelly/{jellyId} (D-03, DRAW-03)
    public LiveData<Resource<ResponseBody>> startAging(Long jellyId, JellyStartAgingReqDTO reqDTO) {
        MutableLiveData<Resource<ResponseBody>> liveData = new MutableLiveData<>();
        jellyService.startAging(jellyId, reqDTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }

    // 오늘 젤리 존재 여부 확인 — GET /jelly/user/{userId} 재활용, 클라이언트에서 날짜 필터링
    public LiveData<Resource<Boolean>> hasTodayJelly(Long userId) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        jellyService.getJellyList(userId).enqueue(new Callback<List<JellyDrawerResDTO>>() {
            @Override
            public void onResponse(Call<List<JellyDrawerResDTO>> call, Response<List<JellyDrawerResDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Resource.success(TodayJellyFilter.containsToday(response.body(), today)));
                } else {
                    liveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<JellyDrawerResDTO>> call, Throwable t) {
                liveData.postValue(Resource.error(RepositoryError.message(t)));
            }
        });
        return liveData;
    }
}
