package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.viewmodel.AgedEmoViewModel;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.viewmodel.JellyCombViewModel;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view
 * @description : 젤리 뮤지엄 상세 화면 — 숙성된 감정의 이름, 날짜, 일기 내용을 표시
 * @date : 2026-06-27
 */
public class AgedEmoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_AGED_EMO_ID = "extra_aged_emo_id";

    private ImageView ivImage;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvContent;

    private AgedEmoViewModel agedEmoViewModel;
    private JellyCombViewModel jellyCombViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aged_emo_detail);

        long agedEmoId = getIntent().getLongExtra(EXTRA_AGED_EMO_ID, -1L);
        if (agedEmoId == -1L) {
            finish();
            return;
        }

        ivImage   = findViewById(R.id.iv_aged_emo_image);
        tvName    = findViewById(R.id.tv_aged_emo_name);
        tvDate    = findViewById(R.id.tv_aged_emo_date);
        tvContent = findViewById(R.id.tv_aged_emo_content);

        agedEmoViewModel  = new ViewModelProvider(this).get(AgedEmoViewModel.class);
        jellyCombViewModel = new ViewModelProvider(this).get(JellyCombViewModel.class);

        agedEmoViewModel.getAgedEmoById(agedEmoId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                AgedEmoResDTO data = resource.getData();
                tvName.setText(data.getAgedEmoName());
                tvDate.setText(JellyMuseumMapper.formatCreatedDate(data.getCreateDate()));
                tvContent.setText(data.getContent());
                loadComboImage(data.getJellyCombId());
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, resource.getError(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadComboImage(Long jellyCombId) {
        if (jellyCombId == null || jellyCombId <= 0) return;
        jellyCombViewModel.getJellyCombById(jellyCombId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                String icon = resource.getData().getJellyIcon();
                if (icon != null) {
                    Glide.with(this)
                            .load(NetworkConfig.assetUrl(icon))
                            .placeholder(R.drawable.ic_jelly_placeholder)
                            .error(R.drawable.ic_jelly_placeholder)
                            .into(ivImage);
                }
            }
        });
    }
}
