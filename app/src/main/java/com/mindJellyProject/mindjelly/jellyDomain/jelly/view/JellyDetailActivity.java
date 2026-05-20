package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.common.ErrorMessageUtil;
import com.mindJellyProject.mindjelly.databinding.ActivityJellyDetailBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.view
 * @description : 젤리 상세 화면 — jellyId로 단건 조회 후 일기 내용 표시
 * @date : 2026-05-20
 */
public class JellyDetailActivity extends AppCompatActivity {

    private ActivityJellyDetailBinding binding;
    private JellyViewModel jellyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJellyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long jellyId = getIntent().getLongExtra("jellyId", -1L);
        if (jellyId == -1L) {
            finish();
            return;
        }

        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);

        jellyViewModel.getJellyById(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                binding.tvJellyName.setText(resource.getData().getJellyName());
                binding.tvCreateDate.setText(resource.getData().getCreateDate());
                binding.tvContent.setText(resource.getData().getContent());
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
