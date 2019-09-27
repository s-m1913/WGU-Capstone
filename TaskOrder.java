package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.*;
import com.example.thegreatmugwump.taskmanager.helper.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TaskOrder extends AppCompatActivity {

    private TableLayout inventoryTable;
    private TableLayout orderTable;
    private TextView orderTotal;
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private ArrayList<Inventory> inventory = new ArrayList<>();
    private ArrayList <InStock> inStock = new ArrayList<>();
    private ArrayList <Order> order = new ArrayList<>();
    private ArrayList <String> queryArray = new ArrayList<>();
    private int taskID;
    private int orderID;
    private int employeeID;
    private static DecimalFormat df2 = new DecimalFormat("#.00");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
    private String query = "";
    private String subURL;
    private String dataMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_task_line_item);

        assert getSupportActionBar() != null;

        taskID = getIntent().getExtras().getInt("taskID");
        employeeID = Integer.parseInt(TaskList.employeeID);
        findElements();
        getInStock();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Cancel");
        menu.add("Submit Order");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit Order")) createOrder();

        if (item.getTitle().toString().equals("Cancel"))loadTaskDetails();

        return true;
    }

    private void findElements(){//Assign layout items to variables

        inventoryTable = findViewById(R.id.OTL_InventoryList);
        orderTable = findViewById(R.id.OTL_TransferList);
        orderTotal = findViewById((R.id.OTL_orderTotal));
    }

    private void getInStock(){

        dataMethod = "getInStock";
        subURL = "getInStock.php/";
        query = "SELECT * FROM Instock a left outer join Inventory b on a.InStockInventoryID = b.InventoryID WHERE a.InStockEmployeeID = " + employeeID + ";";

        new databaseInterface().execute();
    }

    private void getInventory(){

        dataMethod = "getInventory";
        subURL = "getInventory.php/";
        query = "SELECT * FROM inventory";

        new databaseInterface().execute();
    }

    private void loadElements(){//populate the layout items with order details

        Integer quant;
        Double price = 0.00;
        int count=0;

        orderTotal.setText("Total $0.00");
        for (Inventory i : inventory) {

            quant = 0;
            price = i.getPrice();

            TableRow tr = new TableRow(this);
            tr.setId(i.getInvID());
            tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow selectedRow = (TableRow) view;
                    TextView tv0 = (TextView) selectedRow.getChildAt(0);
                    TextView tv2 = (TextView) selectedRow.getChildAt(2);
                    String title = tv0.getText().toString();
                    String price = tv2.getText().toString();
                    addToOrder(title, price, selectedRow.getId());
                }
            });

            if(count%2!=0) tr.setBackgroundColor(Color.LTGRAY);

            for (InStock s : inStock){
                if (i.getInvID().equals(s.getInvID())) quant = s.getQuantity();
            }

            TextView itemCol = new TextView(this);
            itemCol.setTextColor(Color.BLACK);
            itemCol.setText(i.getInvTitle());
            itemCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            itemCol.setPadding(10, 10, 10, 10);
            tr.addView(itemCol);

            TextView quantCol = new TextView(this);
            quantCol.setTextColor(Color.BLACK);
            quantCol.setText(quant.toString());
            quantCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            quantCol.setPadding(10, 10, 10, 10);
            tr.addView(quantCol);

            TextView priceCol = new TextView(this);
            priceCol.setTextColor(Color.BLACK);
            priceCol.setText("$" + df2.format(price));
            priceCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            priceCol.setPadding(10, 10, 10, 10);
            tr.addView(priceCol);

            inventoryTable.addView(tr);
            count ++;
        }
        highlightRows();
        updateTotal();
    }

    private void createOrder(){

        LocalDateTime date = LocalDateTime.now();
        String Date = dtf.format(date);

        dataMethod = "createOrder";
        subURL = "insertEditDelete.php/";
        query = "INSERT INTO inventoryorder (TaskID, OrderDate, OrderStatus, OrderReceived) VALUES (" + taskID + ", '" + Date + "', 'Pending', 'No');";

        new databaseInterface().execute();
    }

    private void getOrder(){

        dataMethod = "getOrder";
        subURL = "getOrder.php/";
        query = "SELECT * FROM inventoryorder WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void updateTask(){

        dataMethod = "updateTask";
        subURL = "insertEditDelete.php/";
        query = "UPDATE task" +
                " SET TaskStatus = 'Parts On Order'" +
                " WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void submitLineItems(){

        String title;
        int quantity;
        int invID = 0;
        dataMethod = "submitLineItems";

        for (Order o : order){
            orderID = o.getOrderID();
        }

        for (int i = 1; i < orderTable.getChildCount(); i++) {

            View view = orderTable.getChildAt(i);

            if (view instanceof TableRow) {

                TableRow thisRow = (TableRow) view;

                TextView tv0 = (TextView) thisRow.getChildAt(0);
                TextView tv1 = (TextView) thisRow.getChildAt(1);

                title = tv0.getText().toString();
                quantity = Integer.parseInt(tv1.getText().toString());

                //Add new parts to the order

                for (Inventory in : inventory) {
                    if (in.getInvTitle().equals(title)) invID = in.getInvID();
                }

                queryArray.add ("INSERT INTO orderlineitem (OrderID, InventoryID, OrderLineItemQuantity) " +
                        "VALUES (" + orderID + ", " + invID + ", " + quantity + ");");

            }
        }
        new databaseInterface().execute();
    }

    private void addToOrder(String title, String price, int rowID){

        Integer quant = 1;
        Double lineSum;
        Double linePrice;

        for (int i = 0; i < orderTable.getChildCount(); i++) {

            View view1 =  orderTable.getChildAt(i);

            //Check existing rows
            if (view1 instanceof TableRow) {
                TableRow thisRow = (TableRow) view1;
                TextView tv0 = (TextView) thisRow.getChildAt(0);

                //increment quantity on existing rows
                if (tv0.getText().toString().equals(title)) {

                    TextView tv1 = (TextView) thisRow.getChildAt(1);
                    TextView tv2 = (TextView) thisRow.getChildAt(2);
                    TextView tv3 = (TextView) thisRow.getChildAt(3);

                    quant = Integer.parseInt(tv1.getText().toString()) + 1;
                    linePrice = Double.parseDouble(tv2.getText().toString().substring(1));
                    lineSum = quant * linePrice;

                    tv1.setText(quant.toString());
                    tv3.setText("$" + df2.format(lineSum));
                }
            }
        }

        //Add new row
        if (quant.equals(1)) {

            TableRow tr = new TableRow(this);
            tr.setId(rowID);

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow selectedRow = (TableRow) view;
                    TextView tv0 = (TextView) selectedRow.getChildAt(0);
                    String title = tv0.getText().toString();
                    subtractFromOrder(title);
                }
            });

            TextView itemCol = new TextView(this);
            itemCol.setTextColor(Color.BLACK);
            itemCol.setText(title);
            itemCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            itemCol.setPadding(10, 10, 10, 10);
            tr.addView(itemCol);

            TextView quantCol = new TextView(this);
            quantCol.setTextColor(Color.BLACK);
            quantCol.setText(quant.toString());
            quantCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            quantCol.setPadding(10, 10, 10, 10);
            tr.addView(quantCol);

            TextView priceCol = new TextView(this);
            priceCol.setTextColor(Color.BLACK);
            priceCol.setText(price);
            priceCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            priceCol.setPadding(10, 10, 10, 10);
            tr.addView(priceCol);

            TextView lineTotalCol = new TextView(this);
            lineTotalCol.setTextColor(Color.BLACK);
            lineTotalCol.setText(price);
            lineTotalCol.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lineTotalCol.setPadding(10, 10, 10, 10);
            tr.addView(lineTotalCol);

            orderTable.addView(tr);
        }
        updateTotal();
        highlightRows();
    }

    private void subtractFromOrder(String title){

        Integer quant;
        Double price;

        for (int i = 0; i < orderTable.getChildCount(); i++) {

            View view =  orderTable.getChildAt(i);

            if (view instanceof TableRow) {

                TableRow thisRow = (TableRow) view;
                TextView tv0 = (TextView) thisRow.getChildAt(0);

                if (tv0.getText().toString().equals(title)) {

                    TextView tv1 = (TextView) thisRow.getChildAt(1);
                    TextView tv2 = (TextView) thisRow.getChildAt(2);
                    TextView tv3 = (TextView) thisRow.getChildAt(3);
                    quant = Integer.parseInt(tv1.getText().toString()) - 1;
                    price = Double.parseDouble(tv2.getText().toString().substring(1)) * quant;
                    tv3.setText("$" + df2.format(price));

                    if (quant.equals(0)) {

                        orderTable.removeView(view);

                    }else {

                        tv1.setText(quant.toString());
                    }
                }
            }
        }
        updateTotal();
        highlightRows();
    }

    private void highlightRows(){

        int count = 0;
        for (int i = 0; i < inventoryTable.getChildCount(); i++) {

            View view1 = inventoryTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }

        count = 0;
        for (int i = 0; i < orderTable.getChildCount(); i++) {

            View view1 = orderTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private void updateTotal(){

        Double total = 0.00;

        for (int i = 1; i < orderTable.getChildCount(); i++) {

            View view1 = orderTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                TableRow thisRow = (TableRow) view1;
                TextView tv3 = (TextView) thisRow.getChildAt(3);
                total = total + Double.parseDouble(tv3.getText().toString().substring(1));
            }
        }

        orderTotal.setText("$" + df2.format(total));
    }

    private void loadTaskDetails(){

        Intent i = new Intent(this, TaskDetails.class);
        i.putExtra("taskID",taskID);
        startActivity(i);
    }

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        int success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(TaskOrder.this);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = new JSONObject();

            if (queryArray.size() == 0) jsonObject = httpJsonParser.makeHttpRequest(subURL, query);

            for (String s : queryArray){
                jsonObject = httpJsonParser.makeHttpRequest(subURL, s);
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

            if (dataMethod.equals("getInStock")) {

                inStock = arrayListBuilder.BuildInStock(data);
                getInventory();

            }else if (dataMethod.equals("getInventory")){

                inventory = arrayListBuilder.BuildInventory(data);
                loadElements();

            }else if (dataMethod.equals("createOrder")){

                getOrder();

            }else if (dataMethod.equals("getOrder")){

                order = arrayListBuilder.BuildOrder(data);
                updateTask();

            }else if (dataMethod.equals("updateTask")){

                submitLineItems();

            }else if (dataMethod.equals("submitLineItems")){

                loadTaskDetails();
            }
        }
    }
}
