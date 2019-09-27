package com.example.thegreatmugwump.taskmanager.Objects;

//Creates a Customer Object
public class Customer {

    private Integer customerID;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String contact;
    private String phone;

    public Customer(Integer customerID, String name, String address, String city, String state, String zip, String contact, String phone) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.contact = contact;
        this.phone = phone;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
