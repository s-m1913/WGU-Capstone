package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
//Adds a new customer to the database
public class AddCustomer extends AppCompatActivity {

    private TextView customer;
    private TextView address;
    private TextView city;
    private TextView zip;
    private TextView state;
    private TextView contact;
    private TextView phone;
    private String query = "";
    private String subURL;
    private ArrayList<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;

        setContentView(R.layout.activity_add_customer);

        customer = findViewById(R.id.AC_customer);
        address = findViewById(R.id.AC_address);
        city = findViewById(R.id.AC_city);
        zip = findViewById(R.id.AC_zip);
        state = findViewById(R.id.AC_state);
        contact = findViewById(R.id.AC_contact);
        phone = findViewById(R.id.AC_phone);

        textViews.add(customer);
        textViews.add(address);
        textViews.add(city);
        textViews.add(zip);
        textViews.add(state);
        textViews.add(contact);
        textViews.add(phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        menu.add("Add Task");
        menu.add("Submit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit")) {

            if (validate()) {//validate the databaseInterface and send to the database

                subURL = "insertEditDelete.php/";
                query = "INSERT INTO customer (" +
                        "CustomerName, " +
                        "CustomerAddress, " +
                        "CustomerCity, " +
                        "CustomerState, " +
                        "CustomerZip, " +
                        "CustomerContactName, " +
                        "CustomerPhone) " +
                        "VALUES ('"+
                        customer.getText().toString() +"', '" +
                        address.getText().toString() +"', '" +
                        city.getText().toString() +"', '" +
                        state.getText().toString() +"', '" +
                        zip.getText().toString() +"', '" +
                        contact.getText().toString() +"', '" +
                        phone.getText().toString() + "');";

                new databaseInterface().execute();
            }
        }

        if (item.getTitle().toString().equals("Add Task")){

            loadAddTask();
        }
        return true;
    }

    private boolean validate(){//validate the entries before submitting to the database

        boolean valid = true;

        for (TextView t : textViews){

            if (t.getText().toString().trim().equals("")) {
                t.setBackgroundColor(Color.RED);
                Toast.makeText(this, "All Fields Must be Filled", Toast.LENGTH_LONG).show();
                valid = false;
            }

            if (zip.getText().toString().length()!= 5){
                zip.setBackgroundColor(Color.RED);
                Toast.makeText(this, "Zip Codes Must be 5 Digits", Toast.LENGTH_LONG).show();
                valid = false;
            }

            if (state.getText().toString().length() != 2){
                state.setBackgroundColor(Color.RED);
                Toast.makeText(this, "State Codes Must be 2 Letters", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        return valid;
    }

    private void loadAddTask(){//Load the TaskList screen

        Intent i = new Intent(this, AddTask.class);
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
            progressBar = new ProgressBar(AddCustomer.this);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(subURL, query);

            try {

                success = jsonObject.getInt("success");

                if (success == 1) {

                    data = jsonObject.getJSONArray("databaseInterface");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.GONE);
            loadAddTask();
        }
    }
}
