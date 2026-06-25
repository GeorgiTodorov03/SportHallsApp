package com.diploma.sporthallsapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.RegisterRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    private SwitchCompat switchIsOwner;
    private Button btnRegisterSubmit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etRegFirstName);
        etLastName = findViewById(R.id.etRegLastName);
        etEmail = findViewById(R.id.etRegEmail);
        etPhone = findViewById(R.id.etRegPhone);
        etPassword = findViewById(R.id.etRegPassword);
        switchIsOwner = findViewById(R.id.switchIsOwner);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);

        btnRegisterSubmit.setOnClickListener(v -> executeRegistration());
    }

    private void executeRegistration() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean isOwner = switchIsOwner.isChecked();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Моля, попълнете всички полета", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(email, password, firstName, lastName, phone, isOwner);

        ApiClient.getApiService().register(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Регистрацията е успешна!", Toast.LENGTH_LONG).show();
                    finish(); // Затваряме екрана и се връщаме към Login
                } else {
                    Toast.makeText(RegisterActivity.this, "Грешка при регистрация: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Мрежова грешка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
