package com.example.thegreatmugwump.taskmanager.Objects;
//Creates an InStock Object
public class InStock extends Inventory {

    private Integer inStockID;
    private Integer employeeID;
    private Integer quantity;

    public InStock(Integer invID, String invTitle, double price, Integer inStockID, Integer employeeID, int quantity) {
        super(invID, invTitle, price);
        this.inStockID = inStockID;
        this.employeeID = employeeID;
        this.quantity = quantity;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public Integer getInStockID() {
        return inStockID;
    }

    public void setInStockID(Integer inStockID) {
        this.inStockID = inStockID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

