package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.util.ArrayList;
import java.util.List;

public class JellyDrawerAdapter extends RecyclerView.Adapter<JellyDrawerAdapter.ViewHolder> {

    private List<JellyDrawerResDTO> jellyList = new ArrayList<>();

    public void setJellyList(List<JellyDrawerResDTO> list) {
        this.jellyList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jelly_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JellyDrawerResDTO jelly = jellyList.get(position);
        holder.tvJellyId.setText("Jelly ID: " + jelly.getJellyId());
        holder.tvCreateDate.setText(jelly.getCreateDate());
        holder.tvAgingStatus.setText(jelly.getAging() ? "에이징 중" : "완성");
    }

    @Override
    public int getItemCount() {
        return jellyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJellyId, tvCreateDate, tvAgingStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJellyId = itemView.findViewById(R.id.tvJellyId);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
            tvAgingStatus = itemView.findViewById(R.id.tvAgingStatus);
        }
    }
}
