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
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.Employee;
import com.example.thegreatmugwump.taskmanager.Objects.InStock;
import com.example.thegreatmugwump.taskmanager.Objects.Inventory;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryList extends AppCompatActivity {

    private TableLayout inventoryTable;
    private TableLayout transferTable;
    private Spinner receiver;
    private ArrayList<InStock> sender = new ArrayList<>();
    private ArrayList<InStock> receiverInv = new ArrayList<>();
    private ArrayList <Inventory> inventory = new ArrayList<>();
    private ArrayList <Employee> employees = new ArrayList<>();
    private ArrayList <String> queryArray = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private Integer employeeID;
    private Integer receiverID = 0;
    private static DecimalFormat df = new DecimalFormat("#.00");
    private String query = "";
    private String subURL;
    private String dataMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        assert getSupportActionBar() != null;

        employeeID = Integer.parseInt(TaskList.employeeID);
        findElements();
        getInventory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Task List");
        menu.add("Receive Orders");
        menu.add("Submit Transfer");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit Transfer")) {

            for (Employee e : employees) {

                if (e.getName().equals(receiver.getSelectedItem().toString())) receiverID = e.getEmployeeID();
            }

            getInStock(receiverID, "receiver");
        }

        if (item.getTitle().toString().equals("Task List")) {

            loadTaskList();
        }

        if (item.getTitle().toString().equals("Receive Orders")) {

            loadOrderList();
        }

        return true;
    }

    private void findElements(){//Assign layout items to variables

        inventoryTable = findViewById(R.id.OHI_InventoryList);
        transferTable = findViewById(R.id.OHI_TransferList);
        receiver = findViewById(R.id.OHI_Receiver);
    }

    private void loadElements(){//populate the layout items with inventory details

        Integer quant;
        Double price;
        int count = 1;
        String[] data = new String[3];

        //Load inventory list
        for (InStock in : sender) {

            if (in.getEmployeeID() == employeeID && in.getQuantity() > 0) {

                quant = in.getQuantity();
                price = in.getPrice();

                data[0] = in.getInvTitle();
                data[1] = quant.toString();
                data[2] = "$" + df.format(price);

                TableRow tr = newTableRow(data,count);
                tr.setId(in.getInvID());

                tr.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        TableRow selectedRow = (TableRow) view;
                        TextView tv0 = (TextView) selectedRow.getChildAt(0);
                        TextView tv2 = (TextView) selectedRow.getChildAt(2);
                        String title = tv0.getText().toString();
                        String price = tv2.getText().toString();
                        addToTransfer(title, price, view);
                    }
                });

                inventoryTable.addView(tr);
                count++;
            }
        }

        List<String> employeeList = new ArrayList<>();

        for (Employee e : employees){
            if (!e.getEmployeeID().equals(employeeID)) {

                employeeList.add(e.getName());
            }
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(Objects.requireNonNull(this), android.R.layout.simple_spinner_item, employeeList);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.receiver.setAdapter(adp);
    }

    private void getInventory(){

        dataMethod = "getInventory";
        subURL = "getInventory.php/";
        query = "SELECT * FROM inventory";

        new databaseInterface().execute();
    }

    private void getInStock(int employee, String label){

        dataMethod = label;
        subURL = "getInStock.php/";
        query = "SELECT * FROM instock a left outer join Inventory b on a.InStockInventoryID = b.InventoryID WHERE a.InStockEmployeeID = " + employee + ";";

        new databaseInterface().execute();
    }

    private void getEmployees(){

        dataMethod = "getEmployees";
        subURL = "getEmployees.php/";
        query = "SELECT * FROM employee;";

        new databaseInterface().execute();
    }

    private void addToTransfer(String title, String price, View selectedView){//Adds a part to the transfer table and subtracts 1 from senders inventory

        int count = transferTable.getChildCount();
        Integer quant = 1;
        String[] data = new String[3];
        Integer rowID = 0;

        if (selectedView instanceof TableRow) {

            //Get InStock quantity
            TableRow selectedRow = (TableRow) selectedView;
            TextView selectedQuant = (TextView) selectedRow.getChildAt(1);
            rowID = selectedRow.getId();

            //Check for InStock quantity to be > 0
            if (Integer.parseInt(selectedQuant.getText().toString()) > 0) {

                //Subtract 1 from InStock row
                Integer a = Integer.parseInt(selectedQuant.getText().toString()) - 1;
                selectedQuant.setText(a.toString());

                //Check existing used inventory rows
                for (Integer i = 0; i < transferTable.getChildCount(); i++) {

                    View view1 = transferTable.getChildAt(i);

                    if (view1 instanceof TableRow) {

                        TableRow thisRow = (TableRow) view1;
                        TextView tv0 = (TextView) thisRow.getChildAt(0);
                        TextView tv1 = (TextView) thisRow.getChildAt(1);

                        //increment quantity on existing rows
                        if (tv0.getText().toString().equals(title)) {

                            quant = Integer.parseInt(tv1.getText().toString()) + 1;
                            tv1.setText(quant.toString());
                        }
                    }
                }

                //Add new row
                if (quant.equals(1)) {

                    data [0] = title;
                    data [1] = quant.toString();
                    data [2] = price;

                    TableRow tr = newTableRow(data,count);
                    tr.setId(rowID);

                    tr.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            TableRow selectedRow = (TableRow) view;
                            TextView tv0 = (TextView) selectedRow.getChildAt(0);
                            String title = tv0.getText().toString();
                            subtractFromTransfer(title, view);
                        }
                    });

                    transferTable.addView(tr);
                }
            }
        }
    }

    private void subtractFromTransfer(String title, View selectedView){//Subtracts a part from the transfer table and adds 1 to senders inventory

        Integer quant;

        if (selectedView instanceof TableRow) {

            //Get transferred quantity
            TableRow selectedRow = (TableRow) selectedView;
            TextView selectedQuant = (TextView) selectedRow.getChildAt(1);


            //Check for quantity to be > 0
            if (Integer.parseInt(selectedQuant.getText().toString()) > 0) {

                TableRow tr = new TableRow(this);

                //Subtract 1 from Table row
                Integer a = Integer.parseInt(selectedQuant.getText().toString()) - 1;
                selectedQuant.setText(a.toString());

                if (a.equals(0))transferTable.removeView(selectedView);

                for (Integer i = 0; i <= inventoryTable.getChildCount(); i++) {

                    View view1 = inventoryTable.getChildAt(i);

                    if (view1 instanceof TableRow) {

                        TableRow thisRow = (TableRow) view1;
                        TextView tv0 = (TextView) thisRow.getChildAt(0);

                        //Check for and increment quantity on row
                        if (tv0.getText().toString().equals(title)) {

                            TextView tv1 = (TextView) thisRow.getChildAt(1);
                            quant = Integer.parseInt(tv1.getText().toString()) + 1;
                            tv1.setText(quant.toString());
                        }
                    }
                }
            }
        }
    }

    private void loadTaskList(){

        Intent i = new Intent(this, TaskList.class);
        i.putExtra("employeeID",employeeID.toString());
        startActivity(i);
    }

    private void loadOrderList(){

        Intent i = new Intent(this, OrderList.class);
        startActivity(i);
    }

    private void submit() {

        String title;
        int quantity;
        int invID = 0;
        boolean entered;
        dataMethod = "submit";
        subURL = "insertEditDelete.php/";

        //Get transferred items
        for (int i = 1; i < transferTable.getChildCount(); i++) {

            entered = false;
            View view = transferTable.getChildAt(i);

            if (view instanceof TableRow) {

                TableRow thisRow = (TableRow) view;

                TextView tv0 = (TextView) thisRow.getChildAt(0);
                TextView tv1 = (TextView) thisRow.getChildAt(1);

                title = tv0.getText().toString();
                quantity = Integer.parseInt(tv1.getText().toString());

                //Update quantities on parts the receiver has already
                for (InStock in : receiverInv) {

                    if (in.getInvID().equals(thisRow.getId())) {

                        invID = in.getInvID();
                        quantity = quantity + in.getQuantity();
                        entered = true;
                    }
                }

                if (entered){

                    queryArray.add("UPDATE instock SET InStockQuantity = " + quantity +
                            " WHERE InStockInventoryID = " + invID +
                            " AND InStockEmployeeID = " + receiverID + ";");

                } else {//Add new parts to the receiver

                    for (Inventory in : inventory) {
                        if (in.getInvTitle().equals(title)) invID = in.getInvID();
                    }

                    //AddInStock(quantity, invID, receiverID);
                    queryArray.add ("INSERT INTO instock (InStockInventoryID, InStockQuantity, InStockEmployeeID) " +
                            "VALUES (" + invID + ", " + quantity + ", " + receiverID + ");");
                }
            }
        }

        //Get sender items
        for (int i = 1; i < inventoryTable.getChildCount(); i++){

            View view1 =  inventoryTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                TableRow thisRow = (TableRow) view1;

                TextView tv0 = (TextView) thisRow.getChildAt(0);
                TextView tv1 = (TextView) thisRow.getChildAt(1);

                title = tv0.getText().toString();
                quantity = Integer.parseInt(tv1.getText().toString());

                for (InStock in: sender) {

                    if (in.getInvTitle().equals(title) && in.getQuantity() != quantity) {

                        queryArray.add("UPDATE instock SET InStockQuantity = " + quantity +
                                " WHERE InStockInventoryID = " + thisRow.getId() +
                                " AND InStockEmployeeID = " + employeeID + ";");
                    }
                }
            }
        }
        new databaseInterface().execute();
        loadTaskList();
    }

    private TableRow newTableRow(String[] data, int count){

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (count % 2 != 0) tr.setBackgroundColor(Color.LTGRAY);

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

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        int success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(InventoryList.this);
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

            if (dataMethod.equals("getInventory")){

                if (success == 1) inventory = arrayListBuilder.BuildInventory(data);
                getInStock(employeeID,"getInStock");

            }else if (dataMethod.equals("getInStock")) {

                if (success == 1) sender = arrayListBuilder.BuildInStock(data);
                getEmployees();

            }else if (dataMethod.equals("getEmployees")) {

                if (success == 1) employees = arrayListBuilder.BuildEmployee(data);
                loadElements();

            }else if (dataMethod.equals("receiver")) {

                if (success == 1) receiverInv = arrayListBuilder.BuildInStock(data);
                submit();
            }
        }
    }
}
