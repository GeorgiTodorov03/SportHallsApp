package com.diploma.sporthallsapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diploma.sporthallsapp.MainActivity;
import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.LoginRequest;
import com.diploma.sporthallsapp.model.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_ROLE_TEST";

    private EditText etEmail, etPassword;
    private Button btnLoginSubmit;
    private MaterialButton btnGoogleLogin;
    private TextView btnBack, tvRegisterRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация на компонентите от новия XML дизайн
        btnBack = findViewById(R.id.btnBack);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);

        // Бутон Назад - затваря LoginActivity и връща госта в MainActivity
        btnBack.setOnClickListener(v -> finish());

        // Бутон за вход чрез REST API
        btnLoginSubmit.setOnClickListener(v -> handleLogin());

        // Бутон за Google Authentication (Логиката ще се имплементира на по-късен етап)
        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Интеграцията с Google предстои...", Toast.LENGTH_SHORT).show();
        });

        // Пренасочване към екрана за регистрация
        tvRegisterRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
        ApiClient.getApiService().login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();

                    // Запазване на JWT токена в SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", loginResponse.getToken());
                    editor.apply();

                    // Обработка на ролята и интелигентно пренасочване
                    navigateToDashboard(loginResponse);
                } else {
                    Toast.makeText(LoginActivity.this, "Невалидни потребителски данни!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {

                // Изписва грешка при проблем с мрежата (напр. спрян сървър или грешно IP)
                Toast.makeText(LoginActivity.this, "Грешка при връзка със сървъра: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToDashboard(LoginResponse loginResponse) {
        if (loginResponse.getRole() == null) {
            Toast.makeText(this, "Грешка: Бекендът не върна роля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Преобразуваме ролята в главни букви и премахваме излишни интервали
        String role = loginResponse.getRole().toUpperCase().trim();

        // Дебъг лог в Logcat за лесно проследяване при грешки в низовете
        Log.d(TAG, "Пристигаща роля от бекенда: -> " + role);


        Toast.makeText(this, "Успешен вход!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

