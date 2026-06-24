package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mindJellyProject.mindjelly.common.ErrorMessageUtil;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.databinding.ActivityJellyDrawerBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.view
 * @description : 젤리서랍 Activity — ViewBinding 전환, 빈 상태 UI, 로딩 인디케이터, 숙성시키기 콜백, 한국어 에러
 * @modification : 2026-05-14(Phase2-D) ViewBinding + 빈 상태(DRAW-04) + isLoading(QUAL-01) + startAging(DRAW-03) + 한국어 에러(QUAL-02)
 * @date : 2025-01-03
 *
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 * 2026-05-14     Phase2-D        ViewBinding 전환, 빈 상태/로딩/startAging/한국어 에러 완성
 */
public class JellyDrawerActivity extends AppCompatActivity {
    private static final String TAG = "JellyDrawerActivity";
    private ActivityJellyDrawerBinding binding;
    private JellyDrawerAdapter adapter;
    private JellyViewModel jellyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ViewBinding 초기화
        binding = ActivityJellyDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "onCreate: Initializing activity");

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // RecyclerView 초기화
        binding.rvJellyList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new JellyDrawerAdapter();
        binding.rvJellyList.setAdapter(adapter);

        // ViewModel 초기화
        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);

        // isLoading observe — pbLoading VISIBLE/GONE 토글 (QUAL-01)
        jellyViewModel.isLoading.observe(this, loading -> {
            binding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        // 아이템 클릭 → 상세 화면 이동
        adapter.setOnItemClickListener(jellyId -> {
            Intent intent = new Intent(JellyDrawerActivity.this, JellyDetailActivity.class);
            intent.putExtra("jellyId", jellyId);
            startActivity(intent);
        });

        SessionManager sessionManager = SessionManager.getInstance(this);
        long userId = sessionManager.getUserId();
        Log.d(TAG, "Requesting jelly list for userId: " + userId);

        // getJellyList 호출 — 로딩 시작
        jellyViewModel.isLoading.postValue(true);
        jellyViewModel.getJellyList(userId).observe(this, resource -> {
            jellyViewModel.isLoading.postValue(false);
            if (resource != null && resource.isSuccess()) {
                List<JellyDrawerResDTO> list = resource.getData();
                if (list == null || list.isEmpty()) {
                    // 빈 상태 (DRAW-04)
                    binding.rvJellyList.setVisibility(View.GONE);
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.tvEmptyStateBody.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Jelly list is empty — showing empty state");
                } else {
                    // 리스트 표시
                    binding.rvJellyList.setVisibility(View.VISIBLE);
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.tvEmptyStateBody.setVisibility(View.GONE);
                    adapter.submitList(list);
                    Log.d(TAG, "Successfully loaded " + list.size() + " jellies");
                }
            } else if (resource != null && resource.isError()) {
                // 에러 상태 — 한국어 Toast (QUAL-02)
                binding.rvJellyList.setVisibility(View.GONE);
                binding.tvEmptyState.setVisibility(View.GONE);
                binding.tvEmptyStateBody.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load jelly list: " + resource.getError());
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
