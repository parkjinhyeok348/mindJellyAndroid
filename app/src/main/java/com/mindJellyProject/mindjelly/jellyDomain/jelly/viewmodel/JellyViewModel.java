package com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit.JellyRepository;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel
 * @description : Jelly ViewModel
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyViewModel extends ViewModel {
    private final JellyRepository repository;

    public JellyViewModel() {
        this.repository = new JellyRepository();
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
}

