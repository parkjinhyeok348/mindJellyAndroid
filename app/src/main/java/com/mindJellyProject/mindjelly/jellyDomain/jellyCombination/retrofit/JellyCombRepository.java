package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit;


import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit
 * @description : Jelly Combination Repository
 * @modification : 2026-05-14(Phase2) getJellyCombId 메서드 추가 (Pitfall 1 해결)
 * @date : 2025-01-03
 */

public class JellyCombRepository {

    private final JellyCombService jellyCombService;

    public JellyCombRepository(Context context) {
        this.jellyCombService = RetrofitClient.createService(JellyCombService.class, context);
    }

    // 젤리 조합 id로 젤리 조합 정보 호출
    public LiveData<Resource<JellyCombResDTO>> getJellyCombById(Long jellyCombId) {
        MutableLiveData<Resource<JellyCombResDTO>> jellyCombLiveData = new MutableLiveData<>();
        jellyCombService.getJellyCombById(jellyCombId).enqueue(new Callback<JellyCombResDTO>() {
            @Override
            public void onResponse(Call<JellyCombResDTO> call, Response<JellyCombResDTO> response) {
                if (response.isSuccessful()) {
                    jellyCombLiveData.postValue(Resource.success(response.body()));
                } else {
                    jellyCombLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyCombResDTO> call, Throwable t) {
                jellyCombLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return jellyCombLiveData;
    }

    // 감정 2가지와 깨달음 여부로 아이콘 이미지 호출
    public LiveData<Resource<String>> getJellyIcon(Long firstEmo, Long secondEmo, Boolean isAwaken) {
        MutableLiveData<Resource<String>> jellyIconLiveData = new MutableLiveData<>();
        jellyCombService.getJellyIcon(firstEmo, secondEmo, isAwaken).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // String 타입 데이터를 명확하게 '성공' 데이터로 전달
                    jellyIconLiveData.postValue(Resource.success(response.body()));
                } else {
                    jellyIconLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                jellyIconLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return jellyIconLiveData;
    }

    /**
     * 두 감정 조합으로 jellyCombId를 조회한다 (RESEARCH.md Pitfall 1 해결).
     * JellySaveReqDTO.jellyCombId 필드를 채우기 위해 TodayJellyActivity에서 사용.
     */
    public LiveData<Resource<Long>> getJellyCombId(Long firstEmo, Long secondEmo) {
        MutableLiveData<Resource<Long>> jellyCombIdLiveData = new MutableLiveData<>();
        jellyCombService.getJellyCombId(firstEmo, secondEmo).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jellyCombIdLiveData.postValue(Resource.success(response.body()));
                } else {
                    jellyCombIdLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                jellyCombIdLiveData.postValue(Resource.error("Error: " + t.getMessage()));
            }
        });
        return jellyCombIdLiveData;
    }
}
