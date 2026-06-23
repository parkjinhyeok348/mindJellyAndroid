package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.AgingFilter;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

import java.time.LocalDate;
import java.util.List;

public class AgingCompletedListActivity extends AppCompatActivity {

    private JellyViewModel jellyViewModel;
    private LinearLayout container;
    private ProgressBar progressBar;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging_completed_list);

        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);
        container  = findViewById(R.id.container_completed_jellies);
        progressBar = findViewById(R.id.progress_completed);
        tvStatus   = findViewById(R.id.tv_completed_status);

        loadCompletedJellies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompletedJellies();
    }

    private void loadCompletedJellies() {
        long userId = SessionManager.getInstance(this).getUserId();
        if (userId <= 0) {
            tvStatus.setText("로그인 후 확인할 수 있어요.");
            return;
        }
        setLoading(true);
        jellyViewModel.getJellyList(userId).observe(this, resource -> {
            setLoading(false);
            container.removeAllViews();
            if (resource == null || resource.isError()) {
                tvStatus.setText("목록을 불러오지 못했어요.");
                return;
            }
            List<JellyDrawerResDTO> completed = AgingFilter.filterCompleted(
                    resource.getData(), LocalDate.now());
            if (completed.isEmpty()) {
                tvStatus.setText("아직 숙성이 완료된 젤리가 없어요.");
                return;
            }
            tvStatus.setText("숙성 완료 젤리 " + completed.size() + "개");
            for (JellyDrawerResDTO jelly : completed) {
                addJellyCard(jelly);
            }
        });
    }

    private void addJellyCard(JellyDrawerResDTO jelly) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_edit_text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView tvInfo = new TextView(this);
        tvInfo.setText(safe(jelly.getEmo1Name()) + " + " + safe(jelly.getEmo2Name()));
        tvInfo.setTextSize(16);
        tvInfo.setTextColor(getColor(R.color.text_primary));
        tvInfo.setTypeface(tvInfo.getTypeface(), android.graphics.Typeface.BOLD);
        card.addView(tvInfo);

        TextView tvDate = new TextView(this);
        tvDate.setText("숙성 완료: " + safe(jelly.getCreateDate()));
        tvDate.setTextSize(13);
        tvDate.setTextColor(getColor(R.color.text_secondary));
        card.addView(tvDate);

        Button btnWrite = new Button(this);
        btnWrite.setText("일기 쓰기");
        btnWrite.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgingCompleteActivity.class);
            intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_ID,      jelly.getJellyId());
            intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_COMB_ID, jelly.getJellyCombId());
            intent.putExtra(AgingCompleteActivity.EXTRA_EMO1_NAME,     jelly.getEmo1Name());
            intent.putExtra(AgingCompleteActivity.EXTRA_EMO2_NAME,     jelly.getEmo2Name());
            intent.putExtra(AgingCompleteActivity.EXTRA_CREATE_DATE,   jelly.getCreateDate());
            startActivity(intent);
        });
        card.addView(btnWrite);
        container.addView(card);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private String safe(String s) { return s != null ? s : "-"; }
}
