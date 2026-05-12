package com.mindJellyProject.mindjelly.jellyDomain.jellyImage.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.model.JellyImageSaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyImage.retrofit.JellyImageRepository;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyImage.viewmodel
 * @description :Jelly Image ViewModel
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyImageViewModel extends AndroidViewModel {

    private final JellyImageRepository repository;

    public JellyImageViewModel(Application application) {
        super(application);
        this.repository = new JellyImageRepository(application.getApplicationContext());
    }

    public LiveData<Resource<JellyImageResDTO>> createJellyImage(JellyImageSaveReqDTO reqDTO) {
        return repository.createJellyImage(reqDTO);
    }

    public LiveData<Resource<List<JellyImageResDTO>>> getJellyImageList(Long jellyId) {
        return repository.getJellyImageList(jellyId);
    }
}
