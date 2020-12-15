package com.navigatpeer.authentication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit;
    private static final String BASE_URL = " "; //make this url configurable

    public static Retrofit getRetrofitInstance() {
        if (retrofit != null) {
            retrofit = new Retrofit.Builder().baseUrl(
                    BASE_URL
            ).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

    public static Api getApi() {
        return getRetrofitInstance().create(Api.class);
    }
}
