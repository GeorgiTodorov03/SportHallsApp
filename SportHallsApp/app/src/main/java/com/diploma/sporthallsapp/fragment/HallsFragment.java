package com.diploma.sporthallsapp.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.adapter.SportsHallAdapter;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.SportsHall;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HallsFragment extends Fragment {
    private EditText etSearchHalls;
    private ChipGroup chipGroupSports;
    private RecyclerView rvAllHalls;
    private SportsHallAdapter adapter;

    private List<SportsHall> fullList = new ArrayList<>();       // Пази оригиналния списък от бекенда
    private List<SportsHall> filteredList = new ArrayList<>();   // Пази филтрирания списък, който виждаме на екрана

    private String currentSelectedSport = "Всички";
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_halls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearchHalls = view.findViewById(R.id.etSearchHalls);
        chipGroupSports = view.findViewById(R.id.chipGroupSports);
        rvAllHalls = view.findViewById(R.id.rvAllHalls);

        rvAllHalls.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SportsHallAdapter(filteredList);
        rvAllHalls.setAdapter(adapter);

        // 1. Слушател за търсачката (Филтрира при всяка написана буква)
        etSearchHalls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 2. Слушател за чиповете (филтри по спорт)
        chipGroupSports.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentSelectedSport = "Всички";
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chipAll) currentSelectedSport = "Всички";
                else if (id == R.id.chipFootball) currentSelectedSport = "Футбол";
                else if (id == R.id.chipBasketball) currentSelectedSport = "Баскетбол";
                else if (id == R.id.chipTennis) currentSelectedSport = "Тенис";
            }
            applyFilters();
        });

        loadAllHalls();
    }

    private void loadAllHalls() {
        ApiClient.getApiService().getAllHalls().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<SportsHall>> call, Response<List<SportsHall>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullList.clear();
                    fullList.addAll(response.body());
                    applyFilters(); // Първоначално прилагаме филтрите, за да зареди всичко
                }
            }

            @Override
            public void onFailure(Call<List<SportsHall>> call, Throwable t) {
                Toast.makeText(getContext(), "Грешка при извличане на зали", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Комбинирана логика за филтриране в реално време
    private void applyFilters() {
        filteredList.clear();

        for (SportsHall hall : fullList) {
            boolean matchesSport = currentSelectedSport.equals("Всички") ||
                    (hall.getType() != null && hall.getType().equalsIgnoreCase(currentSelectedSport));

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    (hall.getName() != null && hall.getName().toLowerCase().contains(currentSearchQuery)) ||
                    (hall.getLocation() != null && hall.getLocation().toLowerCase().contains(currentSearchQuery));

            // Ако залата отговаря и на двете условия, я добавяме в списъка
            if (matchesSport && matchesSearch) {
                filteredList.add(hall);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
