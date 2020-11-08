package com.andriod.cust.cure.bean;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    String token ;

    String refresh_Token ;

    @SerializedName("customer_id")
    Long customerId ;


    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getRefresh_Token() {
        return refresh_Token;
    }

    public void setRefresh_Token(String refresh_Token) {
        this.refresh_Token = refresh_Token;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
