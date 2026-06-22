package com.diploma.sporthallsapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.adapter.AdminHallsAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.SportsHall;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminApprovalActivity extends AppCompatActivity implements AdminHallsAdapter.OnAdminActionListener {

    private RecyclerView rvPendingHalls;
    private AdminHallsAdapter adapter;
    private List<SportsHall> pendingList = new ArrayList<>();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval); // Може да преизползваш същия прост XML с RecyclerView

        rvPendingHalls = findViewById(R.id.rvPendingHalls); // Търсим RecyclerView-то
        rvPendingHalls.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminHallsAdapter(pendingList, this);
        rvPendingHalls.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("token", null);

        loadPendingHalls();
    }

    private void loadPendingHalls() {
        ApiClient.getApiService().getPendingHalls(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SportsHall>> call, @NonNull Response<List<SportsHall>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendingList.clear();
                    pendingList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SportsHall>> call, @NonNull Throwable t) {
                Toast.makeText(AdminApprovalActivity.this, "Мрежова грешка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(SportsHall hall) {
        ApiClient.getApiService().approveHall(token, hall.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminApprovalActivity.this, "Залата е одобрена!", Toast.LENGTH_SHORT).show();
                    loadPendingHalls(); // Презареждаме списъка
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onReject(SportsHall hall) {
        ApiClient.getApiService().rejectHall(token, hall.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminApprovalActivity.this, "Залата е отхвърлена!", Toast.LENGTH_SHORT).show();
                    loadPendingHalls();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }
}