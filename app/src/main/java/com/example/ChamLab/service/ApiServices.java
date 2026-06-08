package com.example.ChamLab.service;

import com.example.ChamLab.model.Book;
import com.example.ChamLab.model.Response;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServices {

    @GET("/bookRouter/get-list-books")
    Call<Response<ArrayList<Book>>> getListBook();

    @POST("bookRouter/add-book")
    Call<Response<Book>> addBook(@Body Book book);

    @DELETE("bookRouter/delete-book-by-id/{id}")
    Call<Response<Book>> deleteBook(@Path("id") String id);

    @PUT("bookRouter/update-book-by-id/{id}")
    Call<Response<Book>> updateBook(@Path("id") String id, @Body Book book);
}
