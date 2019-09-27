package com.example.thegreatmugwump.taskmanager.Objects;

//Creates an Employee Object
public class Employee{

    private Integer EmployeeID;
    private String name;
    private String password;
    private String phone;
    private String status;
    private String address;
    private String city;
    private String state;
    private String zip;

    public Employee(Integer employeeID, String name, String password, String phone, String status, String address, String city, String state, String zip) {

        EmployeeID = employeeID;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.status = status;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public Integer getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        EmployeeID = employeeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
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

}
