package com.diploma.sporthallsapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diploma.sporthallsapp.MainActivity;
import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.LoginRequest;
import com.diploma.sporthallsapp.model.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализираме SharedPreferences за локално пазене на токена
        sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);

        // Проверка: Ако потребителят вече има запазен токен, прескачаме този екран директно към Главния екран
        if (sharedPreferences.getString("token", null) != null) {
            navigateToMain();
        }

        // Обвързваме XML елементите с Java кода
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Клик на бутона за Вход
        btnLogin.setOnClickListener(v -> handleLogin());

        // Клик за отиване на екран Регистрация
        tvRegisterLink.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Екранът за регистрация следва...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Моля попълнете всички полета!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Извикваме уеб заявката асинхронно
        ApiClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() != null) {

                    // Успешен вход! Сървърът ни връща токен и роля.
                    String token = response.body().getToken();
                    String role = response.body().getRole();

                    // Записваме JWT токена трайно в устройството
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.putString("role", role);
                    editor.apply();


                    // ДИНАМИЧНО ПРЕНАСОЧВАНЕ СПОРЕД РОЛЯТА
                    if ("OWNER".equals(role)) {
                        Intent intent = new Intent(LoginActivity.this, OwnerDashboardActivity.class);
                        Toast.makeText(LoginActivity.this, "Успешен вход!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else if ("ADMIN".equals(role)) {
                        Intent intent = new Intent(LoginActivity.this, AdminApprovalActivity.class);
                        Toast.makeText(LoginActivity.this, "Успешен вход!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        // По подразбиране (ROLE_USER) си отива в сегашния главен екран със залите
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(LoginActivity.this, "Успешен вход!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Грешен имейл или парола!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                // Изписва грешка при проблем с мрежата (напр. спрян сървър или грешно IP)
                Toast.makeText(LoginActivity.this, "Грешка при връзка със сървъра: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Затваряме LoginActivity, за да не може потребителят да се върне назад с бутона Back
    }
}
