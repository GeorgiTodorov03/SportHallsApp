package com.diploma.sporthallsapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.activity.LoginActivity;
import com.diploma.sporthallsapp.adapter.ReservationsAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.ReservationResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationsFragment extends Fragment {

    private RecyclerView rvReservations;
    private ReservationsAdapter adapter;
    private List<ReservationResponse> reservationList = new ArrayList<>();
    private TextView tvNoReservations;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Зареждаме XML на фрагмента (увери се, че в него имаш съответните ID-та)
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);

        rvReservations = view.findViewById(R.id.rvReservations);
        tvNoReservations = view.findViewById(R.id.tvNoReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReservationsAdapter(getContext(), reservationList);
        rvReservations.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthAndLoadReservations();
    }

    private void checkAuthAndLoadReservations() {

        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        // Ако няма токен или е празен – директен шут към LoginActivity
        if (token == null || token.trim().isEmpty()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Добавяме флаг, за да изчистим стека, ако се налага
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Спираме изпълнението на метода дотук
            return;
        }

        // Ако има токен – показваме RecyclerView и дърпаме данните от бекенда
        if (rvReservations != null) {
            rvReservations.setVisibility(View.VISIBLE);
        }
        loadReservationsFromBackend("Bearer " + token);
    }

    private void loadReservationsFromBackend(String token) {
        ApiClient.getApiService().getMyReservations(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationResponse>> call, @NonNull Response<List<ReservationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservationList.clear();
                    reservationList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (reservationList.isEmpty()) {
                        tvNoReservations.setVisibility(View.VISIBLE);
                    } else {
                        tvNoReservations.setVisibility(View.GONE);
                    }
                } else {
                    int errorCode = response.code();

                    if (errorCode == 401) {
                        // 1. Токенът е изтекъл - трием го!
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove("token").apply();


                        Toast.makeText(getContext(), "Сесията изтече. Моля, влезте отново.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Сървърна грешка: " + errorCode, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationResponse>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Няма връзка с бекенда", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
