package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel.AgedEmoViewModel;
import com.mindJellyProject.mindjelly.common.AgingCheckWorker;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel.JellyCombViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AgingCompleteActivity extends AppCompatActivity {

    public static final String EXTRA_JELLY_ID      = "extra_jelly_id";
    public static final String EXTRA_JELLY_COMB_ID = "extra_jelly_comb_id";
    public static final String EXTRA_EMO1_NAME     = "extra_emo1_name";
    public static final String EXTRA_EMO2_NAME     = "extra_emo2_name";
    public static final String EXTRA_CREATE_DATE   = "extra_create_date";

    private AgedEmoViewModel agedEmoViewModel;
    private JellyCombViewModel jellyCombViewModel;
    private JellyViewModel jellyViewModel;
    private ImageView ivCombo;
    private EditText etContent;
    private ProgressBar progressBar;
    private Button btnSubmit;

    private long jellyId;
    private long jellyCombId;
    private String createDate;
    private String agedEmoName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging_complete);

        jellyId     = getIntent().getLongExtra(EXTRA_JELLY_ID, -1L);
        jellyCombId = getIntent().getLongExtra(EXTRA_JELLY_COMB_ID, -1L);
        createDate  = getIntent().getStringExtra(EXTRA_CREATE_DATE);

        agedEmoViewModel   = new ViewModelProvider(this).get(AgedEmoViewModel.class);
        jellyCombViewModel = new ViewModelProvider(this).get(JellyCombViewModel.class);
        jellyViewModel     = new ViewModelProvider(this).get(JellyViewModel.class);

        ivCombo     = findViewById(R.id.iv_combo);
        etContent   = findViewById(R.id.et_aged_content);
        progressBar = findViewById(R.id.progress_submit);
        btnSubmit   = findViewById(R.id.btn_submit_aged_diary);

        TextView tvDate = findViewById(R.id.tv_aging_complete_date);
        try {
            tvDate.setText("숙성 완료일: " + LocalDate.parse(createDate).plusDays(7));
        } catch (Exception e) {
            tvDate.setText("숙성 완료");
        }

        loadJellyTitle();
        loadComboImage();

        btnSubmit.setOnClickListener(v -> submitDiary());
    }

    // 원본 제목을 숙성된 감정 이름으로 사용
    private void loadJellyTitle() {
        if (jellyId <= 0) return;
        jellyViewModel.getJellyById(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                String title = resource.getData().getTitle();
                String jellyName = resource.getData().getJellyName();
                agedEmoName = (title != null && !title.trim().isEmpty()) ? title
                        : (jellyName != null ? jellyName : "");
                ((TextView) findViewById(R.id.tv_emotion_combo)).setText(agedEmoName);
            }
        });
    }

    private void loadComboImage() {
        if (jellyCombId <= 0) return;
        jellyCombViewModel.getJellyCombById(jellyCombId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                Glide.with(this)
                        .load(NetworkConfig.assetUrl(resource.getData().getJellyIcon()))
                        .into(ivCombo);
            }
        });
    }

    private void submitDiary() {
        String content = etContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "일기 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jellyCombId <= 0) {
            Toast.makeText(this, "감정 조합 정보가 없어요.", Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);

        long userId = SessionManager.getInstance(this).getUserId();
        String name = agedEmoName != null && !agedEmoName.trim().isEmpty() ? agedEmoName : "숙성된 감정";
        AgedEmoSaveReqDTO dto = new AgedEmoSaveReqDTO(
                userId, jellyCombId, name, content,
                LocalDate.now().toString(), new ArrayList<>());

        agedEmoViewModel.createAgedEmo(dto).observe(this, agedResource -> {
            if (agedResource != null && agedResource.isSuccess()) {
                // 승격 성공 → 원본 젤리 삭제 후 뮤지엄으로
                removeNotifiedId();
                deleteOriginalJellyAndGoToMuseum();
            } else if (agedResource != null && agedResource.isError()) {
                setLoading(false);
                Toast.makeText(this, agedResource.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteOriginalJellyAndGoToMuseum() {
        if (jellyId <= 0) {
            goToMuseum();
            return;
        }
        jellyViewModel.deleteJelly(jellyId).observe(this, resource -> {
            // 삭제 성공/실패와 무관하게 뮤지엄으로 이동 (실패 시에도 AgedEmo는 생성됨)
            goToMuseum();
        });
    }

    private void goToMuseum() {
        setLoading(false);
        startActivity(new Intent(this, jellyMuseumActivity.class));
        finish();
    }

    private void removeNotifiedId() {
        SharedPreferences prefs = getSharedPreferences(AgingCheckWorker.PREFS_NAME, MODE_PRIVATE);
        Set<String> notified = new HashSet<>(
                prefs.getStringSet(AgingCheckWorker.KEY_NOTIFIED_IDS, new HashSet<>()));
        notified.remove(String.valueOf(jellyId));
        prefs.edit().putStringSet(AgingCheckWorker.KEY_NOTIFIED_IDS, notified).apply();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
    }
}
