package com.example.demoRetrofit.services;

import com.example.demoRetrofit.model.Response;
import com.example.demoRetrofit.model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServices {
    @GET("usersRouter/get-list-users")
    Call<Response<ArrayList<User>>> getListUser();

    @PUT("usersRouter/update-user-by-id/{id}")
    Call<Response<User>> updateUser(@Path("id") String id, @Body User user);

    @DELETE("usersRouter/delete-user-by-id/{id}")
    Call<Response<User>> deleteUser(@Path("id") String id);

    @GET("usersRouter/detail/{id}")
    Call<Response<User>> getUser(@Path("id") String id);

    @POST("usersRouter/add-user-with-link-image")
    Call<Response<User>> addUser(@Body User user);
}
