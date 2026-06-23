package com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyStartAgingReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit.JellyRepository;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel
 * @description : Jelly ViewModel
 * @modification : 2026-05-14(Phase2) isLoading MutableLiveData + startAging 메서드 추가 (QUAL-01, DRAW-03)
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 * 2026-05-14     Phase2          isLoading LiveData<Boolean> + startAging(Long) 추가
 */
public class JellyViewModel extends AndroidViewModel {
    private final JellyRepository repository;

    /**
     * 네트워크 요청 중 로딩 상태 (QUAL-01).
     * Activity에서 observe하여 ProgressBar visibility를 제어한다.
     * Resource<T>에 LOADING 상태를 추가하지 않고 별도 필드로 관리 — 기존 observe 코드 파괴 없음.
     */
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public JellyViewModel(Application application) {
        super(application);
        this.repository = new JellyRepository(application.getApplicationContext());
    }

    public LiveData<Resource<Jelly>> createJelly(JellySaveReqDTO reqDTO) {
        return repository.createJelly(reqDTO);
    }

    public LiveData<Resource<JellyUpdateResDTO>> getJellyInfo(Long jellyId) {
        return repository.getJellyInfo(jellyId);
    }

    public LiveData<Resource<Jelly>> updateJelly(Long jellyId, JellyUpdateReqDTO reqDTO) {
        return repository.updateJelly(jellyId, reqDTO);
    }

    public LiveData<Resource<JellyResDTO>> getJellyById(Long jellyId) {
        return repository.getJellyById(jellyId);
    }

    public LiveData<Resource<List<JellyDrawerResDTO>>> getJellyList(Long userId) {
        return repository.getJellyList(userId);
    }

    /**
     * 숙성 시작 요청 (DRAW-03).
     * 호출 즉시 isLoading을 true로 설정한다.
     * 호출자(Activity)가 반환된 LiveData를 observe하여
     * 완료/실패 시 isLoading.postValue(false)를 처리한다.
     *
     * @param jellyId 숙성 시작할 젤리 ID
     * @return LiveData<Resource<ResponseBody>> — isSuccessful()이면 에이징룸으로 이동
     */
    public LiveData<Resource<ResponseBody>> startAging(Long jellyId) {
        isLoading.postValue(true);
        return repository.startAging(jellyId, new JellyStartAgingReqDTO());
    }

    // isLoading 관리 불필요 — Activity가 pbLoading을 직접 제어함
    public LiveData<Resource<Boolean>> hasTodayJelly(Long userId) {
        return repository.hasTodayJelly(userId);
    }
}
