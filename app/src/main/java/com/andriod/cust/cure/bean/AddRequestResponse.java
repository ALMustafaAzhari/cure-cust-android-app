package com.andriod.cust.cure.bean;

public class AddRequestResponse {
    private Request request ;

    private Character status ;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }
}
