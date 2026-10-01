package com.xmall75.steamdealsalert.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.local.DealEntity;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deal_card, parent, false);
        return new DealViewHolder(view);
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

        private final TextView tvTitle, tvSalePrice, tvNormalPrice, tvAppId, tvRating;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvNormalPrice = itemView.findViewById(R.id.tvNormalPrice);
            tvAppId = itemView.findViewById(R.id.tvAppId);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        public void bind(DealEntity deal) {
            Context context = itemView.getContext();

            tvTitle.setText(deal.title);
            tvSalePrice.setText(String.format(Locale.US, "$%.2f", deal.salePrice));

            tvNormalPrice.setText(String.format(Locale.US, "$%.2f", deal.normalPrice));
            tvNormalPrice.setPaintFlags(tvNormalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvAppId.setText("AppID: " + deal.steamAppId);

            if (deal.ratingPercent >= 80) {
                tvRating.setTextColor(ContextCompat.getColor(context, R.color.tn_green));
            } else {
                tvRating.setTextColor(ContextCompat.getColor(context, R.color.tn_magenta));
            }

            String ratingText = deal.ratingText != null ? deal.ratingText : "-";
            tvRating.setText(String.format(Locale.US, "Rating: %d%% (%s)", deal.ratingPercent, ratingText));

            itemView.setOnClickListener(v -> {
                String url = "https://store.steampowered.com/app/" + deal.steamAppId;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            });
        }
    }
}