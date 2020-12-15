package com.navigatpeer.authentication;

import android.provider.CalendarContract;

import com.navigatpeer.models.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("/api/register")
    Call<ApiResponse<User>> registerUser(@Body User user);

    @POST("/api/login")
    Call<ApiResponse<User>> authenticateUser(@Body User user);

}