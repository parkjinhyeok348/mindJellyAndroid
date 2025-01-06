package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmo;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateResDTO;
import com.mindJellyProject.mindjelly.basicEmo.retrofit.BasicEmoService;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.retrofit
 * @description : AgedEmo Repository
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoRepository {
    private final AgedEmoService agedEmoService;

    public AgedEmoRepository(){
        this.agedEmoService = RetrofitClient.createService(AgedEmoService.class);
    }

    // AgedEmo 생성
    public LiveData<Resource<AgedEmo>> createAgedEmo(AgedEmoSaveReqDTO dto) {
        MutableLiveData<Resource<AgedEmo>> agedEmoLiveData = new MutableLiveData<>();

        agedEmoService.createAgedEmo(dto).enqueue(new Callback<AgedEmo>() {
            @Override
            public void onResponse(Call<AgedEmo> call, Response<AgedEmo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AgedEmo> call, Throwable t) {
                agedEmoLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoLiveData;
    }

    //AgedEmo 수정을 위한 상세 정보 출력
    public LiveData<Resource<AgedEmoUpdateResDTO>> getAgedEmoInfo(Long agedEmoId) {
        MutableLiveData<Resource<AgedEmoUpdateResDTO>> agedEmoInfoLiveData = new MutableLiveData<>();

        agedEmoService.getAgedEmoInfo(agedEmoId).enqueue(new Callback<AgedEmoUpdateResDTO>() {
            @Override
            public void onResponse(Call<AgedEmoUpdateResDTO> call, Response<AgedEmoUpdateResDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoInfoLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoInfoLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AgedEmoUpdateResDTO> call, Throwable t) {
                agedEmoInfoLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoInfoLiveData;
    }

    //AgedEmo 상세 정보 수정
    public LiveData<Resource<AgedEmo>> updateAgedEmo(Long agedEmoId, AgedEmoUpdateReqDTO reqDTO) {
        MutableLiveData<Resource<AgedEmo>> updatedAgedEmoLiveData = new MutableLiveData<>();

        agedEmoService.updateAgedEmo(agedEmoId, reqDTO).enqueue(new Callback<AgedEmo>() {
            @Override
            public void onResponse(Call<AgedEmo> call, Response<AgedEmo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatedAgedEmoLiveData.postValue(new Resource<>(response.body()));
                } else {
                    updatedAgedEmoLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AgedEmo> call, Throwable t) {
                updatedAgedEmoLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return updatedAgedEmoLiveData;
    }

    //AgedEmo Id를 통해 상세 정보 출력
    public LiveData<Resource<AgedEmoResDTO>> getAgedEmoById(Long agedEmoId) {
        MutableLiveData<Resource<AgedEmoResDTO>> agedEmoByIdLiveData = new MutableLiveData<>();

        agedEmoService.getAgedEmoById(agedEmoId).enqueue(new Callback<AgedEmoResDTO>() {
            @Override
            public void onResponse(Call<AgedEmoResDTO> call, Response<AgedEmoResDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoByIdLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoByIdLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AgedEmoResDTO> call, Throwable t) {
                agedEmoByIdLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoByIdLiveData;
    }

    // 사용자 id를 통해 젤리 뮤지엄 리스트 출력
    public LiveData<Resource<List<AgedEmoMuseumResDTO>>> getAgedEmoList(Long userId) {
        MutableLiveData<Resource<List<AgedEmoMuseumResDTO>>> agedEmoListLiveData = new MutableLiveData<>();

        agedEmoService.getAgedEmoList(userId).enqueue(new Callback<List<AgedEmoMuseumResDTO>>() {
            @Override
            public void onResponse(Call<List<AgedEmoMuseumResDTO>> call, Response<List<AgedEmoMuseumResDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agedEmoListLiveData.postValue(new Resource<>(response.body()));
                } else {
                    agedEmoListLiveData.postValue(new Resource<>("Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<AgedEmoMuseumResDTO>> call, Throwable t) {
                agedEmoListLiveData.postValue(new Resource<>(t.getMessage()));
            }
        });

        return agedEmoListLiveData;
    }
}
