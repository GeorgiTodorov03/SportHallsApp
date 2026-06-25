package com.diploma.sporthallsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView rvRecommendedHalls;
    private SportsHallAdapter adapter;
    private List<SportsHall> recommendedHallsList = new ArrayList<>();
    private MaterialButton btnExploreHalls, btnAiAssistant;
    private TextView tvViewAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация на изгледите
        rvRecommendedHalls = view.findViewById(R.id.rvRecommendedHalls);
        btnExploreHalls = view.findViewById(R.id.btnExploreHalls);
        btnAiAssistant = view.findViewById(R.id.btnAiAssistant);
        tvViewAll = view.findViewById(R.id.tvViewAll);

        // Настройка на RecyclerView (Вертикален списък за препоръчани)
        rvRecommendedHalls.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SportsHallAdapter(recommendedHallsList);
        rvRecommendedHalls.setAdapter(adapter);

        // Навигация при клик на бутоните от банера
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);

        btnExploreHalls.setOnClickListener(v -> {
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_halls); // Превключва на таб "Зали"
        });

        tvViewAll.setOnClickListener(v -> {
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_halls);
        });

        btnAiAssistant.setOnClickListener(v -> {
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_ai); // Превключва на таб "AI"
        });

        loadRecommendedHalls();
    }

    private void loadRecommendedHalls() {

        // Зареждаме одобрените зали от бекенда (за гости не е нужен токен за този ендпоинт)
        ApiClient.getApiService().getAllHalls().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SportsHall>> call, @NonNull Response<List<SportsHall>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedHallsList.clear();
                    // Взимаме само първите 3 зали като "препоръчани", за да не препълваме началния екран
                    List<SportsHall> allHalls = response.body();
                    for (int i = 0; i < Math.min(allHalls.size(), 3); i++) {
                        recommendedHallsList.add(allHalls.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SportsHall>> call, Throwable t) {
                Toast.makeText(getContext(), "Грешка при връзката със сървъра", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
