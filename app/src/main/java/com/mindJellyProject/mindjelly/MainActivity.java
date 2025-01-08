package com.mindJellyProject.mindjelly;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

        btnTodayJelly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnJellyDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
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