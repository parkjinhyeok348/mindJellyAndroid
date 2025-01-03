package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit.JellyService;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit
 * @description : Jelly Combination Repository
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03

 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */

public class JellyCombRepository {

    private final JellyCombService jellyCombService;

    public JellyCombRepository() {
        this.jellyCombService = RetrofitClient.createService(JellyCombService.class);
    }

    // 젤리 조합 id로 젤리 조합 정보 호출
    public LiveData<Resource<JellyCombResDTO>> getJellyCombById(Long jellyCombId) {
        MutableLiveData<Resource<JellyCombResDTO>> jellyCombLiveData = new MutableLiveData<>();
        jellyCombService.getJellyCombById(jellyCombId).enqueue(new Callback<JellyCombResDTO>() {
            @Override
            public void onResponse(Call<JellyCombResDTO> call, Response<JellyCombResDTO> response) {
                if (response.isSuccessful()) {
                    jellyCombLiveData.postValue(new Resource<>(response.body()));
                } else {
                    jellyCombLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyCombResDTO> call, Throwable t) {
                jellyCombLiveData.postValue(new Resource<>("Error: " + t.getMessage()));
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
                    jellyIconLiveData.postValue(new Resource<>(response.body()));
                } else {
                    jellyIconLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                jellyIconLiveData.postValue(new Resource<>("Error: " + t.getMessage()));
            }
        });
        return jellyIconLiveData;
    }
}

