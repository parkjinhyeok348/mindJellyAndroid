package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel.AgedEmoViewModel;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.SessionManager;

import java.util.Collections;
import java.util.List;

public class jellyMuseumActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private JellyMuseumAdapter adapter;
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
        recyclerView = findViewById(R.id.container_museum_jellies);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new JellyMuseumAdapter(this);
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, AgedEmoDetailActivity.class);
            intent.putExtra(AgedEmoDetailActivity.EXTRA_AGED_EMO_ID, item.getAgedEmoId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        Button btnRefresh = findViewById(R.id.btn_refresh_museum);
        btnRefresh.setOnClickListener(v -> loadMuseum());
        loadMuseum();
    }

    private void loadMuseum() {
        adapter.setItems(Collections.emptyList());
        if (userId <= 0) {
            tvStatus.setText("로그인 후 젤리 뮤지엄을 확인할 수 있어요.");
            return;
        }

        setLoading(true);
        tvStatus.setText("젤리 뮤지엄을 불러오는 중입니다.");
        agedEmoViewModel.getAgedEmoList(userId).observe(this, this::renderMuseum);
    }

    private void renderMuseum(Resource<List<AgedEmoMuseumResDTO>> result) {
        setLoading(false);

        if (result == null || result.isError()) {
            adapter.setItems(Collections.emptyList());
            tvStatus.setText(result == null ? "젤리 뮤지엄을 불러오지 못했어요." : result.getError());
            return;
        }

        List<AgedEmoMuseumResDTO> agedJellies = result.getData();
        adapter.setItems(agedJellies);
        if (agedJellies == null || agedJellies.isEmpty()) {
            tvStatus.setText("아직 완성된 젤리가 없어요.");
        } else {
            tvStatus.setText("전시된 젤리 " + agedJellies.size() + "개를 확인했어요.");
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
