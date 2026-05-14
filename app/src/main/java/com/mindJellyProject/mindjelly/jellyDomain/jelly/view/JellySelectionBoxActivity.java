package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingRoomActivity;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.jellyMuseumActivity;
import com.mindJellyProject.mindjelly.basicEmo.view.TodayJellyActivity;

public class JellySelectionBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jelly_selection_box);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnTodayJelly = findViewById(R.id.btn_today_jelly);
        Button btnJellyDrawer = findViewById(R.id.btn_jelly_drawer);
        Button btnAgingRoom = findViewById(R.id.btn_aging_room);
        Button btnJellyMuseum = findViewById(R.id.btn_jelly_museum);

        btnTodayJelly.setOnClickListener(v -> startActivity(new Intent(this, TodayJellyActivity.class)));
        btnJellyDrawer.setOnClickListener(v -> startActivity(new Intent(this, JellyDrawerActivity.class)));
        btnAgingRoom.setOnClickListener(v -> startActivity(new Intent(this, AgingRoomActivity.class)));
        btnJellyMuseum.setOnClickListener(v -> startActivity(new Intent(this, jellyMuseumActivity.class)));
    }
}
