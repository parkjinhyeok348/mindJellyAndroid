package com.mindJellyProject.mindjelly;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingRoomActivity;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.jellyMuseumActivity;
import com.mindJellyProject.mindjelly.basicEmo.view.TodayJellyActivity;
import com.mindJellyProject.mindjelly.databinding.ActivityMainBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.view.JellyDrawerActivity;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.view.JellySelectionBoxActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 말풍선 랜덤 문구
        String[] messages = getResources().getStringArray(R.array.home_speech_messages);
        binding.tvSpeechBubble.setText(pickRandom(messages, new Random()));

        // 버튼 네비게이션
        binding.btnTodayJelly.setOnClickListener(v ->
                startActivity(new Intent(this, TodayJellyActivity.class)));

        binding.btnJellyDrawer.setOnClickListener(v ->
                startActivity(new Intent(this, JellyDrawerActivity.class)));

        binding.btnAgingRoom.setOnClickListener(v ->
                startActivity(new Intent(this, AgingRoomActivity.class)));

        binding.btnJellyMuseum.setOnClickListener(v ->
                startActivity(new Intent(this, jellyMuseumActivity.class)));

        binding.btnSelectionBox.setOnClickListener(v ->
                startActivity(new Intent(this, JellySelectionBoxActivity.class)));
    }

    static String pickRandom(String[] messages, Random random) {
        if (messages == null || messages.length == 0) return "";
        return messages[random.nextInt(messages.length)];
    }
}
