package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindJellyProject.mindjelly.R;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.model.AgedEmoImageResDTO;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmoImage.retrofit.AgedEmoImageService;
import com.mindJellyProject.mindjelly.common.NetworkConfig;
import com.mindJellyProject.mindjelly.common.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JellyMuseumAdapter extends RecyclerView.Adapter<JellyMuseumAdapter.ViewHolder> {

    private final List<AgedEmoMuseumResDTO> items = new ArrayList<>();
    private final AgedEmoImageService agedEmoImageService;

    JellyMuseumAdapter(Context context) {
        this.agedEmoImageService = RetrofitClient.createService(AgedEmoImageService.class, context);
    }

    @SuppressLint("NotifyDataSetChanged")
    void setItems(List<AgedEmoMuseumResDTO> newItems) {
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
                .inflate(R.layout.item_jelly_museum, parent, false);
        return new ViewHolder(view, agedEmoImageService);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
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
        private final ImageView ivJellyImage;
        private final TextView tvJellyTitle;
        private final TextView tvCreateDate;
        private final AgedEmoImageService agedEmoImageService;
        private Call<List<AgedEmoImageResDTO>> currentCall;

        ViewHolder(@NonNull View itemView, AgedEmoImageService agedEmoImageService) {
            super(itemView);
            this.agedEmoImageService = agedEmoImageService;
            ivJellyImage = itemView.findViewById(R.id.iv_jelly_image);
            tvJellyTitle = itemView.findViewById(R.id.tv_jelly_title);
            tvCreateDate = itemView.findViewById(R.id.tv_create_date);
        }

        void bind(AgedEmoMuseumResDTO item) {
            tvJellyTitle.setText(JellyMuseumMapper.title(item.getAgedEmoId()));
            tvCreateDate.setText(JellyMuseumMapper.formatCreatedDate(item.getCreateDate()));

            clear();
            ivJellyImage.setImageResource(R.drawable.ic_jelly_placeholder);

            Long agedEmoId = item.getAgedEmoId();
            if (agedEmoId == null) {
                return;
            }

            currentCall = agedEmoImageService.getAgedEmoImageList(agedEmoId);
            currentCall.enqueue(new Callback<List<AgedEmoImageResDTO>>() {
                @Override
                public void onResponse(Call<List<AgedEmoImageResDTO>> call,
                                       Response<List<AgedEmoImageResDTO>> response) {
                    if (call.isCanceled()) {
                        return;
                    }
                    if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                        return;
                    }
                    String imageName = response.body().get(0).getImageName();
                    if (imageName == null) {
                        return;
                    }
                    String url = NetworkConfig.assetUrl(imageName);
                    Glide.with(itemView.getContext())
                            .load(url)
                            .placeholder(R.drawable.ic_jelly_placeholder)
                            .error(R.drawable.ic_jelly_placeholder)
                            .into(ivJellyImage);
                }

                @Override
                public void onFailure(Call<List<AgedEmoImageResDTO>> call, Throwable t) {
                    // 실패 시 placeholder 유지
                }
            });
        }

        void clear() {
            if (currentCall != null) {
                currentCall.cancel();
                currentCall = null;
            }
            Glide.with(itemView.getContext()).clear(ivJellyImage);
        }
    }
}
