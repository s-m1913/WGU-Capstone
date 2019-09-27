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
import com.example.thegreatmugwump.taskmanager.Objects.Employee;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditPassword extends AppCompatActivity {

    private String query = "";
    private String subURL;
    private String dataMethod;
    private String oldPassword;
    private String employeeID;
    private TextView oldPassView;
    private TextView newPass1View;
    private TextView newPass2View;
    private TextView message1View;
    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        assert getSupportActionBar() != null;
        employeeID = getIntent().getExtras().getString("employeeID");
        findElements();
        getEmployees();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.add("Submit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Submit")) {
            for (Employee e : employees){
                if (e.getEmployeeID().toString().equals(employeeID))oldPassword = e.getPassword();
            }
            if (validate()) updatePassword();
        }
        return true;
    }

    private void findElements(){//Assign layout items to variables

        oldPassView = findViewById(R.id.EP_old);
        newPass1View = findViewById(R.id.EP_new1);
        newPass2View = findViewById(R.id.EP_new2);
        message1View = findViewById(R.id.EP_message1);
    }

    private void getEmployees(){

        dataMethod = "getEmployees";
        subURL = "getEmployees.php";
        query = "SELECT * FROM employee";

        new databaseInterface().execute();
    }

    private boolean validate(){//Check that password meets requirements

        boolean valid = true;
        boolean atleastOneUpper = false;
        boolean atleastOneLower = false;
        boolean atleastOneDigit = false;
        StringBuilder message = new StringBuilder();

        oldPassView.setBackgroundColor(Color.WHITE);
        newPass1View.setBackgroundColor(Color.WHITE);
        newPass2View.setBackgroundColor(Color.WHITE);
        message1View.setText("");

        if (!oldPassword.equals(oldPassView.getText().toString())){
            oldPassView.setBackgroundColor(Color.RED);
            message.append("Current Password is Incorrect\n");
            valid = false;
        }
        if (!newPass1View.getText().toString().equals(newPass2View.getText().toString())){
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("New Passwords Do Not Match\n");
            valid = false;
        }
        if (newPass1View.getText().toString().equals(oldPassView.getText().toString())){
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("New Password Must Not Match Current Password\n");
            valid = false;
        }
        //Check for blank entries
        if (oldPassView.getText().toString().trim().equals("")
                ||newPass1View.getText().toString().trim().equals("")
                ||newPass2View.getText().toString().trim().equals("")){
            oldPassView.setBackgroundColor(Color.RED);
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("All Fields Must Have Entries\n");
            valid = false;
        }
        // If less then 8 characters, not valid
        if (newPass1View.getText().toString().length() < 8) {
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("Password Must Contain 8 Characters\n");
            valid = false;
        }
        // Check for Uppercase, Lowercase and numeric Characters
        for (int i = 0; i < newPass1View.getText().toString().length(); i++) {
            if (Character.isUpperCase(newPass1View.getText().toString().charAt(i))) {
                atleastOneUpper = true;
            }
            else if (Character.isLowerCase(newPass1View.getText().toString().charAt(i))) {
                atleastOneLower = true;
            }
            else if (Character.isDigit(newPass1View.getText().toString().charAt(i))) {
                atleastOneDigit = true;
            }
        }
        if (!atleastOneDigit){
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("Password Must Contain a Number\n");
        }
        if (!atleastOneLower){
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("Password Must Contain a Lowercase Letter\n");
        }
        if (!atleastOneUpper){
            newPass1View.setBackgroundColor(Color.RED);
            newPass2View.setBackgroundColor(Color.RED);
            message.append("Password Must Contain an Uppercase Letter\n");
        }
        message1View.setText(message);
        valid = valid && atleastOneUpper && atleastOneLower && atleastOneDigit;
        return valid;
    }

    private void updatePassword(){

        dataMethod = "updatePassword";
        subURL = "insertEditDelete.php/";
        query = "UPDATE employee" +
                " SET EmployeePassword = '" + newPass1View.getText().toString() +
                "' WHERE EmployeeID = " + employeeID + ";";
        new databaseInterface().execute();
    }

    private void loadTaskList(){

        Intent i = new Intent(this, TaskList.class);
        i.putExtra("employeeID",employeeID);
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
            progressBar = new ProgressBar(EditPassword.this);
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

                } else if (success == 0){

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.GONE);

            if(dataMethod.equals("getEmployees")) {

                if (success == 1)  employees = arrayListBuilder.BuildEmployee(data);

            }else if (dataMethod.equals("updatePassword")){

                loadTaskList();
            }
        }
    }
}
