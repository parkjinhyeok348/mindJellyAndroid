package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.ErrorMessageUtil;
import com.mindJellyProject.mindjelly.databinding.ActivityJellyEditBinding;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyUpdateReqDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.viewmodel.JellyViewModel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jelly.view
 * @description : 젤리 수정 화면 — 제목/본문을 프리필 후 편집하여 저장
 * @date : 2026-06-26
 */
public class JellyEditActivity extends AppCompatActivity {

    private ActivityJellyEditBinding binding;
    private JellyViewModel jellyViewModel;
    private long jellyId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJellyEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jellyId = getIntent().getLongExtra("jellyId", -1L);
        if (jellyId == -1L) {
            finish();
            return;
        }

        jellyViewModel = new ViewModelProvider(this).get(JellyViewModel.class);

        // 기존 제목·본문 프리필
        jellyViewModel.getJellyInfo(jellyId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                binding.etTitle.setText(resource.getData().getTitle());
                binding.etContent.setText(resource.getData().getContent());
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnSaveEdit.setOnClickListener(v -> save());
    }

    private void save() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_diary_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSaveEdit.setEnabled(false);
        // jellyName/jellyImages는 null로 보내 기존 값 유지 (백엔드 blank-guard), title/content만 수정
        JellyUpdateReqDTO reqDTO = new JellyUpdateReqDTO(null, title, content, null);
        jellyViewModel.updateJelly(jellyId, reqDTO).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(this, getString(R.string.success_edit_saved), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.mindJellyProject.mindjelly.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else if (resource != null && resource.isError()) {
                binding.btnSaveEdit.setEnabled(true);
                Toast.makeText(this, ErrorMessageUtil.getKoreanMessage(resource, this), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
