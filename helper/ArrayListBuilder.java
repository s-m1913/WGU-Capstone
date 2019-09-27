package com.example.thegreatmugwump.taskmanager.helper;

import com.example.thegreatmugwump.taskmanager.Objects.Customer;
import com.example.thegreatmugwump.taskmanager.Objects.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ArrayListBuilder {

    public ArrayList<Task> BuildTask (JSONArray data){

        ArrayList<Task> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer taskID = jsonObject.getInt("TaskID");
                Integer customerID = jsonObject.getInt("CustomerID");
                Integer employeeID = jsonObject.getInt("EmployeeID");
                String status = jsonObject.getString("TaskStatus");
                String taskDescription = jsonObject.getString("TaskDescription");
                String taskResolution = jsonObject.getString("TaskResolution");

                list.add(new Task(taskID, customerID, employeeID, status, taskDescription, taskResolution));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Customer> BuildCustomer (JSONArray data){

        ArrayList<Customer> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer customerID = jsonObject.getInt("CustomerID");
                String customerName = jsonObject.getString("CustomerName");
                String customerAddress = jsonObject.getString("CustomerAddress");
                String customerCity = jsonObject.getString("CustomerCity");
                String CustomerState = jsonObject.getString("CustomerState");
                String customerZip = jsonObject.getString("CustomerZip");
                String CustomerContactName = jsonObject.getString("CustomerContactName");
                String customerPhone = jsonObject.getString("CustomerPhone");

                list.add(new Customer(customerID, customerName, customerAddress, customerCity, CustomerState, customerZip, CustomerContactName, customerPhone));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<TaskTimeStamp> BuildTaskTimeStamp (JSONArray data){

        ArrayList<TaskTimeStamp> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer TaskTimeStampID = jsonObject.getInt("TaskTimeStampID");
                Integer TaskID = jsonObject.getInt("TaskID");
                String Created = jsonObject.getString("TaskTimeStampCreated");
                String Enroute = jsonObject.getString("TaskTimeStampEnroute");
                String Start = jsonObject.getString("TaskTimeStampStart");
                String End = jsonObject.getString("TaskTimeStampEnd");
                Integer startMiles = jsonObject.getInt("TaskTimeStampStartMiles");
                Integer endMiles = jsonObject.getInt("TaskTimeStampEndMiles");

                list.add(new TaskTimeStamp(TaskTimeStampID, TaskID, Created, startMiles, endMiles, Enroute, Start, End));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<TaskLineItem> BuildTaskLineItem (JSONArray data){

        ArrayList<TaskLineItem> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer LineItemID = jsonObject.getInt("TaskLineItemID");
                Integer TaskID = jsonObject.getInt("TaskID");
                Integer InventoryID = jsonObject.getInt("InventoryID");
                Integer Quant = jsonObject.getInt("TaskLineItemQuant");

                list.add(new TaskLineItem(LineItemID, Quant, InventoryID, TaskID));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Inventory> BuildInventory (JSONArray data){

        ArrayList<Inventory> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer InventoryID = jsonObject.getInt("InventoryID");
                Double InventoryPrice = jsonObject.getDouble("InventoryPrice");
                String InventoryTitle = jsonObject.getString("InventoryTitle");

                list.add(new Inventory(InventoryID, InventoryTitle, InventoryPrice));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<InStock> BuildInStock (JSONArray data){

        ArrayList<InStock> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer InventoryID = jsonObject.getInt("InventoryID");
                Double InventoryPrice = jsonObject.getDouble("InventoryPrice");
                String InventoryTitle = jsonObject.getString("InventoryTitle");
                Integer InStockID = jsonObject.getInt("InStockInventoryID");
                Integer EmployeeID = jsonObject.getInt("InStockEmployeeID");
                Integer Quantity = jsonObject.getInt("InStockQuantity");

                list.add(new InStock(InventoryID, InventoryTitle, InventoryPrice, InStockID, EmployeeID, Quantity));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Order> BuildOrder(JSONArray data){

        ArrayList<Order> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer OrderID = jsonObject.getInt("OrderID");
                Integer TaskID = jsonObject.getInt("TaskID");
                String OrderDate = jsonObject.getString("OrderDate");
                String OrderReceived = jsonObject.getString("OrderReceived");
                String OrderStatus = jsonObject.getString("OrderStatus");
                String CustomerName = jsonObject.getString("CustomerName");

                list.add(new Order(OrderID, TaskID, OrderDate, OrderReceived, OrderStatus, CustomerName));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<OrderLineItem> BuildOrderLineItem (JSONArray data){

        ArrayList<OrderLineItem> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer OrderLineItemID = jsonObject.getInt("OrderLineItemID");
                Integer OrderID = jsonObject.getInt("OrderID");
                Integer OrderLineItemQuantity = jsonObject.getInt("OrderLineItemQuantity");
                Integer InventoryID = jsonObject.getInt("InventoryID");

                list.add(new OrderLineItem(OrderLineItemID, OrderLineItemQuantity, InventoryID, OrderID));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Employee> BuildEmployee(JSONArray data){

        ArrayList<Employee> list = new ArrayList<>();

        try{
            for (int i = 0; i < data.length(); i++) {

                JSONObject jsonObject = data.getJSONObject(i);

                Integer EmployeeID = jsonObject.getInt("EmployeeID");
                String EmployeeName = jsonObject.getString("EmployeeName");
                String EmployeePassword = jsonObject.getString("EmployeePassword");
                String EmployeePhone = jsonObject.getString("EmployeePhone");
                String EmployeeStatus = jsonObject.getString("EmployeeStatus");
                String EmployeeAddress = jsonObject.getString("EmployeeAddress");
                String EmployeeCity = jsonObject.getString("EmployeeCity");
                String EmployeeState = jsonObject.getString("EmployeeState");
                String EmployeeZip = jsonObject.getString("EmployeeZip");

                list.add(new Employee(EmployeeID, EmployeeName, EmployeePassword, EmployeePhone, EmployeeStatus, EmployeeAddress, EmployeeCity, EmployeeState, EmployeeZip));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}