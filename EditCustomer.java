package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.thegreatmugwump.taskmanager.Objects.Customer;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class EditCustomer extends AppCompatActivity {

    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private Integer customerID;
    private EditText customer;
    private EditText address;
    private EditText city;
    private EditText zip;
    private EditText state;
    private EditText contact;
    private EditText phone;
    private String query = "";
    private String subURL;
    private String dataMethod;
    MenuItem select;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        assert getSupportActionBar() != null;

        customerID = getIntent().getExtras().getInt("customerID");

        findElements();
        getCustomers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        select = menu.add("Submit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit")){

            dataMethod = "Submit";
            subURL = "getCustomers.php/";
            query = "UPDATE customer " +
                    "SET CustomerName = '" + customer.getText().toString() +
                    "', CustomerAddress = '" + address.getText().toString() +
                    "', CustomerCity = '" + city.getText().toString() +
                    "', CustomerZip = '" + zip.getText().toString() +
                    "', CustomerState = '" + state.getText().toString() +
                    "', CustomerContactName = '" + contact.getText().toString() +
                    "', CustomerPhone = '" + phone.getText().toString() +
                    "' WHERE CustomerID = '" + customerID + "';";
            new databaseInterface().execute();
        }
        return true;
    }

    private void loadAddTaskScreen(){//Load the AddTaskScreen screen

        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    private void findElements(){//Assign layout items to variables

        customer = findViewById(R.id.EC_customer);
        address = findViewById(R.id.EC_address);
        city = findViewById(R.id.EC_city);
        zip = findViewById(R.id.EC_zip);
        state = findViewById(R.id.EC_state);
        contact = findViewById(R.id.EC_contact);
        phone = findViewById(R.id.EC_phone);
    }

    private void loadElements(){//populate the layout items with customer details

        for (Customer c: customers) {

            customerID = c.getCustomerID();
            customer.setText(c.getName());
            address.setText(c.getAddress());
            city.setText(c.getCity());
            zip.setText(c.getZip());
            state.setText(c.getState());
            contact.setText(c.getContact());
            phone.setText(c.getPhone());
        }
    }

    private void getCustomers(){

        dataMethod = "getCustomers";
        subURL = "getCustomers.php/";
        query = "SELECT * FROM customer WHERE CustomerID = " + customerID + ";";
        new databaseInterface().execute();
    }

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        int success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(EditCustomer.this);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e ("dataMethod-----", dataMethod);
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(subURL, query);

            try {

                success = jsonObject.getInt("success");

                if (success == 1) {

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


             if (dataMethod.equals("getCustomers")){

                customers = arrayListBuilder.BuildCustomer(data);
                loadElements();

             }else if (dataMethod.equals("Submit")){

                 loadAddTaskScreen();
             }
        }
    }
}
