package com.diploma.sporthallsapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.adapter.SportsHallAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.SportsHall;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerDashboardActivity extends AppCompatActivity {

    private RecyclerView rvOwnerHalls;
    private FloatingActionButton fabAddHall;
    private SportsHallAdapter adapter;
    private List<SportsHall> ownerHallsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        rvOwnerHalls = findViewById(R.id.rvOwnerHalls);
        fabAddHall = findViewById(R.id.fabAddHall);

        // Настройка на RecyclerView
        rvOwnerHalls.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SportsHallAdapter(ownerHallsList);
        rvOwnerHalls.setAdapter(adapter);

        // Клик върху бутона "+" отваря форма за добавяне на зала
        fabAddHall.setOnClickListener(v -> {
            startActivity(new Intent(OwnerDashboardActivity.this, AddSportsHallActivity.class));
            Toast.makeText(this, "Отваряне на форма за нова зала...", Toast.LENGTH_SHORT).show();
        });

        loadOwnerHalls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnerHalls(); // Презарежда залите, ако собственикът се върне от екрана за добавяне
    }

    private void loadOwnerHalls() {
        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Грешка: Липсва токен!", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;

        ApiClient.getApiService().getOwnerHalls(authHeader).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SportsHall>> call, @NonNull Response<List<SportsHall>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ownerHallsList.clear();
                    ownerHallsList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Обновяваме списъка на екрана
                } else {
                    Toast.makeText(OwnerDashboardActivity.this, "Грешка при зареждане: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SportsHall>> call, @NonNull Throwable t) {
                Toast.makeText(OwnerDashboardActivity.this, "Мрежова грешка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}