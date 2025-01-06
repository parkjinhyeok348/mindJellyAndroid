package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmo;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoUpdateResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.retrofit.AgedEmoRepository;
import com.mindJellyProject.mindjelly.common.Resource;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel
 * @description : AgedEmo ViewModel
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class AgedEmoViewModel extends ViewModel {

    private final AgedEmoRepository repository;

    public AgedEmoViewModel(AgedEmoRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<AgedEmo>> createAgedEmo(AgedEmoSaveReqDTO dto) {
        return repository.createAgedEmo(dto);
    }

    public LiveData<Resource<AgedEmoUpdateResDTO>> getAgedEmoInfo(Long agedEmoId) {
        return repository.getAgedEmoInfo(agedEmoId);
    }

    public LiveData<Resource<AgedEmo>> updateAgedEmo(Long agedEmoId, AgedEmoUpdateReqDTO reqDTO) {
        return repository.updateAgedEmo(agedEmoId, reqDTO);
    }

    public LiveData<Resource<AgedEmoResDTO>> getAgedEmoById(Long agedEmoId) {
        return repository.getAgedEmoById(agedEmoId);
    }

    public LiveData<Resource<List<AgedEmoMuseumResDTO>>> getAgedEmoList(Long userId) {
        return repository.getAgedEmoList(userId);
    }
}
