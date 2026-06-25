package com.diploma.sporthallsapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.activity.LoginActivity;

public class ProfileFragment extends Fragment {
    private View btnLogout;
    private TextView tvProfileEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализираме елементите от твоя XML дизайн
        btnLogout = view.findViewById(R.id.btnLogout); // Провери дали ID-то съвпада с бутона за Изход
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail); // Ако имаш поле за имейл

        // Бутон за Изход - чисти токена и затваря сесията
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthentication();
    }

    private void checkAuthentication() {
        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        // Ако потребителят НЕ е логнат (няма токен), директно го препращаме към LoginActivity
        if (token == null || token.trim().isEmpty()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Тук можеш да добавиш код, който да пълни tvProfileEmail и tvProfileName,
            // ако ги дърпаш от базата или ги пазиш локално.
        }
    }

    private void handleLogout() {
        if (getActivity() == null) return;

        // Чистим токена от SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("token").apply();

        Toast.makeText(getContext(), "Успешен изход от профила", Toast.LENGTH_SHORT).show();

        // Пренасочваме веднага към LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
