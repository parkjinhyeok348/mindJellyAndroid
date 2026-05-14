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
            tvStatus.setText("濡쒓렇?????숈꽦諛⑹쓣 ?뺤씤?????덉뒿?덈떎.");
            return;
        }

        setLoading(true);
        tvStatus.setText("?숈꽦諛⑹쓣 遺덈윭?ㅻ뒗 以묒엯?덈떎.");
        jellyViewModel.getJellyList(userId).observe(this, this::renderJellies);
    }

    private void renderJellies(Resource<List<JellyDrawerResDTO>> result) {
        setLoading(false);
        container.removeAllViews();

        if (result == null || result.isError()) {
            tvStatus.setText(result == null ? "?숈꽦諛⑹쓣 遺덈윭?ㅼ? 紐삵뻽?듬땲??" : result.getError());
            return;
        }

        List<JellyDrawerResDTO> jellies = result.getData();
        if (jellies == null || jellies.isEmpty()) {
            tvStatus.setText("?꾩쭅 ?숈꽦???ㅻ━媛 ?놁뒿?덈떎.");
            addMessageCard("?ㅻ━ ?쒕엻?먯꽌 ?ㅻ━瑜??숈꽦?쒗궎硫??닿납???쒖떆?⑸땲??");
            return;
        }

        tvStatus.setText("?ㅻ━ " + jellies.size() + "媛쒕? ?뺤씤?덉뒿?덈떎.");
        for (JellyDrawerResDTO jelly : jellies) {
            addJellyCard(jelly);
        }
    }

    private void addJellyCard(JellyDrawerResDTO jelly) {
        LinearLayout card = createCard();
        String status = jelly.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = Boolean.TRUE.equals(jelly.getAging()) ? "Aging" : "Waiting";
        }
        card.addView(createText("?ㅻ━ ID: " + jelly.getJellyId(), 16, true));
        card.addView(createText("?곹깭: " + status, 14, false));
        card.addView(createText("?앹꽦?? " + nullToDash(jelly.getCreateDate()), 14, false));
        card.addView(createText("媛먯젙: " + nullToDash(jelly.getEmo1Name()) + " / " + nullToDash(jelly.getEmo2Name()), 14, false));
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

    private String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
