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
 * @description : 젤리 상세 화면 — 제목/내용 표시, 수정·삭제·숙성하기 (숙성중이 아닐 때만 노출)
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

        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, JellyEditActivity.class);
            intent.putExtra("jellyId", jellyId);
            startActivity(intent);
        });
        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        binding.btnStartAging.setOnClickListener(v -> showAgingConfirmDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 수정 후 복귀 시 최신 내용 반영을 위해 매번 재조회
        if (jellyId != -1L) {
            loadJelly();
        }
    }

    private void loadJelly() {
        jellyViewModel.getJellyById(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                binding.tvJellyName.setText(resource.getData().getJellyName());
                binding.tvCreateDate.setText(resource.getData().getCreateDate());
                binding.tvTitle.setText(resource.getData().getTitle());
                binding.tvContent.setText(resource.getData().getContent());
                // 숙성중이 아닐 때만 수정/삭제/숙성 버튼 노출
                boolean aging = Boolean.TRUE.equals(resource.getData().getAging());
                int visibility = aging ? View.GONE : View.VISIBLE;
                binding.actionRow.setVisibility(visibility);
                binding.btnStartAging.setVisibility(visibility);
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_jelly_title)
                .setMessage(R.string.dialog_delete_jelly_message)
                .setPositiveButton(R.string.dialog_delete_jelly_positive, (d, w) -> deleteJelly())
                .setNegativeButton(R.string.dialog_delete_jelly_negative, null)
                .show();
    }

    private void deleteJelly() {
        binding.btnDelete.setEnabled(false);
        jellyViewModel.deleteJelly(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(this, getString(R.string.success_jelly_deleted), Toast.LENGTH_SHORT).show();
                finish();
            } else if (resource != null && resource.isError()) {
                binding.btnDelete.setEnabled(true);
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
            }
        });
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
