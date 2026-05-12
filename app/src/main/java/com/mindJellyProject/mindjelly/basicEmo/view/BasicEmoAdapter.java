package com.mindJellyProject.mindjelly.basicEmo.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.basicEmo.model.BasicEmoResDTO;

import java.util.ArrayList;
import java.util.List;

public class BasicEmoAdapter extends RecyclerView.Adapter<BasicEmoAdapter.ViewHolder> {

    private List<BasicEmoResDTO> emos = new ArrayList<>();
    // 슬래시(/)와 이미지가 저장된 경로(/images/)를 추가합니다.
    private final String serverUrl = "http://10.0.2.2:8080";

    @SuppressLint("NotifyDataSetChanged")
    public void setEmos(List<BasicEmoResDTO> emos) {
        this.emos = emos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basic_emo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BasicEmoResDTO emo = emos.get(position);
        
        // 유저 요청: serverUrl + emo.getEmoIcon()
        String fullImageUrl = serverUrl + emo.getEmoIcon();
        
        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // 로딩 중 표시할 기본 이미지
                .error(android.R.drawable.stat_notify_error)          // 에러 시 표시할 기본 이미지
                .into(holder.emoImageView);
    }

    @Override
    public int getItemCount() {
        return emos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emoImageView = itemView.findViewById(R.id.emoImageView);
        }
    }
}
