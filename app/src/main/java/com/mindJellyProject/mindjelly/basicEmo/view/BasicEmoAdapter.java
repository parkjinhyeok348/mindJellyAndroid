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
    private final String serverUrl = "http://10.0.2.2:8080";
    
    // 선택된 포지션들을 관리하는 리스트 (최대 2개)
    private final List<Integer> selectedPositions = new ArrayList<>();

    // 선택 상태 변경 리스너 인터페이스
    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<BasicEmoResDTO> selectedEmos);
    }

    private OnSelectionChangedListener selectionChangedListener;

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEmos(List<BasicEmoResDTO> emos) {
        this.emos = emos;
        selectedPositions.clear();
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
        
        String fullImageUrl = serverUrl + emo.getEmoIcon();
        
        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.emoImageView);

        // 현재 아이템이 선택된 상태인지 확인
        holder.itemView.setSelected(selectedPositions.contains(position));

        holder.itemView.setOnClickListener(v -> {
            if (selectedPositions.contains(position)) {
                // 이미 선택된 항목 클릭 시 해제
                selectedPositions.remove(Integer.valueOf(position));
                notifyItemChanged(position);
            } else {
                // 2개 미만으로 선택된 경우에만 새로 선택 가능
                if (selectedPositions.size() < 2) {
                    selectedPositions.add(position);
                    notifyItemChanged(position);
                }
            }
            
            // 리스너를 통해 선택된 데이터 전달
            if (selectionChangedListener != null) {
                selectionChangedListener.onSelectionChanged(getSelectedEmos());
            }
        });
    }

    @Override
    public int getItemCount() {
        return emos.size();
    }

    public List<BasicEmoResDTO> getSelectedEmos() {
        List<BasicEmoResDTO> selectedEmos = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            if (pos < emos.size()) {
                selectedEmos.add(emos.get(pos));
            }
        }
        return selectedEmos;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emoImageView = itemView.findViewById(R.id.emoImageView);
        }
    }
}
