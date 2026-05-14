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
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel.AgedEmoViewModel;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.SessionManager;

import java.util.List;

public class jellyMuseumActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private LinearLayout container;
    private AgedEmoViewModel agedEmoViewModel;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jelly_museum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        agedEmoViewModel = new ViewModelProvider(this).get(AgedEmoViewModel.class);
        userId = SessionManager.getInstance(this).getUserId();
        tvStatus = findViewById(R.id.tv_museum_status);
        progressBar = findViewById(R.id.progress_museum);
        container = findViewById(R.id.container_museum_jellies);
        Button btnRefresh = findViewById(R.id.btn_refresh_museum);

        btnRefresh.setOnClickListener(v -> loadMuseum());
        loadMuseum();
    }

    private void loadMuseum() {
        container.removeAllViews();
        if (userId <= 0) {
            tvStatus.setText("濡쒓렇?????ㅻ━ 諛뺣Ъ愿???뺤씤?????덉뒿?덈떎.");
            return;
        }

        setLoading(true);
        tvStatus.setText("?ㅻ━ 諛뺣Ъ愿??遺덈윭?ㅻ뒗 以묒엯?덈떎.");
        agedEmoViewModel.getAgedEmoList(userId).observe(this, this::renderMuseum);
    }

    private void renderMuseum(Resource<List<AgedEmoMuseumResDTO>> result) {
        setLoading(false);
        container.removeAllViews();

        if (result == null || result.isError()) {
            tvStatus.setText(result == null ? "?ㅻ━ 諛뺣Ъ愿??遺덈윭?ㅼ? 紐삵뻽?듬땲??" : result.getError());
            return;
        }

        List<AgedEmoMuseumResDTO> agedJellies = result.getData();
        if (agedJellies == null || agedJellies.isEmpty()) {
            tvStatus.setText("?꾩쭅 ?꾩꽦???ㅻ━媛 ?놁뒿?덈떎.");
            addMessageCard("?숈꽦???앸궃 ?ㅻ━媛 ?앷린硫?諛뺣Ъ愿???꾩떆?⑸땲??");
            return;
        }

        tvStatus.setText("?꾩떆???ㅻ━ " + agedJellies.size() + "媛쒕? ?뺤씤?덉뒿?덈떎.");
        for (AgedEmoMuseumResDTO agedJelly : agedJellies) {
            addMuseumCard(agedJelly);
        }
    }

    private void addMuseumCard(AgedEmoMuseumResDTO agedJelly) {
        LinearLayout card = createCard();
        card.addView(createText("?꾩꽦 ?ㅻ━ ID: " + agedJelly.getAgedEmoId(), 16, true));
        card.addView(createText("議고빀 ID: " + agedJelly.getJellyCombId(), 14, false));
        card.addView(createText("?앹꽦?? " + nullToDash(agedJelly.getCreateDate()), 14, false));
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
