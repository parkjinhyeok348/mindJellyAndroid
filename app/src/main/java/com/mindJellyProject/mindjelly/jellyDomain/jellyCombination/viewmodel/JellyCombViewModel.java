package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel
 * @description :
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit.JellyCombRepository;

public class JellyCombViewModel extends ViewModel {

    private final JellyCombRepository repository;

    public JellyCombViewModel(JellyCombRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<JellyCombResDTO>> getJellyCombById(Long jellyCombId) {
        return repository.getJellyCombById(jellyCombId);
    }

    public LiveData<Resource<String>> getJellyIcon(Long firstEmo, Long secondEmo, Boolean isAwaken) {
        return repository.getJellyIcon(firstEmo, secondEmo, isAwaken);
    }
}

