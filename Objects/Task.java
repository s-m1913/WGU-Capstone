package com.example.thegreatmugwump.taskmanager.Objects;

//Creates a Task Object
public class Task {
    private Integer taskID;
    private int customerID;
    private int employeeID;
    private String taskDescription;
    private String taskResolution;
    private String status;

    public Task(Integer taskID, int customerID, int employeeID, String status, String taskDescription, String taskResolution) {
        this.taskID = taskID;
        this.customerID = customerID;
        this.employeeID = employeeID;
        this.status = status;
        this.taskDescription = taskDescription;
        this.taskResolution = taskResolution;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskResolution() {
        return taskResolution;
    }

    public void setTaskResolution(String taskResolution) {
        this.taskResolution = taskResolution;
    }

}
