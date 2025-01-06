package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.viewmodel;

import androidx.lifecycle.LiveData;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit.AgedEmoImageRepository;
import com.mindJellyProject.mindjelly.common.Resource;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.viewmodel
 * @description : AgedEmoImage ViewModel
 * @modification : 2025-01-06(Jinhyeok) 수정
 * @date : 2025-01-06
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-06     Jinhyeok        주석 생성
 */
public class AgedEmoImageViewModel {
    private final AgedEmoImageRepository repository;

    public AgedEmoImageViewModel(AgedEmoImageRepository repository) {
        this.repository = repository;
    }

    // AgedEmoImage 생성
    public LiveData<Resource<AgedEmoImageResDTO>> createAgedEmoImage(AgedEmoImageSaveReqDTO reqDTO) {
        return repository.createAgedEmoImage(reqDTO);
    }

    // AgedEmoImage 목록 조회
    public LiveData<Resource<List<AgedEmoImageResDTO>>> getAgedEmoImageList(Long agedEmoId) {
        return repository.getAgedEmoImageList(agedEmoId);
    }
}
