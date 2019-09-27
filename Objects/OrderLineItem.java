package com.example.thegreatmugwump.taskmanager.Objects;

//Creates a OrderLineItem Object
public class OrderLineItem {
    private int orderLineItemID;
    private int OrderLineItemQuantity;
    private int inventoryID;
    private Integer orderID;

    public OrderLineItem(int orderLineItemID, int OrderLineItemQuantity, int inventoryID, Integer orderID) {
        this.orderLineItemID = orderLineItemID;
        this.OrderLineItemQuantity = OrderLineItemQuantity;
        this.inventoryID = inventoryID;
        this.orderID = orderID;
    }

    public Integer getOrderLineItemQuantity() { return OrderLineItemQuantity; }

    public void setOrderLineItemQuantity(int orderLineItemQuantity) { OrderLineItemQuantity = orderLineItemQuantity; }

    public int getOrderLineItemID() {
        return orderLineItemID;
    }

    public void setOrderLineItemID(int orderLineItemID) {
        this.orderLineItemID = orderLineItemID;
    }

    public int getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

}
