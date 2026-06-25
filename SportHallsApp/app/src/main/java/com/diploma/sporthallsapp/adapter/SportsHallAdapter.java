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
        holder.tvHallRating.setText(String.valueOf(hall.getRating()));
        holder.tvWorkingHours.setText("Работно време: " + hall.getWorkingHoursFrom() + "-" + hall.getWorkingHoursTo());

        // ЗАРЕЖДАНЕ НА СНИМКАТА С GLIDE
        holder.ivHallImage.setImageResource(R.drawable.placeholder_hall);

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
        TextView tvHallName, tvHallType, tvHallLocation, tvHallPrice, tvHallRating, tvWorkingHours;
        ImageView ivHallImage;

        public HallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHallName = itemView.findViewById(R.id.tvHallName);
            tvHallType = itemView.findViewById(R.id.tvSportType);
            tvHallLocation = itemView.findViewById(R.id.tvHallAddress);
            tvHallPrice = itemView.findViewById(R.id.tvHallPrice);
            tvHallRating = itemView.findViewById(R.id.tvHallRating); // Свържи рейтинга
            tvWorkingHours = itemView.findViewById(R.id.tvHallWorkingHours);
            ivHallImage = itemView.findViewById(R.id.ivHallImage);

        }
    }
}
