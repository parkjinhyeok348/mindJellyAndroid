package com.mindJellyProject.mindjelly.jellyDomain.jellyImage.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageSaveReqDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyImage.retrofit
 * @description : JellyImage Repository
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyImageRepository {

    private final JellyImageService jellyImageService;

    public JellyImageRepository() {
        this.jellyImageService = RetrofitClient.createService(JellyImageService.class);
    }

    // 젤리 이미지 생성
    public LiveData<Resource<JellyImageResDTO>> createJellyImage(JellyImageSaveReqDTO reqDTO) {
        MutableLiveData<Resource<JellyImageResDTO>> resultLiveData = new MutableLiveData<>();
        jellyImageService.createJellyImage(reqDTO).enqueue(new Callback<JellyImageResDTO>() {
            @Override
            public void onResponse(Call<JellyImageResDTO> call, Response<JellyImageResDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(new Resource<>(response.body()));
                } else {
                    resultLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JellyImageResDTO> call, Throwable t) {
                resultLiveData.postValue(new Resource<>("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }

    // 젤리 id를 통해 젤리 이미지 리스트 호출
    public LiveData<Resource<List<JellyImageResDTO>>> getJellyImageList(Long jellyId) {
        MutableLiveData<Resource<List<JellyImageResDTO>>> resultLiveData = new MutableLiveData<>();
        jellyImageService.getJellyImageList(jellyId).enqueue(new Callback<List<JellyImageResDTO>>() {
            @Override
            public void onResponse(Call<List<JellyImageResDTO>> call, Response<List<JellyImageResDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(new Resource<>(response.body()));
                } else {
                    resultLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<JellyImageResDTO>> call, Throwable t) {
                resultLiveData.postValue(new Resource<>("Error: " + t.getMessage()));
            }
        });
        return resultLiveData;
    }
}
