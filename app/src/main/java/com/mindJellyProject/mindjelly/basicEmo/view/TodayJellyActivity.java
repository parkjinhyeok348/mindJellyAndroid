package com.mindJellyProject.mindjelly.basicEmo.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;
import com.mindJellyProject.mindjelly.basicEmo.viewmodel.BasicEmoViewModel;
import com.mindJellyProject.mindjelly.common.ErrorMessageUtil;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.databinding.ActivityTodayJellyBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel.JellyCombViewModel;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellySaveReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.view.JellyDrawerActivity;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

import java.time.LocalDate;
import java.util.List;

public class TodayJellyActivity extends AppCompatActivity {

    private static final String TAG = "TodayJellyActivity";
    private ActivityTodayJellyBinding binding;
    private BasicEmoViewModel viewModel;
    private BasicEmoAdapter adapter;

    // 젤리 조합을 위한 뷰모델
    private JellyCombViewModel jellyCombViewModel;
    // 젤리 저장을 위한 뷰모델 (JELLY-04)
    private JellyViewModel jellyViewModel;

    // jellyCombId 캐싱 (Pitfall 1 해결 — T-02B-02 mitigate)
    private Long cachedJellyCombId = null;

    private boolean isStep2 = false;
    private boolean twoSelected = false;
    private boolean checkingDuplicate = false;

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
        jellyCombViewModel = new ViewModelProvider(this).get(JellyCombViewModel.class);

        // 젤리 저장 뷰모델 초기화
        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);

        // 중복 체크 완료 전까지 RecyclerView 숨김
        binding.emoRecyclerView.setVisibility(View.INVISIBLE);
        binding.pbLoading.setVisibility(View.VISIBLE);

        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(BasicEmoViewModel.class);
        loadBasicEmos();
        checkingDuplicate = true;
        checkDuplicateAndProceed();

        // 초기에는 조합 젤리 박스를 INVISIBLE로 두어 상단 공간을 항상 예약 (2개 미선택 시 여백)
        binding.combinedBox.setVisibility(View.INVISIBLE);

        // isLoading observe — pbLoading 표시 및 btnSave 비활성화 (QUAL-01)
        jellyViewModel.isLoading.observe(this, loading -> {
            if (!checkingDuplicate) {
                binding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
                binding.btnSave.setClickable(!loading);
            }
        });

        // 다음 버튼 — Step 1 → Step 2 전환
        binding.btnNext.setOnClickListener(v -> showStep2());

        // 저장 버튼 클릭 리스너 (Pitfall 2 방지 — observe 중복 등록 없이 버튼 클릭 내부에서 관찰)
        binding.btnSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String diary = binding.etDiary.getText().toString().trim();

            // T-02B-01: 빈 일기 거부
            if (diary.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_diary_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            // T-02B-02: jellyCombId null 거부
            if (cachedJellyCombId == null) {
                Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
                return;
            }

            // T-02B-03: userId 검증
            long userId = SessionManager.getInstance(this).getUserId();
            if (userId == -1L) {
                Toast.makeText(this, getString(R.string.error_auth), Toast.LENGTH_SHORT).show();
                return;
            }

            // 백엔드 agingPeriod/createDate는 LocalDate(yyyy-MM-dd)이므로 날짜 문자열로 전송.
            // 숙성 기간 7일 → agingPeriod = 생성일 + 7일 (숙성 완료 날짜)
            LocalDate today = LocalDate.now();
            String createDate = today.toString();
            String agingDate = today.plusDays(7).toString();
            JellySaveReqDTO reqDTO = new JellySaveReqDTO(
                    userId,
                    cachedJellyCombId,
                    "오늘의 젤리",
                    title,
                    diary,
                    agingDate,
                    createDate,
                    null
            );

            jellyViewModel.createJelly(reqDTO).observe(this, resource -> {
                if (resource != null && resource.isSuccess()) {
                    Toast.makeText(this, getString(R.string.success_jelly_saved), Toast.LENGTH_SHORT).show();
                    jellyViewModel.isLoading.postValue(false);
                    startActivity(new Intent(this, JellyDrawerActivity.class));
                    finish();
                } else if (resource != null && resource.isError()) {
                    jellyViewModel.isLoading.postValue(false);
                    Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new BasicEmoAdapter();
        binding.emoRecyclerView.setAdapter(adapter);

        // 선택 상태 변경 리스너 등록
        adapter.setOnSelectionChangedListener(selectedEmos -> {
            if (selectedEmos.size() == 2) {
                twoSelected = true;
                binding.btnNext.setEnabled(true);
                binding.btnNext.setAlpha(1.0f);

                Long id1 = selectedEmos.get(0).getEmoId();
                Long id2 = selectedEmos.get(1).getEmoId();

                // 2개 선택 시 조합 박스를 즉시 노출 (이미지는 비동기 로드, 이름은 이미지에 포함)
                binding.combinedBox.setVisibility(View.VISIBLE);

                fetchCombinedJellyIcon(id1, id2);

                jellyCombViewModel.getJellyCombId(id1, id2).observe(this, resource -> {
                    if (resource != null && resource.isSuccess()) {
                        cachedJellyCombId = resource.getData();
                        Log.d(TAG, "jellyCombId 캐싱 완료: " + cachedJellyCombId);
                    } else if (resource != null && resource.isError()) {
                        Log.e(TAG, "jellyCombId 조회 실패: " + resource.getError());
                        cachedJellyCombId = null;
                    }
                });
            } else {
                twoSelected = false;
                binding.btnNext.setEnabled(false);
                binding.btnNext.setAlpha(0.5f);

                // 2개 미선택 시 박스를 INVISIBLE로 두어 공간은 유지하되 여백으로 표시
                binding.combinedBox.setVisibility(View.INVISIBLE);
                binding.combinedJellyImageView.setImageDrawable(null);
                cachedJellyCombId = null;
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
                    Glide.with(this)
                            .load(NetworkConfig.assetUrl(iconPath))
                            .into(binding.combinedJellyImageView);
                }
            } else if (resource != null && resource.isError()) {
                // 이미지 로드 실패 시에도 박스(조합 이름)는 유지 — 박스 노출은 선택 상태로 제어
                Log.e(TAG, "젤리 조합 아이콘 로딩 에러: " + resource.getError());
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

    private void checkDuplicateAndProceed() {
        long userId = SessionManager.getInstance(this).getUserId();
        if (userId == -1L) {
            checkingDuplicate = false;
            binding.pbLoading.setVisibility(View.GONE);
            binding.emoRecyclerView.setVisibility(View.VISIBLE);
            return;
        }
        jellyViewModel.hasTodayJelly(userId).observe(this, resource -> {
            checkingDuplicate = false;
            binding.pbLoading.setVisibility(View.GONE);
            if (resource != null && resource.isSuccess() && Boolean.TRUE.equals(resource.getData())) {
                showDuplicateDialog();
            } else {
                binding.emoRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showDuplicateDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_today_jelly_title)
                .setMessage(R.string.dialog_today_jelly_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_today_jelly_positive, (d, w) -> {
                    startActivity(new Intent(this, JellyDrawerActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.dialog_today_jelly_negative, (d, w) -> finish())
                .show();
    }

    private void showStep1() {
        isStep2 = false;
        binding.step1Container.setVisibility(View.VISIBLE);
        binding.step2Container.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.btnSave.setVisibility(View.GONE);
        // Step1 복귀 시 박스는 선택 상태로 복원 (2개면 노출, 아니면 여백)
        binding.combinedBox.setVisibility(twoSelected ? View.VISIBLE : View.INVISIBLE);
        if (twoSelected) {
            binding.btnNext.setEnabled(true);
            binding.btnNext.setAlpha(1.0f);
        }
    }

    private void showStep2() {
        isStep2 = true;
        binding.step1Container.setVisibility(View.GONE);
        binding.step2Container.setVisibility(View.VISIBLE);
        binding.btnNext.setVisibility(View.GONE);
        binding.btnSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isStep2) {
            showStep1();
        } else {
            super.onBackPressed();
        }
    }
}
