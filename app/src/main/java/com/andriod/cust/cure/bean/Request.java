package com.andriod.cust.cure.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Request {

    private Long id;

    private Date entryDate ;

    private Date lastModifyDate ;

    private Item item ;

    private Customer customer ;


    private List<PharmacyResponse> responses = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<PharmacyResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<PharmacyResponse> responses) {
        this.responses = responses;
    }
}
