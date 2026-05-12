package com.mindJellyProject.mindjelly.basicEmo.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;
import com.mindJellyProject.mindjelly.basicEmo.viewmodel.BasicEmoViewModel;
import com.mindJellyProject.mindjelly.databinding.ActivityTodayJellyBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit.JellyCombRepository;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel.JellyCombViewModel;

import java.util.List;

public class TodayJellyActivity extends AppCompatActivity {

    private static final String TAG = "TodayJellyActivity";
    private ActivityTodayJellyBinding binding;
    private BasicEmoViewModel viewModel;
    private BasicEmoAdapter adapter;
    
    // 젤리 조합을 위한 뷰모델 추가
    private JellyCombViewModel jellyCombViewModel;
    private final String serverUrl = "http://10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTodayJellyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 젤리 조합 뷰모델 초기화
        jellyCombViewModel = new JellyCombViewModel(new JellyCombRepository());

        setupRecyclerView();

        viewModel = new BasicEmoViewModel();
        loadBasicEmos();
        
        // 초기에는 조합된 이미지 가리기
        binding.combinedJellyImageView.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new BasicEmoAdapter();
        binding.emoRecyclerView.setAdapter(adapter);

        // 선택 상태 변경 리스너 등록
        adapter.setOnSelectionChangedListener(selectedEmos -> {
            if (selectedEmos.size() == 2) {
                // 2가지 선택된 경우 조합 이미지 호출
                Long id1 = selectedEmos.get(0).getEmoId();
                Long id2 = selectedEmos.get(1).getEmoId();
                
                // 깨달음 여부는 기본적으로 false로 전달 (추후 필요시 수정)
                fetchCombinedJellyIcon(id1, id2);
            } else {
                // 2가지 미만일 경우 이미지 숨김
                binding.combinedJellyImageView.setVisibility(View.GONE);
                binding.combinedJellyImageView.setImageDrawable(null);
            }
        });
    }

    private void fetchCombinedJellyIcon(Long firstEmo, Long secondEmo) {
        Log.d(TAG, "젤리 조합 아이콘 요청 - firstEmo: " + firstEmo + ", secondEmo: " + secondEmo);
        jellyCombViewModel.getJellyIcon(firstEmo, secondEmo, false).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                String iconPath = resource.getData();
                Log.d(TAG, "젤리 조합 아이콘 요청 - iconPath: " + iconPath);
                if (iconPath != null) {
                    binding.combinedJellyImageView.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(serverUrl + iconPath)
                            .into(binding.combinedJellyImageView);
                }
            } else if (resource != null && resource.isError()) {
                Log.e(TAG, "젤리 조합 아이콘 로딩 에러: " + resource.getError());
                binding.combinedJellyImageView.setVisibility(View.GONE);
            }
        });
    }

    private void loadBasicEmos() {
        viewModel.getAllBasicEmoList().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    List<BasicEmoResDTO> emos = resource.getData();
                    if (emos != null) {
                        adapter.setEmos(emos);
                    }
                } else if (resource.isError()) {
                    String errorMsg = resource.getError();
                    Log.e(TAG, "감정 리스트 로딩 에러: " + errorMsg);
                    Toast.makeText(this, "에러: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
