package com.mindJellyProject.mindjelly.users.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.users.model.UserSaveReqDTO;
import com.mindJellyProject.mindjelly.users.model.Users;
import com.mindJellyProject.mindjelly.users.retrofit.UserService;
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText etMobilePhone, etEmail, etPassword, etUserName, etNickName, etBirthDate, etAgeRange;
    private RadioGroup rgGender;
    private CheckBox cbMarketing;
    private Button btnRegister;

    private final UserViewModel userViewModel = new UserViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void initViews() {
        etMobilePhone = findViewById(R.id.et_mobile_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etUserName = findViewById(R.id.et_user_name);
        etNickName = findViewById(R.id.et_nick_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etAgeRange = findViewById(R.id.et_age_range);
        rgGender = findViewById(R.id.rg_gender);
        cbMarketing = findViewById(R.id.cb_marketing);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void registerUser() {
        String mobilePhoneNumber = etMobilePhone.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String userName = etUserName.getText().toString();
        String nickName = etNickName.getText().toString();
        Boolean gender = rgGender.getCheckedRadioButtonId() == R.id.rb_male;
        String birthDate = etBirthDate.getText().toString();
        String ageRange = etAgeRange.getText().toString();
        Boolean isMarketing = cbMarketing.isChecked();

        // DTO 생성
        UserSaveReqDTO userSaveReqDTO = new UserSaveReqDTO(
                mobilePhoneNumber, email, password, userName, nickName, gender,
                birthDate, null, ageRange, isMarketing
        );

        userViewModel.createUser(userSaveReqDTO);
    }
}