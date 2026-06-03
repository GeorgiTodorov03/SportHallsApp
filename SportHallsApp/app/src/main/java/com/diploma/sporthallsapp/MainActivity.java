package com.diploma.sporthallsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.activity.LoginActivity;
import com.diploma.sporthallsapp.adapter.SportsHallAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.SportsHall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<SportsHall> sportsHallList = new ArrayList<>(); // Празен списък като начало
    private RecyclerView rvHalls;
    private SportsHallAdapter adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvHalls = findViewById(R.id.rvHalls);
        rvHalls.setLayoutManager(new LinearLayoutManager(this));

        // Инициализираме адаптера веднага с празния списък, за да няма "No adapter attached"
        adapter = new SportsHallAdapter(sportsHallList);
        rvHalls.setAdapter(adapter);

        sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        // Взимаме токена, който записахме при Login
        String token = sharedPreferences.getString("token", null);

        if (token != null) {
            loadSportsHalls(token);
        } else {
            Toast.makeText(this, "Няма наличен токен. Моля, влезте отново.", Toast.LENGTH_SHORT).show();

            // АВТОМАТИЧНО ПРЕНАСОЧВАНЕ КЪМ ЛОГИН
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Затваряме MainActivity, за да не може да се върне тук с Back бутона
        }
    }

    private void loadSportsHalls(String token) {


        // Сглобяваме правилния Header тук: "Bearer " + чистия токен
        String authorizationHeader = "Bearer " + token;

        // Правим GET заявката, като подаваме токена в Header-а
        ApiClient.getApiService().getAllHalls(authorizationHeader).enqueue(new Callback<List<SportsHall>>() {
            @Override
            public void onResponse(Call<List<SportsHall>> call, Response<List<SportsHall>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Данните дойдоха! Обновяваме списъка в адаптера
                    sportsHallList.clear();
                    sportsHallList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Казваме на екрана да се прерисува с новите зали

                } else {
                    Toast.makeText(MainActivity.this, "Сървърна грешка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SportsHall>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Мрежова грешка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}