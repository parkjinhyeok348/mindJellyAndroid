package com.mindJellyProject.mindjelly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mindJellyProject.mindjelly.basicEmo.view.TodayJellyActivity;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.view.JellyDrawerActivity;
import com.mindJellyProject.mindjelly.users.view.LoginActivity;
import com.mindJellyProject.mindjelly.users.view.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 버튼 초기화
        Button btnTodayJelly = findViewById(R.id.btnTodayJelly);
        Button btnJellyDrawer = findViewById(R.id.btnJellyDrawer);
        Button btnAgingRoom = findViewById(R.id.btnAgingRoom);
        Button btnJellyMuseum = findViewById(R.id.btnJellyMuseum);
        Button btnSelectionBox = findViewById(R.id.btnSelectionBox);

        btnTodayJelly.setOnClickListener( v -> {// 오늘의 마음젤리 페이지로 이동
            Intent intent = new Intent(MainActivity.this, TodayJellyActivity.class);
            startActivity(intent);
        });

        btnJellyDrawer.setOnClickListener( v -> {// 젤리서랍 페이지로 이동
            Intent intent = new Intent(MainActivity.this, JellyDrawerActivity.class);
            startActivity(intent);
        });

        btnAgingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnJellyMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSelectionBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}