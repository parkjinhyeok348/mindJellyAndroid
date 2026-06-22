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
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

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

        holder.tvCreateDate.setText(jelly.getCreateDate());

        Glide.with(holder.itemView.getContext())
                .load(RetrofitClient.BASE_SERVER_URL + jelly.getEmo1Icon())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.emo1ImageView);

        Glide.with(holder.itemView.getContext())
                .load(RetrofitClient.BASE_SERVER_URL + jelly.getEmo2Icon())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.emo2ImageView);

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

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView emo1ImageView, emo2ImageView;
        TextView tvCreateDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emo1ImageView = itemView.findViewById(R.id.emo1ImageView);
            emo2ImageView = itemView.findViewById(R.id.emo2ImageView);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
        }
    }
}