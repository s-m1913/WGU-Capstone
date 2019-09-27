package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.example.thegreatmugwump.taskmanager.Objects.InStock;
import com.example.thegreatmugwump.taskmanager.Objects.Inventory;
import com.example.thegreatmugwump.taskmanager.Objects.TaskLineItem;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskParts extends AppCompatActivity {

    private TableLayout inventoryTable;
    private TableLayout inventoryUsedTable;
    private TextView totalView;
    private EditText searchText;
    private ArrayList <Inventory> inventory = new ArrayList<>();
    private ArrayList <InStock> inStock = new ArrayList<>();
    private ArrayList <TaskLineItem> taskLineItems = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private DecimalFormat df = new DecimalFormat("#.00");
    private int taskID;
    private String query = "";
    private String subURL;
    private String dataMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_parts);

        assert getSupportActionBar() != null;

        findElements();
        getLineItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.add("Submit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit")) loadTaskDetails();

        return true;
    }

    private void getLineItem(){

        taskID = getIntent().getExtras().getInt("taskID");

        dataMethod = "getLineItem";
        subURL = "getTaskLineItem.php/";
        query = "SELECT * FROM tasklineitem WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void getInStock(){

        int employeeID = Integer.parseInt(TaskList.employeeID);

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

    private void findElements(){//Assign layout items to variables

        inventoryTable = findViewById(R.id.TP_inventoryTable);
        inventoryUsedTable = findViewById(R.id.TP_usedTable);
        totalView = findViewById(R.id.TP_PartsTotal);
        searchText = findViewById(R.id.TP_searchText);
        Button searchButton = findViewById(R.id.TP_searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchParts(view);
            }
        });
    }

    private void loadElements() {//populate the layout items with inventory details

        Integer quant;
        Double price;
        int count = 1;
        String[] data = new String[3];
        String[] data1 = new String[4];

        //Load inventory list
        for (InStock in : inStock) {

            quant = in.getQuantity();
            price = in.getPrice();

            data[0] = in.getInvTitle();
            data[1] = quant.toString();
            data[2] = "$" + df.format(price);

            TableRow tr = newTableRow(data,count);
            tr.setId(in.getInStockID());
            tr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    TableRow selectedRow = (TableRow) view;
                    TextView tv0 = (TextView) selectedRow.getChildAt(0);
                    TextView tv2 = (TextView) selectedRow.getChildAt(2);
                    String title = tv0.getText().toString();
                    String price = tv2.getText().toString();
                    addToTask(title, price, view);
                }
            });

            inventoryTable.addView(tr);
            count++;
        }

        //Load previously used inventory
        count = 1;
        for (TaskLineItem l : taskLineItems){

            for (Inventory in : inventory){

                if (in.getInvID().equals(l.getInventoryID())){

                    Double linePrice = in.getPrice();
                    Double sub = linePrice * l.getTaskLineItemQuantity();

                    data1 [0] = in.getInvTitle();
                    data1 [1] = l.getTaskLineItemQuantity().toString();
                    data1 [2] = "$" + df.format(linePrice);
                    data1 [3] = "$" + df.format(sub);

                    TableRow tr = newTableRow(data1, count);

                    tr.setId(l.getTaskLineItemID());
                    tr.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            TableRow selectedRow = (TableRow) view;

                            TextView tv0 = (TextView) selectedRow.getChildAt(0);
                            TextView tv1 = (TextView) selectedRow.getChildAt(2);

                            String title = tv0.getText().toString();
                            String price = tv1.getText().toString();

                            subtractFromTask(title, price, view);
                        }
                    });

                    count ++;
                    inventoryUsedTable.addView(tr);
                }
            }
        }
        highlightRows();
        updateTotal();
    }

    private void addToTask(String title, String price, View selectedView){

        int count = inventoryUsedTable.getChildCount();
        int instockID = 0;
        Integer quant = 1;
        String[] data = new String[4];

        if (selectedView instanceof TableRow) {

            //Get InStock quantity
            TableRow selectedRow = (TableRow) selectedView;
            TextView selectedQuant = (TextView) selectedRow.getChildAt(1);
            TextView selectedName = (TextView) selectedRow.getChildAt(0);

            //Check for InStock quantity to be > 0
            if (Integer.parseInt(selectedQuant.getText().toString()) > 0) {

                //update total
                updateTotal();

                //Subtract 1 from InStock row
                Integer a = Integer.parseInt(selectedQuant.getText().toString()) - 1;
                selectedQuant.setText(a.toString());

                for (InStock in : inStock){

                    if (selectedName.getText().toString().equals(in.getInvTitle())) instockID = in.getInStockID();
                }

                dataMethod = "";
                subURL = "insertEditDelete.php/";
                query = "UPDATE instock SET InStockQuantity = " + a + " WHERE InStockID = " + instockID + ";";
                new databaseInterface().execute();

                //Check existing used inventory rows
                for (Integer i = 0; i < inventoryUsedTable.getChildCount(); i++) {

                    View view1 = inventoryUsedTable.getChildAt(i);

                    if (view1 instanceof TableRow) {

                        TableRow thisRow = (TableRow) view1;
                        TextView tv0 = (TextView) thisRow.getChildAt(0);
                        TextView tv1 = (TextView) thisRow.getChildAt(1);
                        TextView tv3 = (TextView) thisRow.getChildAt(3);

                        //increment quantity and subtotal on existing rows
                        if (tv0.getText().toString().equals(title)) {

                            Double subtotal = Double.parseDouble(price.substring(1)) * (Integer.parseInt(tv1.getText().toString()) + 1);
                            quant = Integer.parseInt(tv1.getText().toString()) + 1;
                            tv1.setText(quant.toString());
                            tv3.setText("$" + df.format(subtotal));

                            dataMethod = "UPDATE";
                            subURL = "insertEditDelete.php/";
                            query = "UPDATE tasklineitem SET TaskLineItemQuant = " + quant + " WHERE TaskLineItemID = " + thisRow.getId() + ";";
                            new databaseInterface().execute();
                        }
                    }
                }

                //Add new row
                if (quant.equals(1)) {

                    data [0] = title;
                    data [1] = quant.toString();
                    data [2] = price;
                    data [3] = price;

                    TableRow tr = newTableRow(data,count);

                    tr.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            TableRow selectedRow = (TableRow) view;
                            TextView tv0 = (TextView) selectedRow.getChildAt(0);
                            TextView tv2 = (TextView) selectedRow.getChildAt(2);
                            String title = tv0.getText().toString();
                            String price = tv2.getText().toString();
                            subtractFromTask(title, price, view);
                        }
                    });
                    inventoryUsedTable.addView(tr);

                    for (InStock in : inStock){

                        if (selectedName.getText().toString().equals(in.getInvTitle())) instockID = in.getInvID();
                    }

                    //inventoryUsedTable.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    dataMethod = "UPDATE";
                    subURL = "insertEditDelete.php/";
                    query = "INSERT INTO tasklineitem (TaskLineItemQuant, InventoryID, TaskID) VALUES (" + quant + ", " + instockID + ", " + taskID + ");";
                    new databaseInterface().execute();
                }

                highlightRows();
                updateTotal();
            }
        }
    }

    private void subtractFromTask(String title, String price, View selectedView){

        Integer quant;
        int instockID = 0;

        if (selectedView instanceof TableRow) {

            //Get UsedTable quantity
            TableRow selectedRow = (TableRow) selectedView;
            TextView selectedQuant = (TextView) selectedRow.getChildAt(1);
            TextView selectedSub = (TextView) selectedRow.getChildAt(3);

            //Check for UsedTable quantity to be > 0
            if (Integer.parseInt(selectedQuant.getText().toString()) > 0) {

                //Subtract 1 from UsedTable row
                Integer a = Integer.parseInt(selectedQuant.getText().toString()) - 1;
                selectedQuant.setText(a.toString());
                Double subTotal = Double.parseDouble(price.substring(1)) * a;
                selectedSub.setText("$" + df.format(subTotal));

                dataMethod = "UPDATE";
                subURL = "insertEditDelete.php/";
                query = "UPDATE tasklineitem SET TaskLineItemQuant = " + a + " WHERE TaskLineItemID = " + selectedRow.getId() + ";";
                new databaseInterface().execute();

                //Add 1 to InStock row
                for (int i = 0; i < inventoryTable.getChildCount(); i++) {

                    View view1 = inventoryTable.getChildAt(i);

                    if (view1 instanceof TableRow) {

                        TableRow thisRow = (TableRow) view1;
                        TextView tv0 = (TextView) thisRow.getChildAt(0);
                        TextView tv1 = (TextView) thisRow.getChildAt(1);

                        //Check for and increment quantity on row
                        if (tv0.getText().toString().equals(title)) {

                            quant = Integer.parseInt(tv1.getText().toString()) + 1;
                            tv1.setText(quant.toString());

                            for (InStock in : inStock){

                                if (tv0.getText().toString().equals(in.getInvTitle())) instockID = in.getInStockID();
                            }

                            dataMethod = "UPDATE";
                            subURL = "insertEditDelete.php/";
                            query = "UPDATE instock SET InStockQuantity = " + a + " WHERE InStockID = '" + instockID + "';";
                            new databaseInterface().execute();
                        }
                    }
                }
            }
            //Delete row if quant = 0
            if (Integer.parseInt(selectedQuant.getText().toString()) == 0) {

                inventoryUsedTable.removeView(selectedRow);
                dataMethod = "UPDATE";
                subURL = "insertEditDelete.php/";
                query = "DELETE FROM tasklineitem WHERE TaskLineItemID = " + selectedRow.getId() + ";";
            }

            highlightRows();
            updateTotal();
        }
    }

    private void highlightRows(){

        int count = 0;
        for (int i = 0; i < inventoryUsedTable.getChildCount(); i++) {

            View view1 = inventoryUsedTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }

        count = 0;
        for (int i = 0; i < inventoryTable.getChildCount(); i++) {

            View view1 = inventoryTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private void updateTotal(){

        Double total = 0.00;

        for (int i = 1; i < inventoryUsedTable.getChildCount(); i++) {

            View view1 = inventoryUsedTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                TableRow thisRow = (TableRow) view1;
                TextView tv3 = (TextView) thisRow.getChildAt(3);
                total = total + Double.parseDouble(tv3.getText().toString().substring(1));
            }
        }

        totalView.setText("$" + df.format(total));
    }

    private void loadTaskDetails(){

        Intent i = new Intent(this, TaskDetails.class);
        i.putExtra("taskID",taskID);
        startActivity(i);
    }

    public void searchParts(View v) {

        Integer quant;
        Double price;
        int count = 1;
        String[] data = new String[3];
        List<InStock> filteredParts = new ArrayList<>();

        String lowerCaseFilter = searchText.getText().toString().toLowerCase();

        for (InStock i : inStock){

            if (i.getInvTitle().toLowerCase().contains(lowerCaseFilter))filteredParts.add(i);

        }

        inventoryTable.removeAllViews();

        for (InStock in : filteredParts) {

            quant = in.getQuantity();
            price = in.getPrice();

            data[0] = in.getInvTitle();
            data[1] = quant.toString();
            data[2] = "$" + df.format(price);

            TableRow tr = newTableRow(data, count);
            tr.setId(in.getInStockID());
            tr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    TableRow selectedRow = (TableRow) view;
                    TextView tv0 = (TextView) selectedRow.getChildAt(0);
                    TextView tv2 = (TextView) selectedRow.getChildAt(2);
                    String title = tv0.getText().toString();
                    String price = tv2.getText().toString();
                    addToTask(title, price, view);
                }
            });

            inventoryTable.addView(tr);
            count++;
        }
    }

    private TableRow newTableRow(String[] data, int count){

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        if (count % 2 != 0) tr.setBackgroundColor(Color.LTGRAY);

        for (int i = 0; i < data.length; i++){

            TextView tv = newTextView(data[i]);
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
            progressBar = new ProgressBar(TaskParts.this);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(subURL, query);

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


            if (dataMethod.equals("getLineItem")) {

                if (success == 1) taskLineItems = arrayListBuilder.BuildTaskLineItem(data);
                getInStock();

            }else if (dataMethod.equals("getInStock")){

                if (success == 1) inStock = arrayListBuilder.BuildInStock(data);
                getInventory();

            }else if (dataMethod.equals("getInventory")) {

                if (success == 1) inventory = arrayListBuilder.BuildInventory(data);
                loadElements();

            }
        }
    }
}
