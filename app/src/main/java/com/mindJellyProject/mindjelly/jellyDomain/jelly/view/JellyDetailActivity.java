package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view.AgingRoomActivity;
import com.mindJellyProject.mindjelly.common.ErrorMessageUtil;
import com.mindJellyProject.mindjelly.databinding.ActivityJellyDetailBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.view
 * @description : 젤리 상세 화면 — jellyId로 단건 조회 후 일기 내용 표시, 숙성하기 버튼
 * @date : 2026-05-20
 */
public class JellyDetailActivity extends AppCompatActivity {

    private ActivityJellyDetailBinding binding;
    private JellyViewModel jellyViewModel;
    private long jellyId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJellyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jellyId = getIntent().getLongExtra("jellyId", -1L);
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
                // 숙성중이 아닐 때만 숙성하기 버튼 노출
                boolean aging = Boolean.TRUE.equals(resource.getData().getAging());
                binding.btnStartAging.setVisibility(aging ? View.GONE : View.VISIBLE);
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnStartAging.setOnClickListener(v -> showAgingConfirmDialog());
    }

    private void showAgingConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_aging_title)
                .setMessage(R.string.dialog_aging_message)
                .setPositiveButton(R.string.dialog_aging_positive, (d, w) -> startAging())
                .setNegativeButton(R.string.dialog_aging_negative, null)
                .show();
    }

    private void startAging() {
        binding.btnStartAging.setEnabled(false);
        jellyViewModel.startAging(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(this, getString(R.string.success_aging_started), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AgingRoomActivity.class));
                finish();
            } else if (resource != null && resource.isError()) {
                binding.btnStartAging.setEnabled(true);
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
