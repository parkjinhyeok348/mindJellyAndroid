package com.mindJellyProject.mindjelly.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.mindJellyProject.mindjelly.MainActivity;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.users.view.LoginActivity;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.common
 * @description : 앱 시작할 때 보이는 스플래쉬 스크린 액티비티
 * @modification : 2025-01-07(Jinhyeok) 수정
 * @date : 2025-01-07
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-07     Jinhyeok        주석 생성
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // 1초
    }
}
