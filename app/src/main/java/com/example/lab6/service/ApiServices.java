package com.example.lab6.service;

import com.example.lab6.model.Distributor;
import com.example.lab6.model.Fruit;
import com.example.lab6.model.LoginData;
import com.example.lab6.model.Response;
import com.example.lab6.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiServices {
    @Multipart
    @POST("usersRouter/add-user-with-link-image")
    Call<Response<User>> register(@Part("userName") RequestBody username,
                                  @Part("password") RequestBody password,
                                  @Part("email") RequestBody email,
                                  @Part("name") RequestBody name,
                                  @Part MultipartBody.Part avatar);

    @POST("jsonwebtokenRouter/login")
    Call<Response<LoginData>> login(@Body User user);

    @GET("fruitRouter/get-list-fruit")
    Call<Response<ArrayList<Fruit>>> getListFruit(@Header("Authorization") String token);

    @GET("distributorRouter/get-list-distributor")
    Call<com.example.lab6.model.Response<ArrayList<Distributor>>> getListDistributor();

    @Multipart
    @POST("fruitRouter/add-fruit-with-file-image")
    Call<Response<Fruit>> addFruit(
            @PartMap Map<String, RequestBody> body,
            @Part ArrayList<MultipartBody.Part> image
    );
}
