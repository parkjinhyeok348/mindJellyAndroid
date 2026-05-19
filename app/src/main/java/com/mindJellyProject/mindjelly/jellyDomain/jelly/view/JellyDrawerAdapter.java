package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.util.Objects;

public class JellyDrawerAdapter extends ListAdapter<JellyDrawerResDTO, JellyDrawerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<JellyDrawerResDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<JellyDrawerResDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                    return sameItem(a, b);
                }

                @Override
                public boolean areContentsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                    return sameContent(a, b);
                }
            };

    static boolean sameItem(JellyDrawerResDTO a, JellyDrawerResDTO b) {
        return a != null
                && b != null
                && a.getJellyId() != null
                && b.getJellyId() != null
                && a.getJellyId().equals(b.getJellyId());
    }

    static boolean sameContent(JellyDrawerResDTO a, JellyDrawerResDTO b) {
        return a != null
                && b != null
                && Objects.equals(a.getJellyId(), b.getJellyId())
                && Objects.equals(a.getStatus(), b.getStatus())
                && Objects.equals(a.getCreateDate(), b.getCreateDate())
                && Objects.equals(a.getEmo1Name(), b.getEmo1Name())
                && Objects.equals(a.getEmo1Icon(), b.getEmo1Icon())
                && Objects.equals(a.getEmo2Name(), b.getEmo2Name())
                && Objects.equals(a.getEmo2Icon(), b.getEmo2Icon());
    }

    static String emotionNamesText(JellyDrawerResDTO jelly) {
        if (jelly == null) {
            return "";
        }

        String emo1Name = clean(jelly.getEmo1Name());
        String emo2Name = clean(jelly.getEmo2Name());

        if (emo1Name.isEmpty()) {
            return emo2Name;
        }
        if (emo2Name.isEmpty()) {
            return emo1Name;
        }
        return emo1Name + " + " + emo2Name;
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public interface OnStartAgingListener {
        void onStartAging(Long jellyId);
    }

    private OnStartAgingListener startAgingListener;

    public void setOnStartAgingListener(OnStartAgingListener l) {
        this.startAgingListener = l;
    }

    public JellyDrawerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jelly_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JellyDrawerResDTO jelly = getItem(position);

        // 날짜 표시
        holder.tvCreateDate.setText(jelly.getCreateDate());
        holder.tvEmotionNames.setText(emotionNamesText(jelly));

        // 감정 아이콘 1 Glide 로딩
        Glide.with(holder.itemView.getContext())
                .load(NetworkConfig.assetUrl(jelly.getEmo1Icon()))
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.emo1ImageView);

        // 감정 아이콘 2 Glide 로딩
        Glide.with(holder.itemView.getContext())
                .load(NetworkConfig.assetUrl(jelly.getEmo2Icon()))
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.emo2ImageView);

        // 상태 배지 3-state 처리
        android.content.Context ctx = holder.itemView.getContext();
        String status = jelly.getStatus();
        int bgColor, textColor;
        String statusText;

        if ("AGING".equals(status)) {
            bgColor = R.color.badge_aging_bg;
            textColor = R.color.badge_aging_text;
            statusText = ctx.getString(R.string.status_aging);
            holder.btnStartAging.setVisibility(View.GONE);
        } else if ("MATURED".equals(status)) {
            bgColor = R.color.badge_complete_bg;
            textColor = R.color.badge_complete_text;
            statusText = ctx.getString(R.string.status_complete);
            holder.btnStartAging.setVisibility(View.GONE);
        } else {
            // WAITING 또는 null/unknown → fallback
            bgColor = R.color.badge_waiting_bg;
            textColor = R.color.badge_waiting_text;
            statusText = ctx.getString(R.string.status_waiting);
            holder.btnStartAging.setVisibility(View.VISIBLE);
        }

        holder.tvAgingStatus.setText(statusText);
        holder.tvAgingStatus.setTextColor(ContextCompat.getColor(ctx, textColor));

        // GradientDrawable mutate — 공유 drawable 상태 오염 방지
        android.graphics.drawable.Drawable bg = holder.tvAgingStatus.getBackground();
        if (bg instanceof android.graphics.drawable.GradientDrawable) {
            ((android.graphics.drawable.GradientDrawable) bg.mutate())
                    .setColor(ContextCompat.getColor(ctx, bgColor));
        } else {
            holder.tvAgingStatus.setBackgroundColor(ContextCompat.getColor(ctx, bgColor));
        }

        // 숙성시키기 버튼 클릭 리스너
        holder.btnStartAging.setOnClickListener(v -> {
            if (startAgingListener != null) {
                startAgingListener.onStartAging(jelly.getJellyId());
            }
        });
    }

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView emo1ImageView, emo2ImageView;
        TextView tvCreateDate, tvEmotionNames, tvAgingStatus;
        Button btnStartAging;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emo1ImageView = itemView.findViewById(R.id.emo1ImageView);
            emo2ImageView = itemView.findViewById(R.id.emo2ImageView);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
            tvEmotionNames = itemView.findViewById(R.id.tvEmotionNames);
            tvAgingStatus = itemView.findViewById(R.id.tvAgingStatus);
            btnStartAging = itemView.findViewById(R.id.btnStartAging);
        }
    }
}
