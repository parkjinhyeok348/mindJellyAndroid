package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class JellyDrawerAdapter extends ListAdapter<JellyDrawerResDTO, JellyDrawerAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<JellyDrawerResDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<JellyDrawerResDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                    return a.getJellyId().equals(b.getJellyId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull JellyDrawerResDTO a, @NonNull JellyDrawerResDTO b) {
                    return a.getJellyId().equals(b.getJellyId())
                            && Objects.equals(a.getCreateDate(), b.getCreateDate());
                }
            };

    public interface OnItemClickListener {
        void onItemClick(Long jellyId);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.itemClickListener = l;
    }

    public JellyDrawerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jelly_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JellyDrawerResDTO jelly = getItem(position);

        holder.tvCreateDate.setText(formatDate(jelly.getCreateDate()));

        // 조합 젤리 이미지 1개 표시 (생성 시 뜨는 조합 이미지와 동일)
        Glide.with(holder.itemView.getContext())
                .load(NetworkConfig.assetUrl(jelly.getJellyIcon()))
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.combinedImageView);

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(jelly.getJellyId());
            }
        });
    }

    static boolean sameItem(JellyDrawerResDTO left, JellyDrawerResDTO right) {
        if (left == null || right == null) return false;
        if (left.getJellyId() == null || right.getJellyId() == null) return false;
        return left.getJellyId().equals(right.getJellyId());
    }

    static boolean sameContent(JellyDrawerResDTO left, JellyDrawerResDTO right) {
        if (left == null || right == null) return false;
        return Objects.equals(left.getJellyId(), right.getJellyId())
                && Objects.equals(left.getStatus(), right.getStatus())
                && Objects.equals(left.getCreateDate(), right.getCreateDate())
                && Objects.equals(left.getEmo1Name(), right.getEmo1Name())
                && Objects.equals(left.getEmo1Icon(), right.getEmo1Icon())
                && Objects.equals(left.getEmo2Name(), right.getEmo2Name())
                && Objects.equals(left.getEmo2Icon(), right.getEmo2Icon());
    }

    static String emotionNamesText(JellyDrawerResDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getEmo1Name() != null && !dto.getEmo1Name().isEmpty()) {
            sb.append(dto.getEmo1Name());
        }
        if (dto.getEmo2Name() != null && !dto.getEmo2Name().isEmpty()) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(dto.getEmo2Name());
        }
        return sb.toString();
    }

    // ISO(yyyy-MM-dd) → yy.MM.dd 표시 포맷
    private static String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            LocalDate d = LocalDate.parse(isoDate);
            return String.format(Locale.getDefault(), "%02d.%02d.%02d",
                    d.getYear() % 100, d.getMonthValue(), d.getDayOfMonth());
        } catch (Exception e) {
            return isoDate;
        }
    }

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView combinedImageView;
        TextView tvCreateDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            combinedImageView = itemView.findViewById(R.id.combinedImageView);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
        }
    }
}