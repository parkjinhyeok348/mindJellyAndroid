package com.mindJellyProject.mindjelly.users.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.Resource;
import com.mindJellyProject.mindjelly.common.SessionManager;
import com.mindJellyProject.mindjelly.users.model.UserUpdateReqDTO;
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvStatus;
    private TextView tvUserId;
    private EditText etNickName;
    private EditText etProfileImage;
    private CheckBox cbMarketing;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userId = SessionManager.getInstance(this).getUserId();

        tvStatus = findViewById(R.id.tv_profile_status);
        tvUserId = findViewById(R.id.tv_user_id);
        etNickName = findViewById(R.id.et_nick_name);
        etProfileImage = findViewById(R.id.et_profile_image);
        cbMarketing = findViewById(R.id.cb_marketing);
        progressBar = findViewById(R.id.progress_profile);
        Button btnLoad = findViewById(R.id.btn_load_profile);
        Button btnSave = findViewById(R.id.btn_save_profile);

        tvUserId.setText(userId > 0 ? "?ъ슜??ID: " + userId : "濡쒓렇???뺣낫媛 ?놁뒿?덈떎.");
        btnLoad.setOnClickListener(v -> loadProfile());
        btnSave.setOnClickListener(v -> saveProfile());

        if (userId > 0) {
            loadProfile();
        } else {
            tvStatus.setText("濡쒓렇?????꾨줈?꾩쓣 遺덈윭?????덉뒿?덈떎.");
        }
    }

    private void loadProfile() {
        if (!hasUser()) {
            return;
        }

        setLoading(true);
        tvStatus.setText("?꾨줈?꾩쓣 遺덈윭?ㅻ뒗 以묒엯?덈떎.");
        userViewModel.getUserProfile(userId).observe(this, this::renderProfile);
    }

    private void renderProfile(Resource<UserUpdateReqDTO> result) {
        setLoading(false);
        if (result != null && result.isSuccess() && result.getData() != null) {
            UserUpdateReqDTO profile = result.getData();
            etNickName.setText(profile.getNickName());
            etProfileImage.setText(profile.getProfileImage());
            cbMarketing.setChecked(Boolean.TRUE.equals(profile.getMarketing()));
            tvStatus.setText("?꾨줈?꾩쓣 遺덈윭?붿뒿?덈떎.");
        } else {
            tvStatus.setText(result == null ? "?꾨줈?꾩쓣 遺덈윭?ㅼ? 紐삵뻽?듬땲??" : result.getError());
        }
    }

    private void saveProfile() {
        if (!hasUser()) {
            return;
        }

        String nickName = etNickName.getText().toString().trim();
        String profileImage = etProfileImage.getText().toString().trim();
        if (nickName.isEmpty()) {
            Toast.makeText(this, "?됰꽕?꾩쓣 ?낅젰?섏꽭??", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        tvStatus.setText("?꾨줈?꾩쓣 ??ν븯??以묒엯?덈떎.");
        UserUpdateReqDTO reqDTO = new UserUpdateReqDTO(nickName, profileImage, cbMarketing.isChecked());
        userViewModel.updateUser(userId, reqDTO).observe(this, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                tvStatus.setText("?꾨줈?꾩쓣 ??ν뻽?듬땲??");
            } else {
                tvStatus.setText(result == null ? "?꾨줈????μ뿉 ?ㅽ뙣?덉뒿?덈떎." : result.getError());
            }
        });
    }

    private boolean hasUser() {
        if (userId <= 0) {
            Toast.makeText(this, "濡쒓렇???뺣낫媛 ?놁뒿?덈떎.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
