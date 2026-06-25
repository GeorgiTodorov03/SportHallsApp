package com.diploma.sporthallsapp.api;

import com.diploma.sporthallsapp.model.LoginRequest;
import com.diploma.sporthallsapp.model.LoginResponse;
import com.diploma.sporthallsapp.model.RegisterRequest;
import com.diploma.sporthallsapp.model.ReservationRequest;
import com.diploma.sporthallsapp.model.ReservationResponse;
import com.diploma.sporthallsapp.model.SportsHall;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<ResponseBody> register(@Body RegisterRequest request);

    @GET("api/v1/halls")
    Call<List<SportsHall>> getAllHalls();
    // Бележка: При заявката за зали добавяме @Header, за да можем да изпратим нашия JWT токен ("Bearer ...")

    @POST("api/v1/reservations")
    Call<ResponseBody> createReservation(
            @Header("Authorization") String token,
            @Body ReservationRequest request
            );

    // Извличане на заетите резервации за конкретна зала и дата
    @GET("api/v1/halls/{id}/reservations")
    retrofit2.Call<java.util.List<ReservationResponse>> getOccupiedReservations(
            @retrofit2.http.Path("id") Long hallId,
            @retrofit2.http.Query("date") String date // формат "YYYY-MM-DD"
    );

    @GET("api/v1/halls/owner")
    Call<List<SportsHall>> getOwnerHalls(
            @Header("Authorization") String token
    );


    // 1. Вземане на всички зали, чакащи одобрение
    @GET("api/v1/admin/halls/pending")
    Call<List<SportsHall>> getPendingHalls(
            @Header("Authorization") String token
    );


    @GET("api/v1/reservations/my-reservations")
    Call<List<ReservationResponse>> getMyReservations(
            @Header("Authorization") String token
    );


}
