package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

import java.time.LocalDate;
import java.util.List;

public class AgingRoomActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private LinearLayout container;
    private JellyViewModel jellyViewModel;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aging_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);
        userId = SessionManager.getInstance(this).getUserId();
        tvStatus = findViewById(R.id.tv_aging_status);
        progressBar = findViewById(R.id.progress_aging);
        container = findViewById(R.id.container_aging_jellies);
        Button btnRefresh = findViewById(R.id.btn_refresh_aging);

        btnRefresh.setOnClickListener(v -> loadJellies());
        loadJellies();
    }

    private void loadJellies() {
        container.removeAllViews();
        if (userId <= 0) {
            tvStatus.setText("로그인 후 에이징룸을 확인할 수 있어요.");
            return;
        }

        setLoading(true);
        tvStatus.setText("에이징룸을 불러오는 중입니다.");
        jellyViewModel.getJellyList(userId).observe(this, this::renderJellies);
    }

    private void renderJellies(Resource<List<JellyDrawerResDTO>> result) {
        setLoading(false);
        container.removeAllViews();

        if (result == null || result.isError()) {
            tvStatus.setText(result == null ? "에이징룸을 불러오지 못했어요." : result.getError());
            return;
        }

        List<JellyDrawerResDTO> jellies = result.getData();
        List<AgingRoomMapper.DisplayJelly> agingRows = AgingRoomMapper.toAgingRows(jellies, LocalDate.now());
        if (agingRows.isEmpty()) {
            tvStatus.setText("아직 숙성 중인 젤리가 없어요.");
            addMessageCard("젤리 서랍에서 젤리를 숙성시키면 이곳에 표시됩니다.");
            if (AgingRoomMapper.hasCompletedJellies(jellies)) {
                addMessageCard("숙성이 끝난 젤리가 있어요. 젤리 뮤지엄에서 확인해보세요.");
            }
            return;
        }

        tvStatus.setText("숙성 중인 젤리 " + agingRows.size() + "개를 확인했어요.");
        for (AgingRoomMapper.DisplayJelly jelly : agingRows) {
            addJellyCard(jelly);
        }
        if (AgingRoomMapper.hasCompletedJellies(jellies)) {
            addMessageCard("숙성이 끝난 젤리가 있어요. 젤리 뮤지엄에서 확인해보세요.");
        }
    }

    private void addJellyCard(AgingRoomMapper.DisplayJelly jelly) {
        LinearLayout card = createCard();
        card.addView(createText(jelly.title, 16, true));
        card.addView(createText(jelly.dDayText, 18, true));
        card.addView(createText(jelly.statusText, 14, false));
        card.addView(createText(jelly.createDateText, 14, false));
        card.addView(createText(jelly.emotionText, 14, false));
        container.addView(card);
    }

    private void addMessageCard(String message) {
        LinearLayout card = createCard();
        card.addView(createText(message, 15, false));
        container.addView(card);
    }

    private LinearLayout createCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_edit_text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);
        return card;
    }

    private TextView createText(String text, int sp, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(sp);
        textView.setTextColor(getColor(R.color.text_primary));
        if (bold) {
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
