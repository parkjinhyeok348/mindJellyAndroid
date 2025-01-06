package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageSaveReqDTO;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit
 * @description : AgedEmoImage Repository
 * @modification : 2025-01-06(Jinhyeok) 수정
 * @date : 2025-01-06
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-06     Jinhyeok        주석 생성
 */
public class AgedEmoImageRepository {
    private final AgedEmoImageService agedEmoImageService;

    public AgedEmoImageRepository() {
        agedEmoImageService = RetrofitClient.createService(AgedEmoImageService.class);
    }

    // AgedEmoImage 생성
    public LiveData<Resource<AgedEmoImageResDTO>> createAgedEmoImage(AgedEmoImageSaveReqDTO reqDTO) {
        MutableLiveData<Resource<AgedEmoImageResDTO>> agedEmoImageLiveData = new MutableLiveData<>();

        agedEmoImageService.createAgedEmoImage(reqDTO).enqueue(new Callback<AgedEmoImageResDTO>() {
            @Override
            public void onResponse(Call<AgedEmoImageResDTO> call, Response<AgedEmoImageResDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoImageLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoImageLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AgedEmoImageResDTO> call, Throwable t) {
                agedEmoImageLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoImageLiveData;
    }

    // AgedEmoImage 목록 조회
    public LiveData<Resource<List<AgedEmoImageResDTO>>> getAgedEmoImageList(Long agedEmoId) {
        MutableLiveData<Resource<List<AgedEmoImageResDTO>>> agedEmoImageListLiveData = new MutableLiveData<>();

        agedEmoImageService.getAgedEmoImageList(agedEmoId).enqueue(new Callback<List<AgedEmoImageResDTO>>() {
            @Override
            public void onResponse(Call<List<AgedEmoImageResDTO>> call, Response<List<AgedEmoImageResDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoImageListLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoImageListLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<AgedEmoImageResDTO>> call, Throwable t) {
                agedEmoImageListLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoImageListLiveData;
    }
}
