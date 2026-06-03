package com.diploma.sporthallsapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.ReservationRequest;
import com.diploma.sporthallsapp.model.ReservationResponse;
import com.diploma.sporthallsapp.model.SportsHall;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailAddress, tvDetailDescription, tvRating, tvWorkingHours, tvPrice;
    private ImageView ivHallImage;
    private Button btnBook;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private String selectedHourStr;
    private SportsHall hall;

    // Всички възможни часове за резервация
    private final String[] allHours = {
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Инициализиране на полетата
        ivHallImage = findViewById(R.id.ivHallImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvRating = findViewById(R.id.tvHallRating);
        tvWorkingHours = findViewById(R.id.tvHallWorkingHours);
        tvPrice = findViewById(R.id.tvHallPrice);
        btnBook = findViewById(R.id.btnBookNow);

        // Взимаме обекта, който адаптерът ни изпрати
        hall = (SportsHall) getIntent().getSerializableExtra("selected_hall");

        if (hall != null) {
            // Пълним екрана с реалните данни
            tvDetailName.setText(hall.getName());
            tvDetailAddress.setText(hall.getLocation());
            tvDetailDescription.setText(hall.getDescription()); // Увери се, че имаш getDescription() в модела
            tvRating.setText("⭐ " + hall.getRating());
            tvWorkingHours.setText("Работно време: " + hall.getWorkingHoursFrom() + " - " + hall.getWorkingHoursTo());
            tvPrice.setText("Цена: " + hall.getPricePerHour() + " лв/час");

            // Зареждане на снимката с Glide
            Glide.with(this)
                    .load(hall.getImageUrl())
                    .placeholder(R.drawable.placeholder_hall) // сложи някаква картинка по подразбиране в drawable
                    .into(ivHallImage);
        }

        btnBook.setOnClickListener(v -> {
            showDatePicker();
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedYear = year1;
            selectedMonth = monthOfYear + 1; // Месеците започват от 0
            selectedDay = dayOfMonth;

            // След като денят е избран, веднага проверяваме кои часове са заети
            checkOccupiedSlotsAndShowPicker();

        }, year, month, day);
        datePickerDialog.show();
    }

    // 2. Питаме бекенда за заетите часове и отваряме интелигентния диалог
    private void checkOccupiedSlotsAndShowPicker() {
        if (hall == null) return;

        // Форматираме датата за заявката към бекенда (YYYY-MM-DD)
        String requestDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay);

        // Заявка към ApiService (увери се, че имаш този метод в ApiService.java от предната стъпка)
        ApiClient.getApiService().getOccupiedReservations(hall.getId(), requestDate)
                .enqueue(new Callback<List<ReservationResponse>>() {
                    @Override
                    public void onResponse(Call<List<ReservationResponse>> call, Response<List<ReservationResponse>> response) {
                        List<String> occupiedHours = new ArrayList<>();

                        if (response.isSuccessful() && response.body() != null) {
                            for (ReservationResponse res : response.body()) {
                                // Парсваме от "2026-06-02T18:00:00" -> вземаме само "18:00"
                                if (res.getStartTime() != null && res.getStartTime().contains("T")) {
                                    String hour = res.getStartTime().split("T")[1].substring(0, 5);
                                    occupiedHours.add(hour);
                                }
                            }
                        }

                        // Преминаваме към показване на списъка с филтрираните часове
                        showSmartTimePickerDialog(occupiedHours);
                    }

                    @Override
                    public void onFailure(Call<List<ReservationResponse>> call, Throwable t) {
                        // При мрежова грешка показваме всички часове като свободни (за защита от забиване)
                        showSmartTimePickerDialog(new ArrayList<>());
                    }
                });
    }

    // 3. Отваряме интелигентния списък, в който заетите часове не могат да се избират
    private void showSmartTimePickerDialog(List<String> occupiedHours) {
        String[] displayItems = new String[allHours.length];
        boolean[] disabledItems = new boolean[allHours.length];

        for (int i = 0; i < allHours.length; i++) {
            if (occupiedHours.contains(allHours[i])) {
                displayItems[i] = allHours[i] + " - [ЗАЕТ]";
                disabledItems[i] = true; // Маркираме го като невалиден
            } else {
                displayItems[i] = allHours[i] + " - Свободен";
                disabledItems[i] = false;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изберете час за резервация");

        builder.setItems(displayItems, (dialog, which) -> {
            // Защита: Ако потребителят по някакъв начин кликне върху зает час
            if (disabledItems[which]) {
                Toast.makeText(DetailActivity.this, "Този час вече е зает!", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedHourStr = allHours[which];

            // Директно стартираме изпращането, тъй като часът е гарантирано свободен
            sendReservationToBackEnd();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendReservationToBackEnd() {

        // ВАЖНО: Проверка дали обектът hall не е null, за да не крашне тук!
        if (hall == null) {
            Toast.makeText(this, "Грешка: Липсват данни за залата!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("RESERVATION_TEST", "Методът sendReservationToBackend СТАРТИРА за зала ID: " + hall.getId());


        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Грешка: Липсва токен за оторизация!", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:00",
                selectedYear, selectedMonth, selectedDay, selectedHourStr);

        // 1. Създаваме обекта, който държи капсулираното ID на залата
        ReservationRequest.SportsHallRequest sportsHallReq = new ReservationRequest.SportsHallRequest(hall.getId());

        // 2. Подаваме го на конструктора на основната заявка заедно с датата
        ReservationRequest request = new ReservationRequest(sportsHallReq, formattedDateTime);
        String authHeader = "Bearer " + token;

        ApiClient.getApiService().createReservation(authHeader, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Резервацията е успешна!", Toast.LENGTH_LONG).show();
                    finish(); // Връща потребителя обратно в списъка
                } else {
                    try {
                        // Взимаме реалния текст на грешката от сървъра
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Празна грешка";
                        Toast.makeText(DetailActivity.this, "Код " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                        android.util.Log.e("SERVER_ERROR", "Грешка от сървъра: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(DetailActivity.this, "Грешка при резервация: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Мрежова грешка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
