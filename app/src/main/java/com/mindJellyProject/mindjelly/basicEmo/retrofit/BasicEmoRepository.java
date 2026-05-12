package com.mindJellyProject.mindjelly.basicEmo.retrofit;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.basicEmo.retrofit
 * @description : BasicEmo Repository
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 */
public class BasicEmoRepository {

    private final BasicEmoService basicEmoService;

    public BasicEmoRepository(Context context){
        this.basicEmoService = RetrofitClient.createService(BasicEmoService.class, context);
    }

    // 기본 감정 ID로 조회
    public LiveData<Resource<BasicEmoResDTO>> getBasicEmoById(Long emoId) {
        MutableLiveData<Resource<BasicEmoResDTO>> resultLiveData = new MutableLiveData<>();

        basicEmoService.getBasicEmoById(emoId).enqueue(new Callback<BasicEmoResDTO>() {
            @Override
            public void onResponse(Call<BasicEmoResDTO> call, Response<BasicEmoResDTO> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<BasicEmoResDTO> call, Throwable t) {
                resultLiveData.postValue(Resource.error(t.getMessage()));
            }
        });

        return resultLiveData;
    }

    // 모든 기본 감정 리스트 조회
    public LiveData<Resource<List<BasicEmoResDTO>>> getAllBasicEmoList() {
        MutableLiveData<Resource<List<BasicEmoResDTO>>> resultLiveData = new MutableLiveData<>();

        basicEmoService.getAllBasicEmoList().enqueue(new Callback<List<BasicEmoResDTO>>() {
            @Override
            public void onResponse(Call<List<BasicEmoResDTO>> call, Response<List<BasicEmoResDTO>> response) {
                if (response.isSuccessful()) {
                    resultLiveData.postValue(Resource.success(response.body()));
                } else {
                    resultLiveData.postValue(Resource.error("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<BasicEmoResDTO>> call, Throwable t) {
                resultLiveData.postValue(Resource.error(t.getMessage()));
            }
        });

        return resultLiveData; // 결과를 LiveData로 반환
    }
}
