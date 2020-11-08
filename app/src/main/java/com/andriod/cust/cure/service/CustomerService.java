package com.andriod.cust.cure.service;

import com.andriod.cust.cure.bean.AddRequestResponse;
import com.andriod.cust.cure.bean.Customer;
import com.andriod.cust.cure.bean.Item;
import com.andriod.cust.cure.bean.LoginRequest;
import com.andriod.cust.cure.bean.LoginResponse;
import com.andriod.cust.cure.bean.PharmacyResponse;
import com.andriod.cust.cure.bean.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CustomerService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request) ;


    @POST("auth/add_customer")
    Call<RegisterResponse> register(@Body Customer customer) ;


    @GET("/auth/refresh_token")
    Call<LoginResponse> refreshToken(@Query("token") String token) ;


    @POST("request/add")
    Call<AddRequestResponse> addRequest(@Header("Authorization") String authorization, @Body Item item) ;


    @GET("/customer/request_responses")
    Call<List<PharmacyResponse>> getResponses(@Header("Authorization") String authorization, @Query("request_id") Long requestId) ;
}
