package com.example.thegreatmugwump.taskmanager.Objects;

//Creates a TaskLineItem Object
public class TaskLineItem {

    private Integer taskLineItemID;
    private Integer taskLineItemQuantity;
    private Integer inventoryID;
    private Integer taskID;

    public TaskLineItem(Integer taskLineItemID,Integer taskLineItemQuantity, Integer inventoryID, Integer taskID) {

        this.taskLineItemID = taskLineItemID;
        this.taskLineItemQuantity = taskLineItemQuantity;
        this.inventoryID = inventoryID;
        this.taskID = taskID;
    }

    public Integer getTaskLineItemID() {
        return taskLineItemID;
    }

    public void setTaskLineItemID(int taskLineItemID) {
        this.taskLineItemID = taskLineItemID;
    }

    public Integer getTaskLineItemQuantity() { return taskLineItemQuantity; }

    public void setTaskLineItemQuantity(Integer taskLineItemQuantity) { this.taskLineItemQuantity = taskLineItemQuantity; }

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

}
