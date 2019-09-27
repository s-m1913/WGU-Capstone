package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatmugwump.taskmanager.Objects.Customer;
import com.example.thegreatmugwump.taskmanager.Objects.Task;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AddTask extends AppCompatActivity {

    private TextView problem;
    private TextView address;
    private TextView city;
    private TextView state;
    private TextView zip;
    private TextView contact;
    private TextView phone;
    private Spinner custSelect;
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private Integer customerID;
    private String query = "";
    private String subURL;
    private String dataMethod;
    private String employeeID = TaskList.employeeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        assert getSupportActionBar() != null;

        findElements();
        getCustomers();

        custSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        menu.add("Add Customer");
        menu.add("Edit Customer");
        menu.add("Task List");
        menu.add("Submit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Add Customer")) {

            Intent i = new Intent(this, AddCustomer.class);
            startActivity(i);

        }else if (item.getTitle().toString().equals("Edit Customer")){

            Intent i = new Intent(this, EditCustomer.class);
            i.putExtra("customerID",customerID);
            startActivity(i);

        }else if (item.getTitle().toString().equals("Task List")){

            loadTaskList();

        }else if (item.getTitle().toString().equals("Submit")){

            postTask();
        }

        for (Customer c : customers) {

            if (custSelect.getSelectedItem().toString().equals(c.getName())) customerID = c.getCustomerID();
        }

        return true;
    }

    private void findElements(){//Assign layout items to variables

        problem = findViewById(R.id.AT_ProblemDescription);
        address = findViewById(R.id.AT_address);
        city = findViewById(R.id.AT_city);
        state = findViewById(R.id.AT_state);
        zip = findViewById(R.id.AT_zip);
        contact = findViewById(R.id.AT_contact);
        phone = findViewById(R.id.AT_phone);
        custSelect = findViewById(R.id.AT_customer);

        textViews.add(problem);
    }

    private void loadElements(){//populate the dropdown menu with customers

        ArrayList<String> custList = new ArrayList<>();

        for (Customer c : customers){
            custList.add(c.getName());
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, custList);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.custSelect.setAdapter(adp);
        if (customers.size() > 0) loadDetails();
    }

    private void loadDetails(){//populate the screen with selected customer details

        for (Customer c : customers){

            if (custSelect.getSelectedItem().toString().equals(c.getName())){

                customerID = c.getCustomerID();
                address.setText(c.getAddress());
                city.setText(c.getCity());
                state.setText(c.getState());
                zip.setText(c.getZip());
                contact.setText(c.getContact());
                phone.setText(c.getPhone());
            }
        }
    }

    private boolean validate(){//Check that all fields are filled

        boolean valid = true;

        for (TextView t : textViews){

            if (t.getText().toString().trim().equals("")) {
                t.setBackgroundColor(Color.RED);
                Toast.makeText(this, "All Fields Must be Filled", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        return valid;
    }

    private void loadTaskList(){

        Intent i = new Intent(this, TaskList.class);
        i.putExtra("employeeID",employeeID);
        startActivity(i);
    }

    private void getCustomers(){

        dataMethod = "GET";
        subURL = "getCustomers.php/";
        query = "SELECT * FROM customer";

        new databaseInterface().execute();
    }

    private void getTasks(){

        dataMethod = "GET2";
        subURL = "getTasks.php/";
        query = "SELECT * FROM task WHERE EmployeeID = " + employeeID + " AND TaskStatus = 'Open'";

        new databaseInterface().execute();
    }

    private void postTask(){

        if(validate()){

            for (Customer c: customers){

                if (c.getName().equals(custSelect.getSelectedItem().toString())) customerID = c.getCustomerID();
            }
            dataMethod = "POST1";
            subURL = "insertEditDelete.php/";
            query = "INSERT INTO task (" +
                    "CustomerID," +
                    "EmployeeID," +
                    "TaskStatus," +
                    "TaskDescription," +
                    "TaskResolution) " +
                    "VALUES ("+
                    customerID +", " +
                    employeeID +", '" +
                    "Open', \"" +
                    problem.getText().toString() +"\", '');";

            new databaseInterface().execute();
        }
    }

    private void postTimeStamp(){

        int taskID = 0;
        int miles = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
        LocalDateTime ldt = LocalDateTime.now();
        String date = dtf.format(ldt);

        for (Task t : tasks){

            if (customerID.equals(t.getCustomerID()))taskID = t.getTaskID();
        }

        dataMethod = "POST2";
        subURL = "insertEditDelete.php/";
        query = "INSERT INTO tasktimestamp (" +
                "TaskID," +
                "TaskTimeStampCreated, " +
                "TaskTimeStampStartMiles, " +
                "TaskTimeStampEndMiles, " +
                "TaskTimeStampEnroute, " +
                "TaskTimeStampStart, " +
                "TaskTimeStampEnd) " +
                "VALUES (" +
                taskID + ", '" +
                date + "', " +
                miles + ", " +
                miles + ", " +
                "' ', ' ', ' ');";

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
            progressBar = new ProgressBar(AddTask.this);
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

                    data = jsonObject.getJSONArray("data");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.GONE);

            if (dataMethod.equals("GET")) {

                customers = arrayListBuilder.BuildCustomer(data);
                loadElements();

            }else if (dataMethod.equals("POST1")) {

                getTasks();

            }else if (dataMethod.equals("GET2")){

                tasks = arrayListBuilder.BuildTask(data);
                postTimeStamp();

            }else if (dataMethod.equals("POST2")) loadTaskList();
        }
    }
}
