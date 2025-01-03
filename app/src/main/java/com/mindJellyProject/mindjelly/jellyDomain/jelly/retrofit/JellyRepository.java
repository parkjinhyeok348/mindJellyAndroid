package com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateResDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit
 * @description : Jelly Repository
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyRepository {
    private final JellyService jellyService;

    public JellyRepository() {
        this.jellyService = RetrofitClient.createService(JellyService.class);
    }

    // 젤리 생성
    public LiveData<Resource<Jelly>> createJelly(JellySaveReqDTO reqDTO) {
        MutableLiveData<Resource<Jelly>> liveData = new MutableLiveData<>();
        jellyService.createJelly(reqDTO).enqueue(new Callback<Jelly>() {
            @Override
            public void onResponse(Call<Jelly> call, Response<Jelly> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(new Resource<>(response.body()));
                } else {
                    liveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Jelly> call, Throwable t) {
                liveData.postValue(new Resource<>(t.getMessage()));
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
                    liveData.postValue(new Resource<>(response.body()));
                } else {
                    liveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyUpdateResDTO> call, Throwable t) {
                liveData.postValue(new Resource<>(t.getMessage()));
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
                    liveData.postValue(new Resource<>(response.body()));
                } else {
                    liveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Jelly> call, Throwable t) {
                liveData.postValue(new Resource<>(t.getMessage()));
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
                    liveData.postValue(new Resource<>(response.body()));
                } else {
                    liveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyResDTO> call, Throwable t) {
                liveData.postValue(new Resource<>(t.getMessage()));
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
                    liveData.postValue(new Resource<>(response.body()));
                } else {
                    liveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<JellyDrawerResDTO>> call, Throwable t) {
                liveData.postValue(new Resource<>(t.getMessage()));
            }
        });
        return liveData;
    }
}

