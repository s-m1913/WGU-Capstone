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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatmugwump.taskmanager.Objects.Employee;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditEmployee extends AppCompatActivity {

    private TextView nameView;
    private TextView phoneView;
    private TextView addressView;
    private TextView cityView;
    private TextView stateView;
    private TextView zipView;
    private Spinner selectView;
    private Spinner statusView;
    private String query = "";
    private String subURL;
    private String dataMethod;
    private String myEmployeeID = TaskList.employeeID;
    private ArrayList <Employee> employees = new ArrayList<>();
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private int employeeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        assert getSupportActionBar() != null;

        findElements();
        getEmployees();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.add("Task List");
        menu.add("Team List");
        menu.add("Submit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Task List")) {

            loadTaskList();

        } else if (item.getTitle().toString().equals("Team List")) {

            loadTeamList();

        } else if (item.getTitle().toString().equals("Submit")) {

            if (selectView.getSelectedItem().toString().equals("New Employee")){
                newEmployee();
            }else  updateEmployee();
        }
        return true;
    }

    private void findElements(){//Assign layout items to variables

        nameView = findViewById(R.id.EE_name);
        phoneView = findViewById(R.id.EE_phone);
        addressView = findViewById(R.id.EE_address);
        cityView = findViewById(R.id.EE_city);
        stateView = findViewById(R.id.EE_state);
        zipView = findViewById(R.id.EE_zip);
        selectView = findViewById(R.id.EE_employeeSelect);
        statusView = findViewById(R.id.EE_status);
    }

    private void loadElements(){//populate the dropdown menus with employee names and status options

        ArrayList<String> empList = new ArrayList<>();
        empList.add("New Employee");

        for (Employee e : employees) {

            empList.add(e.getName());
        }

        ArrayAdapter<String> adp0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, empList);
        adp0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.selectView.setAdapter(adp0);

        selectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> statusList = new ArrayList<>();
        statusList.add("Employee");
        statusList.add("Manager");
        statusList.add("Terminated");

        ArrayAdapter<String> adp1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        adp1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.statusView.setAdapter(adp1);
    }

    private void loadDetails(){//populate the layout items with selected employee details

        if (selectView.getSelectedItem().toString().equals("New Employee")) {

            nameView.setText("");
            phoneView.setText("");
            addressView.setText("");
            cityView.setText("");
            stateView.setText("");
            zipView.setText("");

        }else {

            for (Employee e : employees) {

                if (e.getName().equals(selectView.getSelectedItem().toString())) {

                    employeeID = e.getEmployeeID();
                    nameView.setText(e.getName());
                    phoneView.setText(e.getPhone());
                    addressView.setText(e.getAddress());
                    cityView.setText(e.getCity());
                    stateView.setText(e.getState());
                    zipView.setText(e.getZip());
                    for (int i = 0; i < 3; i++){
                        if (e.getStatus().equals(statusView.getItemAtPosition(i))) statusView.setSelection(i);
                    }
                }
            }
        }
        textViews.add(nameView);
        textViews.add(phoneView);
        textViews.add(addressView);
        textViews.add(cityView);
        textViews.add(stateView);
        textViews.add(zipView);
    }

    private void getEmployees(){

        dataMethod = "getEmployees";
        subURL = "getEmployees.php";
        query = "SELECT * FROM employee";

        new databaseInterface().execute();
    }

    private void updateEmployee(){

        if(validate()) {
            dataMethod = "updateEmployee";
            subURL = "insertEditDelete.php/";
            query = "UPDATE employee" +
                    " SET EmployeeName = '" + nameView.getText().toString() +
                    "', EmployeeStatus = '" + statusView.getSelectedItem().toString() +
                    "', EmployeePhone = '" + phoneView.getText().toString() +
                    "', EmployeeAddress = '" + addressView.getText().toString() +
                    "', EmployeeCity = '" + cityView.getText().toString() +
                    "', EmployeeState = '" + stateView.getText().toString() +
                    "', EmployeeZip = '" + zipView.getText().toString() +
                    "' WHERE EmployeeID = " + employeeID + ";";
            new databaseInterface().execute();
        }
    }

    private void newEmployee(){

        if(validate()) {
            dataMethod = "newEmployee";
            subURL = "insertEditDelete.php/";
            query = "INSERT INTO employee" +
                    " (EmployeeName, EmployeePassword, EmployeeStatus, EmployeePhone, EmployeeAddress, EmployeeCity, EmployeeState, EmployeeZip) " +
                    "VALUES ('" + nameView.getText().toString() + "', '1234" +
                    "', '" + statusView.getSelectedItem().toString() +
                    "', '" + phoneView.getText().toString() +
                    "', '" + addressView.getText().toString() +
                    "', '" + cityView.getText().toString() +
                    "', '" + stateView.getText().toString() +
                    "', '" + zipView.getText().toString() + "');";
            new databaseInterface().execute();
        }
    }

    private boolean validate(){

        boolean valid = true;

        for (TextView t : textViews) {

            if (t.getText().toString().trim().equals("")) {

                t.setBackgroundColor(Color.RED);
                Toast.makeText(this, "All Fields Must be Filled", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        if (zipView.getText().toString().length()!= 5){
            Integer l = zipView.getText().toString().length();
            Log.e("ZIP<><><><>", l.toString());
            zipView.setBackgroundColor(Color.RED);
            Toast.makeText(this, "Zip Codes Must be 5 Digits", Toast.LENGTH_LONG).show();
            valid = false;

        }else if (stateView.getText().toString().length() != 2){

            stateView.setBackgroundColor(Color.RED);
            Toast.makeText(this, "State Codes Must be 2 Letters", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    private void loadTaskList(){

        Intent i = new Intent(this, TaskList.class);
        i.putExtra("employeeID",myEmployeeID);
        startActivity(i);
    }

    private void loadTeamList(){

        Intent i = new Intent(this, TeamList.class);
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
            progressBar = new ProgressBar(EditEmployee.this);
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

            if (dataMethod.equals("getEmployees")){

                if (success == 1) {
                    employees = arrayListBuilder.BuildEmployee(data);
                    loadElements();
                }
            }
        }
    }
}
