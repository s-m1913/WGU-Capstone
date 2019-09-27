package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.InStock;
import com.example.thegreatmugwump.taskmanager.Objects.Inventory;
import com.example.thegreatmugwump.taskmanager.Objects.Order;
import com.example.thegreatmugwump.taskmanager.Objects.OrderLineItem;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrderList extends AppCompatActivity {

    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<OrderLineItem> orderLineItems = new ArrayList<>();
    private ArrayList<Inventory> inventory = new ArrayList<>();
    private ArrayList<InStock> inStock = new ArrayList<>();
    private ArrayList <String> queryArray = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private String orderID;
    private TableLayout orderSelect;
    private TableLayout lineItem;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
    private String query = "";
    private String subURL;
    private String dataMethod;
    private int employeeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        assert getSupportActionBar() != null;

        employeeID = Integer.parseInt(TaskList.employeeID);

        findElements();
        getOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Inventory List");
        menu.add("Accept Order");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Accept Order")) submitLineItems() ;

        if (item.getTitle().toString().equals("Inventory List")) loadInventoryList();

        return true;
    }

    private void loadInventoryList(){

        Intent i = new Intent(this, InventoryList.class);
        startActivity(i);
    }

    private void getOrder(){

        dataMethod = "getOrder";
        subURL = "getOrder.php/";
        query = "SELECT c.CustomerName, o.OrderID, o.TaskID, o.OrderDate, o.OrderStatus, o.OrderReceived" +
                " FROM inventoryorder o, task t,  customer c" +
                " WHERE o.TaskID = t.TaskID" +
                " AND t.EmployeeID =  " + employeeID +
                " AND c.CustomerID = t.CustomerID" +
                " AND o.OrderStatus = 'Pending';";

        new databaseInterface().execute();
    }

    private void getInventory(){

        dataMethod = "getInventory";
        subURL = "getInventory.php/";
        query = "SELECT * FROM inventory";

        new databaseInterface().execute();
    }

    private void getInStock(){

        dataMethod = "getInStock";
        subURL = "getInStock.php/";
        query = "SELECT * FROM instock a left outer join Inventory b on a.InStockInventoryID = b.InventoryID WHERE a.InStockEmployeeID = " + employeeID + ";";

        new databaseInterface().execute();
    }

    private void getOrderLineItems(int id){

        dataMethod = "getOrderLineItems";
        subURL = "getOrderLineItem.php/";
        query = "SELECT i.OrderLineItemID, i.OrderID, i.InventoryID, i.OrderLineItemQuantity " +
                " FROM orderlineitem i, inventoryorder o, task t " +
                " WHERE o.OrderStatus = 'Pending' " +
                " AND o.OrderID = " + id +
                " AND o.TaskID = t.TaskID AND t.EmployeeID = " + employeeID + ";";

        new databaseInterface().execute();
    }

    private void findElements(){//Assign layout items to variables

        orderSelect = findViewById(R.id.Order_Selector);
        lineItem = findViewById(R.id.Order_LineItems);
    }

    private void loadElements(){//populate the layout items with order details

        String [] data = new String[3];

        for (Order o : orders){

            data[0] = o.getCustomerName();
            data[1] = o.getDate();
            data[2] = o.getOrderID().toString();
            TableRow tr = newTableRow(data);

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow selectedRow = (TableRow) view;
                    TextView tv2 = (TextView) selectedRow.getChildAt(2);
                    orderID = tv2.getText().toString();
                    getOrderLineItems(Integer.parseInt(orderID));
                    highlightAllRows();
                    view.setBackgroundColor(Color.YELLOW);
                }
            });

            orderSelect.addView(tr);
        }
        highlightAllRows();
    }

    private void loadSelectedOrder(){

        String[] data  = new String[2];

        //Remove previous order from table view
        lineItem.removeAllViews();
        data[0] = "Part";
        data[1] = "Quantity";
        TableRow tr = newTableRow(data);
        lineItem.addView(tr);


        //Load TableView with orderLines matching the orderID
        for (OrderLineItem o : orderLineItems){

            if (o.getOrderID().equals(Integer.parseInt(orderID))){

                for (Inventory i : inventory){

                    if (i.getInvID().equals(o.getInventoryID())){

                        data[0] = i.getInvTitle();
                        data[1] = o.getOrderLineItemQuantity().toString();

                        tr = newTableRow(data);
                        tr.setId(i.getInvID());
                        lineItem.addView(tr);

                    }
                }
            }
        }
        highlightBottomRows();
    }

    private void submitLineItems(){//submit order to the database

        String title;
        int quantity;
        int invID = 0;
        boolean entered;
        int instockQuantity = 0;

        dataMethod = "submitLineItems";
        subURL = "insertEditDelete.php/";
        queryArray.add("UPDATE inventoryorder SET OrderStatus = 'Received', OrderReceived = 'Yes';");

        for (int i = 1; i < lineItem.getChildCount(); i++) {

            View view = lineItem.getChildAt(i);
            entered = false;

            if (view instanceof TableRow) {

                TableRow thisRow = (TableRow) view;

                TextView tv0 = (TextView) thisRow.getChildAt(0);
                TextView tv1 = (TextView) thisRow.getChildAt(1);

                quantity = Integer.parseInt(tv1.getText().toString());

                for (InStock in : inStock) {

                    if (in.getInvID().equals(thisRow.getId())){
                        invID = in.getInvID();
                        instockQuantity = in.getQuantity();
                        entered = true;
                    }
                }
                if (entered) {

                    quantity = quantity + instockQuantity;

                    queryArray.add("UPDATE instock SET InStockQuantity = " + quantity +
                            " WHERE InStockInventoryID = " + invID +
                            " AND InStockEmployeeID = " + employeeID + ";");
                }else {

                    queryArray.add("INSERT INTO instock (InStockInventoryID, InStockQuantity, InStockEmployeeID) " +
                            "VALUES (" + thisRow.getId() + ", " + quantity + ", " + employeeID + ");");
                }
            }
        }

        lineItem.removeAllViews();
        orderSelect.removeAllViews();
        orders.clear();
        orderLineItems.clear();
        new databaseInterface().execute();
    }

    private TableRow newTableRow(String[] data){

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (String s : data) {

            TextView tv = newTextView(s);
            tr.addView(tv);
        }

        return tr;
    }

    private TextView newTextView(String data){

        TextView tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setText(data);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setPadding(10, 10, 10, 10);

        return tv;
    }

    private void highlightAllRows(){

        int count = 0;
        for (int i = 0; i < orderSelect.getChildCount(); i++) {

            View view1 = orderSelect.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }

        count = 0;
        for (int i = 0; i < lineItem.getChildCount(); i++) {

            View view1 = lineItem.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private void highlightBottomRows(){

        int count = 0;

        for (int i = 0; i < lineItem.getChildCount(); i++) {

            View view1 = lineItem.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        int success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(OrderList.this);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = new JSONObject();
            Log.e("DataMethod<><><>", dataMethod);
            if (queryArray.size() == 0){ jsonObject = httpJsonParser.makeHttpRequest(subURL, query);

            }else {

                for (String s : queryArray) {
                    jsonObject = httpJsonParser.makeHttpRequest(subURL, s);
                }
                queryArray.clear();
            }

            try {

                success = jsonObject.getInt("success");

                if (success == 1 && jsonObject.getJSONArray("data") != null) {

                    data = jsonObject.getJSONArray("data");

                } else if (success == 0){

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.GONE);


            if (dataMethod.equals("getOrder")) {

                if (success == 1) orders = arrayListBuilder.BuildOrder(data);
                getInventory();

            }else if (dataMethod.equals("getInventory")){

                if (success == 1) inventory = arrayListBuilder.BuildInventory(data);
                getInStock();

            }else if (dataMethod.equals("getOrderLineItems")) {

                if (success == 1) orderLineItems = arrayListBuilder.BuildOrderLineItem(data);
                loadSelectedOrder();

            }else if (dataMethod.equals("submitLineItems")){

                getOrder();

            }else if (dataMethod.equals("getInStock")){

                if (success == 1) inStock = arrayListBuilder.BuildInStock(data);

                loadElements();
            }
        }
    }
}
