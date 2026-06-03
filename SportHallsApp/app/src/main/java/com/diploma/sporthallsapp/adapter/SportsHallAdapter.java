package com.diploma.sporthallsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.activity.DetailActivity;
import com.diploma.sporthallsapp.model.SportsHall;

import java.util.List;

public class SportsHallAdapter extends RecyclerView.Adapter<SportsHallAdapter.HallViewHolder> {

    private List<SportsHall> hallList;

    public SportsHallAdapter(List<SportsHall> hallList) {
        this.hallList = hallList;
    }

    @NonNull
    @Override
    public SportsHallAdapter.HallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hall, parent, false);
        return new HallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportsHallAdapter.HallViewHolder holder, int position) {
        SportsHall hall = hallList.get(position);
        holder.tvHallName.setText(hall.getName());
        holder.tvHallType.setText(hall.getType());
        holder.tvHallLocation.setText(hall.getLocation());
        holder.tvHallPrice.setText(String.format("%.2f лв / час", hall.getPricePerHour()));

        // ЗАРЕЖДАНЕ НА СНИМКАТА С GLIDE
        Glide.with(holder.itemView.getContext())
                .load(hall.getImageUrl()) // Взема URL линка от базата данни
                .placeholder(R.drawable.placeholder_hall) // Снимка по подразбиране докато зарежда
                .error(R.drawable.placeholder_hall) // Снимка при грешка
                .into(holder.ivHallImage);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();

            // Създаваме Intent към DetailActivity (ще го създадем в Стъпка 3)
            Intent intent = new Intent(context, DetailActivity.class);

            // Поставяме целия обект за зала в "раницата" на Intent-а
            intent.putExtra("selected_hall", hall);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hallList.size();
    }

    static class HallViewHolder extends RecyclerView.ViewHolder {
        TextView tvHallName, tvHallType, tvHallLocation, tvHallPrice;
        ImageView ivHallImage;

        public HallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvHallType = itemView.findViewById(R.id.tvHallType);
            tvHallLocation = itemView.findViewById(R.id.tvHallLocation);
            tvHallPrice = itemView.findViewById(R.id.tvHallPrice);
            ivHallImage = itemView.findViewById(R.id.ivHallImage);

        }
    }
}
