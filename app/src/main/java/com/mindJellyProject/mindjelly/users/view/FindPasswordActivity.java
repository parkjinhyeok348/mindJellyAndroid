package com.mindJellyProject.mindjelly.users.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.mindJellyProject.mindjelly.users.model.FindPasswordReqDTO;
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

public class FindPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etUserName;
    private EditText etMobilePhone;
    private ProgressBar progressBar;
    private TextView tvResult;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        etEmail = findViewById(R.id.et_email);
        etUserName = findViewById(R.id.et_user_name);
        etMobilePhone = findViewById(R.id.et_mobile_phone);
        progressBar = findViewById(R.id.progress_find_password);
        tvResult = findViewById(R.id.tv_find_password_result);
        Button btnFindPassword = findViewById(R.id.btn_find_password);

        btnFindPassword.setOnClickListener(v -> findPassword());
    }

    private void findPassword() {
        String email = etEmail.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String mobilePhone = etMobilePhone.getText().toString().trim();

        if (email.isEmpty() || userName.isEmpty() || mobilePhone.isEmpty()) {
            Toast.makeText(this, "?대찓?? ?대쫫, ?대???踰덊샇瑜?紐⑤몢 ?낅젰?섏꽭??", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        tvResult.setVisibility(View.GONE);
        FindPasswordReqDTO reqDTO = new FindPasswordReqDTO(email, userName, mobilePhone);
        userViewModel.findPassword(reqDTO).observe(this, this::renderResult);
    }

    private void renderResult(Resource<String> result) {
        setLoading(false);
        tvResult.setVisibility(View.VISIBLE);
        if (result != null && result.isSuccess()) {
            tvResult.setText(result.getData());
        } else {
            String error = result == null ? "鍮꾨?踰덊샇 ?뺣낫瑜??뺤씤?????놁뒿?덈떎." : result.getError();
            tvResult.setText(error);
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
