package com.example.thegreatmugwump.taskmanager.Objects;

//Creates a Order Object
public class Order {

    private Integer orderID;
    private Integer taskID;
    private String date;
    private String received;
    private String status;
    private String customerName;


    public Order(Integer orderID, Integer taskID, String date, String received, String status, String customerName) {
        this.orderID = orderID;
        this.taskID = taskID;
        this.date = date;
        this.received = received;
        this.status = status;
        this.customerName = customerName;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
