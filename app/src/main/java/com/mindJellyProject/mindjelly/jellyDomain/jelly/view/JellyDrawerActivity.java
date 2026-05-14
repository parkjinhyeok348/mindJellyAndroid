package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;
import com.mindJellyProject.mindjelly.users.view.LoginActivity;

public class JellyDrawerActivity extends AppCompatActivity {
    private static final String TAG = "JellyDrawerActivity";
    private RecyclerView rvJellyList;
    private JellyDrawerAdapter adapter;
    private JellyViewModel jellyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jelly_drawer);
        
        Log.d(TAG, "onCreate: Initializing activity");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // View 초기화
        rvJellyList = findViewById(R.id.rvJellyList);
        rvJellyList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JellyDrawerAdapter();
        rvJellyList.setAdapter(adapter);

        // ViewModel 초기화
        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);

        // 젤리 리스트 요청
        SessionManager sessionManager = SessionManager.getInstance(this);
        Long userId = sessionManager.getUserId();
        if (userId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        Log.d(TAG, "Requesting jelly list for userId: " + userId);
        
        jellyViewModel.getJellyList(userId).observe(this, resource -> {
            if (resource != null) {
                if (resource.getData() != null) {
                    Log.d(TAG, "Successfully loaded " + resource.getData().size() + " jellies");
                    adapter.submitList(resource.getData());
                } else {
                    String errorMessage = resource.getError() != null ? resource.getError() : "Unknown Error";
                    Log.e(TAG, "Failed to load jelly list: " + errorMessage);
                    Toast.makeText(JellyDrawerActivity.this, "젤리 리스트를 불러오는데 실패했습니다: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Resource is null");
            }
        });
    }
}
