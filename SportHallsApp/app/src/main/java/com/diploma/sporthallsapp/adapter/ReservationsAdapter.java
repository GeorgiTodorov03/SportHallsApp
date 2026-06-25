package com.diploma.sporthallsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.model.ReservationResponse;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

    private Context context;
    private List<ReservationResponse> list;

    public ReservationsAdapter(Context context, List<ReservationResponse> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ReservationsAdapter.ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationViewHolder holder, int position) {
        ReservationResponse res = list.get(position);

        // Свързваме името на залата от вложения обект, ако го имаш дефиниран така
        if (res.getSportsHall() != null) {
            holder.tvResHallName.setText(res.getSportsHall().getName());
        } else {
            holder.tvResHallName.setText("Спортна зала");
        }

        holder.tvResDateTime.setText(res.getReservationDate() + " • " + res.getReservationTime() + " ч.");
        holder.tvResStatus.setText(res.getStatus()); // напр. "PENDING", "APPROVED"

        // Промяна на цвета на статуса динамично
        if ("APPROVED".equalsIgnoreCase(res.getStatus()) || "Одобрена".equalsIgnoreCase(res.getStatus())) {
            holder.tvResStatus.setTextColor(android.graphics.Color.parseColor("#22C55E")); // Зелено
        } else {
            holder.tvResStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Оранжево за изчакваща
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvResHallName, tvResDateTime, tvResStatus;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResHallName = itemView.findViewById(R.id.tvResHallName);
            tvResDateTime = itemView.findViewById(R.id.tvResDateTime);
            tvResStatus = itemView.findViewById(R.id.tvResStatus);
        }
    }
}
