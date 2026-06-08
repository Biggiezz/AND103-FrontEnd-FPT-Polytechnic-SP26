package com.example.demoRetrofit.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    private ApiServices apiServices;

    public static String BASE_URL = "http://10.0.2.2:3000/";

    public HttpRequest() {
        apiServices = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiServices.class);
    }

    public ApiServices callAPI() {
        return apiServices;
    }
}
