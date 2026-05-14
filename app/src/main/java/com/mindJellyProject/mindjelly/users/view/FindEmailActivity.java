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
import com.mindJellyProject.mindjelly.users.viewmodel.UserViewModel;

public class FindEmailActivity extends AppCompatActivity {
    private EditText etUserName;
    private EditText etMobilePhone;
    private ProgressBar progressBar;
    private TextView tvResult;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        etUserName = findViewById(R.id.et_user_name);
        etMobilePhone = findViewById(R.id.et_mobile_phone);
        progressBar = findViewById(R.id.progress_find_email);
        tvResult = findViewById(R.id.tv_find_email_result);
        Button btnFindEmail = findViewById(R.id.btn_find_email);

        btnFindEmail.setOnClickListener(v -> findEmail());
    }

    private void findEmail() {
        String userName = etUserName.getText().toString().trim();
        String mobilePhone = etMobilePhone.getText().toString().trim();

        if (userName.isEmpty() || mobilePhone.isEmpty()) {
            Toast.makeText(this, "?대쫫怨??대???踰덊샇瑜??낅젰?섏꽭??", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        tvResult.setVisibility(View.GONE);
        userViewModel.findEmail(userName, mobilePhone).observe(this, this::renderResult);
    }

    private void renderResult(Resource<String> result) {
        setLoading(false);
        tvResult.setVisibility(View.VISIBLE);
        if (result != null && result.isSuccess()) {
            tvResult.setText("媛???대찓?? " + result.getData());
        } else {
            String error = result == null ? "?대찓?쇱쓣 李얠쓣 ???놁뒿?덈떎." : result.getError();
            tvResult.setText(error);
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
