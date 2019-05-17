package com.example.ecommerce.models;

public class AdminOrders {

    private String address, city, name, phone, status, totalAmount, time, date;

    public AdminOrders() {
    }

    public AdminOrders(String address, String city, String name, String phone, String status, String totalAmount, String time, String date) {
        this.address = address;
        this.city = city;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.totalAmount = totalAmount;
        this.time = time;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
