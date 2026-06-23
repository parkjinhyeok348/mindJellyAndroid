package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoSaveReqDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel.AgedEmoViewModel;
import com.mindJellyProject.mindjelly.common.AgingCheckWorker;
import com.mindJellyProject.mindjelly.common.SessionManager;
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
    private EditText etContent;
    private ProgressBar progressBar;
    private Button btnSubmit;

    private long jellyId;
    private long jellyCombId;
    private String emo1Name;
    private String emo2Name;
    private String createDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging_complete);

        jellyId     = getIntent().getLongExtra(EXTRA_JELLY_ID, -1L);
        jellyCombId = getIntent().getLongExtra(EXTRA_JELLY_COMB_ID, -1L);
        emo1Name    = getIntent().getStringExtra(EXTRA_EMO1_NAME);
        emo2Name    = getIntent().getStringExtra(EXTRA_EMO2_NAME);
        createDate  = getIntent().getStringExtra(EXTRA_CREATE_DATE);

        agedEmoViewModel   = new ViewModelProvider(this).get(AgedEmoViewModel.class);
        jellyCombViewModel = new ViewModelProvider(this).get(JellyCombViewModel.class);

        etContent   = findViewById(R.id.et_aged_content);
        progressBar = findViewById(R.id.progress_submit);
        btnSubmit   = findViewById(R.id.btn_submit_aged_diary);

        ((TextView) findViewById(R.id.tv_emotion_combo))
                .setText(safe(emo1Name) + " + " + safe(emo2Name));

        TextView tvDate = findViewById(R.id.tv_aging_complete_date);
        try {
            tvDate.setText("숙성 완료일: " + LocalDate.parse(createDate).plusDays(7));
        } catch (Exception e) {
            tvDate.setText("숙성 완료");
        }

        btnSubmit.setOnClickListener(v -> submitDiary());
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

        jellyCombViewModel.getJellyCombById(jellyCombId).observe(this, combResource -> {
            if (combResource == null || combResource.isError()) {
                setLoading(false);
                Toast.makeText(this, "감정 조합 정보를 불러오지 못했어요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String agedEmoName = combResource.getData() != null
                    ? combResource.getData().getJellyIcon() : "";
            long userId = SessionManager.getInstance(this).getUserId();

            AgedEmoSaveReqDTO dto = new AgedEmoSaveReqDTO(
                    userId, jellyCombId, agedEmoName, content,
                    LocalDate.now().toString(), new ArrayList<>());

            agedEmoViewModel.createAgedEmo(dto).observe(this, agedResource -> {
                setLoading(false);
                if (agedResource != null && agedResource.isSuccess()) {
                    removeNotifiedId();
                    startActivity(new Intent(this, jellyMuseumActivity.class));
                    finish();
                } else if (agedResource != null && agedResource.isError()) {
                    Toast.makeText(this, agedResource.getError(), Toast.LENGTH_SHORT).show();
                }
            });
        });
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

    private String safe(String s) { return s != null ? s : "-"; }
}
