package com.mindJellyProject.mindjelly.users.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.MainActivity;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.users.model.UserLoginReqDTO;
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private Button btnSignup, btnFindEamil,btnFindPassword;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // View 초기화
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        btnFindEamil = findViewById(R.id.btn_find_email);
        btnFindPassword = findViewById(R.id.btn_find_password);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
            // Login 요청
            UserLoginReqDTO reqDTO = new UserLoginReqDTO(email, password);
            userViewModel.login(reqDTO).observe(this, resource -> {
                if (resource != null && resource.getData() != null) {
                    // 로그인 성공 시 Main 페이지로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 실패 시 Toast 메시지 표시
                    Toast.makeText(this, "이메일 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 회원가입 버튼 클릭 이벤트
        btnSignup.setOnClickListener(v -> {
            // 회원가입 페이지로 이동
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // 이메일/비밀번호 찾기 버튼 클릭 이벤트
        btnFindEamil.setOnClickListener(v -> {
            // 비밀번호 찾기 페이지로 이동
            Intent intent = new Intent(LoginActivity.this, FindEmailActivity.class);
            startActivity(intent);
        });
        btnFindPassword.setOnClickListener(v -> {
            // 비밀번호 찾기 페이지로 이동
            Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
            startActivity(intent);
        });
    }
}