package com.mindJellyProject.mindjelly.basicEmo.viewmodel;

import androidx.lifecycle.LiveData;

import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;
import com.mindJellyProject.mindjelly.basicEmo.retrofit.BasicEmoRepository;
import com.mindJellyProject.mindjelly.common.Resource;

import java.util.List;

/**
 * @className : com.mindJellyProject.mindjelly.basicEmo.viewmodel
 * @description : BasicEmo ViewModel
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @author : Jinhyeok
 * @date : 2025-01-03
 *
    ====개정이력(Modification Information)====
        수정일        수정자        수정내용
    -----------------------------------------
      2025-01-03     Jinhyeok        주석 생성

 */
public class BasicEmoViewModel {
     private final BasicEmoRepository repository;

     public BasicEmoViewModel(){
         this.repository = new BasicEmoRepository();
     }

     public LiveData<Resource<BasicEmoResDTO>> getBasicEmoById(Long emoId){
         return repository.getBasicEmoById(emoId);
     }
    public LiveData<Resource<List<BasicEmoResDTO>>> getAllBasicEmoList(){
         return repository.getAllBasicEmoList();
    }

}
