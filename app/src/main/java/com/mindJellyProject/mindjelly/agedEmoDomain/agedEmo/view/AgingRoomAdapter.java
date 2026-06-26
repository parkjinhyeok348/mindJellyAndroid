package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.common.RetrofitClient;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model.JellyCombResDTO;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit.JellyCombService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgingRoomAdapter extends RecyclerView.Adapter<AgingRoomAdapter.ViewHolder> {

    public interface OnCompleteClickListener {
        void onComplete(JellyDrawerResDTO jelly);
    }

    private final List<JellyDrawerResDTO> items = new ArrayList<>();
    private final JellyCombService jellyCombService;
    private OnCompleteClickListener completeClickListener;

    AgingRoomAdapter(Context context) {
        this.jellyCombService = RetrofitClient.createService(JellyCombService.class, context);
    }

    void setOnCompleteClickListener(OnCompleteClickListener listener) {
        this.completeClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setItems(List<JellyDrawerResDTO> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aging_room, parent, false);
        return new ViewHolder(view, jellyCombService);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), completeClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivJellyIcon;
        private final TextView tvEmotionName;
        private final TextView tvCreateDate;
        private final TextView tvDday;
        private final Button btnComplete;
        private final JellyCombService jellyCombService;
        private Call<JellyCombResDTO> currentCall;

        ViewHolder(@NonNull View itemView, JellyCombService jellyCombService) {
            super(itemView);
            this.jellyCombService = jellyCombService;
            ivJellyIcon = itemView.findViewById(R.id.iv_jelly_icon);
            tvEmotionName = itemView.findViewById(R.id.tv_emotion_name);
            tvCreateDate = itemView.findViewById(R.id.tv_create_date);
            tvDday = itemView.findViewById(R.id.tv_dday);
            btnComplete = itemView.findViewById(R.id.btn_complete);
        }

        void bind(JellyDrawerResDTO item, OnCompleteClickListener completeClickListener) {
            tvEmotionName.setText(AgingRoomMapper.emotionTitle(item));
            tvCreateDate.setText(AgingRoomMapper.formatCreatedDate(item.getCreateDate()));
            tvDday.setText(AgingRoomMapper.formatDday(item.getCreateDate(), LocalDate.now()));

            // 숙성 완료(D-Day) 항목에만 완성하기 버튼 노출
            boolean matured = AgingRoomMapper.isMatured(item.getCreateDate(), LocalDate.now());
            btnComplete.setVisibility(matured ? View.VISIBLE : View.GONE);
            if (matured && completeClickListener != null) {
                btnComplete.setOnClickListener(v -> completeClickListener.onComplete(item));
            } else {
                btnComplete.setOnClickListener(null);
            }

            clear();
            ivJellyIcon.setImageResource(R.drawable.ic_jelly_placeholder);

            Long jellyCombId = item.getJellyCombId();
            if (jellyCombId == null) {
                return;
            }

            currentCall = jellyCombService.getJellyCombById(jellyCombId);
            currentCall.enqueue(new Callback<JellyCombResDTO>() {
                @Override
                public void onResponse(Call<JellyCombResDTO> call, Response<JellyCombResDTO> response) {
                    if (call.isCanceled()) {
                        return;
                    }
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }
                    String url = NetworkConfig.assetUrl(response.body().getJellyIcon());
                    Glide.with(itemView.getContext())
                            .load(url)
                            .placeholder(R.drawable.ic_jelly_placeholder)
                            .error(R.drawable.ic_jelly_placeholder)
                            .into(ivJellyIcon);
                }

                @Override
                public void onFailure(Call<JellyCombResDTO> call, Throwable t) {
                    // 실패 시 placeholder 유지
                }
            });
        }

        void clear() {
            if (currentCall != null) {
                currentCall.cancel();
                currentCall = null;
            }
            Glide.with(itemView.getContext()).clear(ivJellyIcon);
        }
    }
}
