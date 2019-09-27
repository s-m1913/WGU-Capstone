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
import com.example.thegreatmugwump.taskmanager.Objects.Customer;
import com.example.thegreatmugwump.taskmanager.Objects.TaskTimeStamp;
import com.example.thegreatmugwump.taskmanager.helper.*;
import com.example.thegreatmugwump.taskmanager.Objects.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TaskList extends AppCompatActivity {

    private TableLayout taskList;
    private ArrayList<Task> task = new ArrayList<>();
    private ArrayList<Customer> customer = new ArrayList<>();
    private ArrayList<TaskTimeStamp> taskTimeStamp = new ArrayList<>();
    public static String employeeID;
    private String dataMethod;
    private String query = "";
    private String subURL;
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        setContentView(R.layout.activity_task_list);

        findElements();
        getTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add("Add Task");
        menu.add("Add Customer");
        menu.add("Inventory List");
        menu.add("Team Management");
        menu.add("Reports");
        menu.add("Change Password");
        menu.add("Log Out");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().equals("Add Task")) { loadAddTask();

        } else if (item.getTitle().equals("Add Customer")){ loadAddCustomer();

        } else if (item.getTitle().equals("Inventory List")){ loadInventoryList();

        } else if (item.getTitle().equals("Team Management")){ loadTeamList();

        } else if (item.getTitle().equals("Log Out")){ loadLogin();

        } else if (item.getTitle().equals("Change Password")){ loadEditPassword();

        } else if (item.getTitle().equals("Reports")) loadReports();

        return true;
    }

    private void getTasks(){

        employeeID = getIntent().getExtras().getString("employeeID");
        dataMethod = "getTasks";
        subURL = "getTasks.php/";
        query = "SELECT * FROM task WHERE EmployeeID = " + employeeID + " AND TaskStatus != 'Complete'";

        new databaseInterface().execute();
    }

    private void getCustomers(){

        dataMethod = "getCustomers";
        subURL = "getCustomers.php/";
        query = "SELECT * FROM customer ";

        new databaseInterface().execute();
    }

    private void getTaskTimeStamp(){

        dataMethod = "getTaskTimeStamp";
        subURL = "getTaskTimeStamp.php/";
        query = "SELECT * FROM tasktimestamp";

        new databaseInterface().execute();
    }

    private void findElements(){

        taskList = findViewById(R.id.TaskContainer);
    }

    private void loadElements(){

        String[] data = new String[4];

        data[0] = "Customer";
        data[1] = "Created";
        data[2] = "Problem";
        data[3] = "Status";

        TableRow tr = newTableRow(data);
        taskList.addView(tr);

        for (Task t : task){

            for (Customer c : customer){
                if (t.getCustomerID() == c.getCustomerID()) data[0] = c.getName();
            }

            for (TaskTimeStamp time : taskTimeStamp){
                if (t.getTaskID().equals(time.getTaskID())) data[1] = time.getCreated();
            }

            data[2] = t.getTaskDescription();
            data[3] = t.getStatus();

            tr = newTableRow(data);
            tr.setId(t.getTaskID());
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow selectedRow = (TableRow) view;
                    loadTaskDetails(selectedRow.getId());
                }
            });
            taskList.addView(tr);
        }
        highlightRows();
    }

    private void highlightRows(){

        int count = 0;
        for (int i = 0; i < taskList.getChildCount(); i++) {

            View view1 = taskList.getChildAt(i);

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

    private void loadTaskDetails(int taskID){

        Intent i = new Intent(this, TaskDetails.class);
        i.putExtra("taskID",taskID);
        startActivity(i);
    }

    private void loadAddTask(){

        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    private void loadAddCustomer(){

        Intent i = new Intent(this, AddCustomer.class);
        startActivity(i);
    }

    private void loadInventoryList(){

        Intent i = new Intent(this, InventoryList.class);
        i.putExtra("employeeID",employeeID);
        startActivity(i);
    }

    private void loadTeamList(){

        Intent i = new Intent(this, TeamList.class);
        startActivity(i);
    }

    private void loadReports(){

        Intent i = new Intent(this, Reports.class);
        startActivity(i);
    }

    private void loadLogin(){

        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    private void loadEditPassword(){

        Intent i = new Intent(this, EditPassword.class);
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
            progressBar = new ProgressBar(TaskList.this);
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
            if (dataMethod.equals("getTasks")) {

                if (success == 1) task = arrayListBuilder.BuildTask(data);
                getCustomers();

            }else if (dataMethod.equals("getCustomers")){

                if (success == 1) customer = arrayListBuilder.BuildCustomer(data);
                getTaskTimeStamp();

            }else if (dataMethod.equals("getTaskTimeStamp")){

                if (success == 1) taskTimeStamp = arrayListBuilder.BuildTaskTimeStamp(data);
                loadElements();
            }
        }
    }
}
