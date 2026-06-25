package com.diploma.sporthallsapp.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.diploma.sporthallsapp.R;
import com.diploma.sporthallsapp.api.ApiClient;
import com.diploma.sporthallsapp.model.ReservationRequest;
import com.diploma.sporthallsapp.model.ReservationResponse;
import com.diploma.sporthallsapp.model.SportsHall;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView btnDetailBack, tvDetailName, tvDetailSportType, tvDetailPrice, tvDetailLocation, tvDetailRating, tvDetailWorkingHours;
    private ImageView ivDetailImage;
    private MaterialButton btnBookNow;

    private GoogleMap googleMap;
    private SportsHall currentHall;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Извличане на обекта от Интента
        currentHall = (SportsHall) getIntent().getSerializableExtra("selected_hall");

        // Инициализиране на UI елементите
        btnDetailBack = findViewById(R.id.btnDetailBack);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSportType = findViewById(R.id.tvDetailSportType);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailWorkingHours = findViewById(R.id.tvDetailWorkingHours);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        btnBookNow = findViewById(R.id.btnBookNow);

        // Зареждане на Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Попълване на данните за залата
        if (currentHall != null) {
            tvDetailName.setText(currentHall.getName());
            tvDetailSportType.setText(currentHall.getType());
            tvDetailPrice.setText(currentHall.getPricePerHour() + " лв / час");
            tvDetailLocation.setText(currentHall.getLocation());
            tvDetailRating.setText(String.valueOf(currentHall.getRating()));
            tvDetailWorkingHours.setText("Работно време: " + currentHall.getWorkingHoursFrom() + "-" + currentHall.getWorkingHoursTo());
        }

        // Връщане назад
        btnDetailBack.setOnClickListener(v -> finish());

        // Бутон за Резервация - Стартира процеса по избор на дата и час
        btnBookNow.setOnClickListener(v -> checkAuthAndPickDate());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        if (googleMap == null) return;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Примерни координати (В реална среда се взимат от текущата зала, ако имаш гео-данни в базата)
        LatLng hallCoordinates = new LatLng(42.6977, 23.3219);

        String markerTitle = "Спортен терен";
        if (currentHall != null && currentHall.getName() != null) {
            markerTitle = currentHall.getName();
        }
        googleMap.addMarker(new MarkerOptions().position(hallCoordinates).title(markerTitle));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hallCoordinates, 15f));
    }

    // Проверка дали потребителят е логнат преди да резервира (Заради Guest режима)
    private void checkAuthAndPickDate() {
        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Трябва да влезеш в профила си, за да направиш резервация!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(DetailActivity.this, LoginActivity.class));
            return;
        }

        // Ако е логнат, отваряме календара
        showDatePicker();
    }

    // 1. Диалог за избор на Дата
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Форматираме датата във вид: YYYY-MM-DD
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (month1 + 1), dayOfMonth);

            // След като избере дата, веднага отваряме избора за час
            showTimePicker();
        }, year, month, day);

        datePickerDialog.setTitle("Избери дата за резервация");
        datePickerDialog.show();
    }

    // 2. Диалог за избор на Час
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            // Форматираме часа във вид: HH:MM
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);

            // Имаме дата и час -> Изпращаме заявката
            sendReservationToBackend();
        }, hour, minute, true);

        timePickerDialog.setTitle("Избери начален час");
        timePickerDialog.show();
    }

    // 3. Изпращане на данните към Spring Boot чрез Retrofit
    private void sendReservationToBackend() {
        if (currentHall == null) return;

        SharedPreferences sharedPreferences = getSharedPreferences("SportHallsPrefs", Context.MODE_PRIVATE);
        String rawToken = sharedPreferences.getString("token", null);
        String token = "Bearer " + rawToken;

        ReservationRequest.SportsHallRequest sportsHallReq = new ReservationRequest.SportsHallRequest(currentHall.getId());
        sportsHallReq.setId(currentHall.getId()); // Задаваме ID-то на залата

        // Създаваме DTO тялото за заявката
        ReservationRequest request = new ReservationRequest(sportsHallReq, selectedTime);

        ApiClient.getApiService().createReservation(token, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Резервацията е изпратена за одобрение!", Toast.LENGTH_LONG).show();
                    finish(); // Затваряме екрана и се връщаме в списъка
                } else {
                    Toast.makeText(DetailActivity.this, "Часът е зает или невалиден!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Грешка при мрежова комуникация", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
