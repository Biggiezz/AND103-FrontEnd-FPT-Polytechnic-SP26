package com.example.lab7.service;


import com.example.lab7.model.Distributor;
import com.example.lab7.model.Fruit;
import com.example.lab7.model.LoginData;
import com.example.lab7.model.Page;
import com.example.lab7.model.Response;
import com.example.lab7.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @POST("api/get-list-distributor")
    Call<Response<ArrayList<Distributor>>> getListDistributor();

    @Multipart
    @POST("fruitRouter/add-fruit-with-file-image")
    Call<Response<Fruit>> addFruit(
            @PartMap Map<String, RequestBody> body,
            @Part ArrayList<MultipartBody.Part> image
    );

    @PUT("api/update-fruit-by-id/{id}")
    Call<Response<Fruit>> updateFruit(@Path("id") String id, @Body Fruit fruit);

    @DELETE("fruitRouter/delete-fruit-by-id/{id}")
    Call<Response<Fruit>> deleteFruit(@Path("id") String id);

    @GET("fruitRouter/get-page-fruit")
    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit(@Header("Authorization") String token,
                                                        @Query("page") int page,
                                                        @Query("name") String name,
                                                        @Query("price") String price,
                                                        @Query("sort") int sort);

    @GET("fruitRouter/get-fruit-by-id/{id}")
    Call<Response<Fruit>> getFruitById(@Header("Authorization") String token,
                                       @Path("id") String id);
}
