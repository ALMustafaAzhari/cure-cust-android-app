package com.andriod.cust.cure.bean;

public class Pharmacy {

    private Long id;

    private String name ;

    private String phone ;

    private String address ;

    private String district ;

    private boolean expand ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean IsExpanded() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
