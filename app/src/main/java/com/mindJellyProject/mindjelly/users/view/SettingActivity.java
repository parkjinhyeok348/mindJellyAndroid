package com.mindJellyProject.mindjelly.users.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

public class SettingActivity extends AppCompatActivity {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        tvStatus = findViewById(R.id.tv_setting_status);
        progressBar = findViewById(R.id.progress_setting);
        Button btnOpenProfile = findViewById(R.id.btn_open_profile);
        Button btnLogout = findViewById(R.id.btn_logout);
        Button btnDeleteUser = findViewById(R.id.btn_delete_user);

        refreshStatus();
        btnOpenProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
        btnDeleteUser.setOnClickListener(v -> confirmDeleteUser());
    }

    private void refreshStatus() {
        long userId = sessionManager.getUserId();
        tvStatus.setText(userId > 0
                ? getString(R.string.setting_status_logged_in, userId)
                : getString(R.string.setting_status_logged_out));
    }

    private void logout() {
        sessionManager.clear();
        Toast.makeText(this, R.string.setting_logout_done, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void confirmDeleteUser() {
        long userId = sessionManager.getUserId();
        if (userId <= 0) {
            Toast.makeText(this, R.string.setting_status_logged_out, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_user_title)
                .setMessage(R.string.dialog_delete_user_message)
                .setNegativeButton(R.string.dialog_delete_user_negative, null)
                .setPositiveButton(R.string.dialog_delete_user_positive, (dialog, which) -> deleteUser(userId))
                .show();
    }

    private void deleteUser(long userId) {
        setLoading(true);
        userViewModel.deleteUser(userId).observe(this, result -> {
            setLoading(false);
            if (result != null && result.getError() == null) {
                sessionManager.clear();
                Toast.makeText(this, R.string.setting_delete_user_done, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                String error = result == null ? getString(R.string.setting_delete_user_failed) : result.getError();
                tvStatus.setText(error);
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
