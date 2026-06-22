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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.activity.LoginActivity;
import com.diploma.sporthallsapp.adapter.SportsHallAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.fragment.AiAgentFragment;
import com.diploma.sporthallsapp.fragment.HallsFragment;
import com.diploma.sporthallsapp.fragment.HomeFragment;
import com.diploma.sporthallsapp.fragment.ProfileFragment;
import com.diploma.sporthallsapp.fragment.ReservationsFragment;
import com.diploma.sporthallsapp.model.SportsHall;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Слушател за кликове върху иконите долу
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            // 1. Свободни екрани (Достъпни за гости)
            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_halls) {
                selectedFragment = new HallsFragment();
            }
            // 2. Защитени екрани (Изискват логнат профил)
            else if (id == R.id.nav_reservations || id == R.id.nav_ai || id == R.id.nav_profile) {
                if (isUserLoggedIn()) {
                    if (id == R.id.nav_reservations) selectedFragment = new ReservationsFragment();
                    if (id == R.id.nav_ai) selectedFragment = new AiAgentFragment();
                    if (id == R.id.nav_profile) selectedFragment = new ProfileFragment();
                } else {
                    // Ако не е логнат -> отваряме новия лъскав LoginActivity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false; // Връщаме false, за да не се премести селекцията на долната лента
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // По подразбиране зареждаме HomeFragment при първоначално отваряне
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void loadSportsHalls(String token) {

        // Правим GET заявката, като подаваме токена в Header-а
        ApiClient.getApiService().getAllHalls().enqueue(new Callback<>() {
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

    // Помощен метод, който проверява дали имаме валиден токен в SharedPreferences
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null;
    }
}