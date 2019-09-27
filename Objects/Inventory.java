package com.example.thegreatmugwump.taskmanager.Objects;

//Creates an Inventory Object
public class Inventory {

    private Integer invID;
    private String invTitle;
    private double price;

    public Inventory(Integer invID, String invTitle, double price) {
        this.invID = invID;
        this.invTitle = invTitle;
        this.price = price;
    }

    public Integer getInvID() { return invID; }

    public void setInvID(int invID) { this.invID = invID; }

    public String getInvTitle() { return invTitle; }

    public void setInvTitle(String invTitle) { this.invTitle = invTitle; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }
}
