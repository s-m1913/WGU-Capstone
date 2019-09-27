package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.Employee;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TeamList extends AppCompatActivity {

    private TableLayout teamTable;
    private String query = "";
    private String subURL;
    private String dataMethod;
    private String myStatus = "";
    private String employeeID = TaskList.employeeID;
    private ArrayList <Employee> employees = new ArrayList<>();
    private ArrayList<String> menuArray = new ArrayList<>();
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        assert getSupportActionBar() != null;

        menuArray.add("Task List");

        teamTable = findViewById(R.id.TL_teamTable);
        getEmployees();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.clear();
        for(String s : menuArray){
            menu.add(s);
        }

        myMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Task List")) {

            Intent i = new Intent(this, TaskList.class);
            i.putExtra("employeeID",employeeID);
            startActivity(i);

        }else if (item.getTitle().toString().equals("Add/Edit Employee")){

            Intent i = new Intent(this, EditEmployee.class);
            startActivity(i);
        }
        return true;
    }

    private void updateOptionsMenu(){

        menuArray.clear();

        for (Employee e : employees){

            if (e.getEmployeeID().equals(Integer.parseInt(employeeID))){

                myStatus = e.getStatus();
            }
        }

        //employee Menu
        if (myStatus.equals("Employee")){
            menuArray.add("Task List");
        }

        //managers Menu
        if (myStatus.equals("Manager")){
            menuArray.add("Add/Edit Employee");
            menuArray.add("Task List");
        }
        onCreateOptionsMenu(myMenu);
        loadElements();
    }

    private void loadElements(){//populate the layout items with team details

        if (myStatus.equals("Manager")){
            String[] data = new String[4];
            //Add Header
            data[0] = "Name";
            data[1] = "Phone";
            data[2] = "Address";
            data[3] = "Status";

            TableRow tr = newTableRow(data);
            teamTable.addView(tr);

            //Add Employees
            for (Employee e : employees) {

                data[0] = e.getName();
                data[1] = e.getPhone();
                data[2] = e.getAddress() + ", " + e.getCity() + ", " + e.getState() + ", " + e.getZip();
                data[3] = e.getStatus();

                tr = newTableRow(data);
                tr.setId(e.getEmployeeID());
                teamTable.addView(tr);
            }

        }else{

            String[] data = new String[3];
            //Add Header
            data[0] = "Name";
            data[1] = "Phone";
            data[2] = "Address";

            TableRow tr = newTableRow(data);
            teamTable.addView(tr);

            //Add Employees
            for (Employee e : employees) {

                data[0] = e.getName();
                data[1] = e.getPhone();
                data[2] = e.getAddress() + ", " + e.getCity() + ", " + e.getState() + ", " + e.getZip();

                tr = newTableRow(data);
                teamTable.addView(tr);
            }
        }
        highlightRows();
    }

    private void getEmployees(){

        dataMethod = "getEmployees";
        subURL = "getEmployees.php";
        query = "SELECT * FROM employee";

        new databaseInterface().execute();
    }

    private void highlightRows(){

        int count = 0;
        for (int i = 0; i < teamTable.getChildCount(); i++) {

            View view1 = teamTable.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private TableRow newTableRow(String[] data){

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

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
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
            progressBar = new ProgressBar(TeamList.this);
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

            if (dataMethod.equals("getEmployees")) {

                if (success == 1) employees = arrayListBuilder.BuildEmployee(data);
                updateOptionsMenu();
            }
        }
    }
}
