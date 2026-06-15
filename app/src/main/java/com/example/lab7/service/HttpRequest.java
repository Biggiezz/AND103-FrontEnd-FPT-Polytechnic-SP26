package com.example.lab7.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private final ApiServices apiServices;

    public HttpRequest() {
        apiServices = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices.class);
    }

    public ApiServices callAPI() {
        return apiServices;
    }
}
