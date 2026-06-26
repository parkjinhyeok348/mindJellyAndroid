package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

import java.util.Collections;
import java.util.List;

public class AgingRoomActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private AgingRoomAdapter adapter;
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
        recyclerView = findViewById(R.id.container_aging_jellies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AgingRoomAdapter(this);
        recyclerView.setAdapter(adapter);

        // 완성하기 → 숙성 완료 화면으로 이동
        adapter.setOnCompleteClickListener(jelly -> {
            Intent intent = new Intent(this, AgingCompleteActivity.class);
            intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_ID,
                    jelly.getJellyId() != null ? jelly.getJellyId() : -1L);
            intent.putExtra(AgingCompleteActivity.EXTRA_JELLY_COMB_ID,
                    jelly.getJellyCombId() != null ? jelly.getJellyCombId() : -1L);
            intent.putExtra(AgingCompleteActivity.EXTRA_CREATE_DATE, jelly.getCreateDate());
            startActivity(intent);
        });

        Button btnRefresh = findViewById(R.id.btn_refresh_aging);
        ImageButton btnNotification = findViewById(R.id.btn_aging_notification);
        btnNotification.setOnClickListener(v ->
                startActivity(new Intent(this, AgingCompletedListActivity.class)));

        btnRefresh.setOnClickListener(v -> loadJellies());
        loadJellies();
    }

    private void loadJellies() {
        adapter.setItems(Collections.emptyList());
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

        if (result == null || result.isError()) {
            adapter.setItems(Collections.emptyList());
            tvStatus.setText(result == null ? "에이징룸을 불러오지 못했어요." : result.getError());
            return;
        }

        List<JellyDrawerResDTO> jellies = result.getData();
        List<JellyDrawerResDTO> agingRows = AgingRoomMapper.toAgingRows(jellies);
        adapter.setItems(agingRows);

        String completedHint = AgingRoomMapper.hasCompletedJellies(jellies)
                ? " 숙성이 끝난 젤리는 젤리 뮤지엄에서 확인해보세요."
                : "";
        if (agingRows.isEmpty()) {
            tvStatus.setText("아직 숙성 중인 젤리가 없어요." + completedHint);
        } else {
            tvStatus.setText("숙성 중인 젤리 " + agingRows.size() + "개를 확인했어요." + completedHint);
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
