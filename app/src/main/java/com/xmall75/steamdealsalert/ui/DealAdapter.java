package com.xmall75.steamdealsalert.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.databinding.ItemDealCardBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    private final List<DealEntity> deals = new ArrayList<>();

    public void setDeals(List<DealEntity> newDeals) {
        this.deals.clear();
        if (newDeals != null) {
            this.deals.addAll(newDeals);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDealCardBinding binding = ItemDealCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        DealEntity deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    static class DealViewHolder extends RecyclerView.ViewHolder {

        private final ItemDealCardBinding binding;

        public DealViewHolder(@NonNull ItemDealCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DealEntity deal) {
            Context context = itemView.getContext();

            binding.tvTitle.setText(deal.title);
            binding.tvSalePrice.setText(String.format(Locale.US, "$%.2f", deal.salePrice));

            binding.tvNormalPrice.setText(String.format(Locale.US, "$%.2f", deal.normalPrice));
            binding.tvNormalPrice.setPaintFlags(binding.tvNormalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            binding.tvAppId.setText(String.format(Locale.US, "AppID: %s", deal.steamAppId));

            if (deal.ratingPercent >= 80) {
                binding.tvRating.setTextColor(ContextCompat.getColor(context, R.color.tn_green));
            } else {
                binding.tvRating.setTextColor(ContextCompat.getColor(context, R.color.tn_magenta));
            }

            String ratingText = deal.ratingText != null ? deal.ratingText : "-";
            binding.tvRating.setText(String.format(Locale.US, "Rating: %d%% (%s)", deal.ratingPercent, ratingText));

            itemView.setOnClickListener(v -> {
                String url = "https://store.steampowered.com/app/" + deal.steamAppId;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            });
        }
    }
}