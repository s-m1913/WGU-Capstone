package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.Customer;
import com.example.thegreatmugwump.taskmanager.Objects.Inventory;
import com.example.thegreatmugwump.taskmanager.Objects.Task;
import com.example.thegreatmugwump.taskmanager.Objects.TaskLineItem;
import com.example.thegreatmugwump.taskmanager.Objects.TaskTimeStamp;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class TaskDetails extends AppCompatActivity {

    private TextView customer;
    private TextView address;
    private TextView city;
    private TextView state;
    private TextView zip;
    private TextView contact;
    private TextView phone;
    private TextView total;
    private TextView problem;
    private TextView created;
    private EditText startOdometer;
    private EditText endOdometer;
    private EditText taskNotes;
    private ArrayList<TaskLineItem> taskLineItems = new ArrayList<>();
    private ArrayList<Inventory> inventory = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Task> dispatched = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<TaskTimeStamp> taskTimeStamps = new ArrayList<>();
    private ArrayList<String> menuItems = new ArrayList<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private DecimalFormat df = new DecimalFormat("#.00");
    private Integer taskID;
    private int timeStampID;
    private int customerID;
    private Integer startMiles = 0;
    private Integer endMiles = 0;
    private String employeeID = TaskList.employeeID;
    private String taskStatus;
    private String description;
    private String customerName;
    private String Address;
    private String City;
    private String State;
    private String Zip;
    private String Contact;
    private String Phone;
    private String Created;
    private String dispatchTime = "";
    private String startTime = "";
    private String endTime = "";
    private String query = "";
    private String subURL;
    private String dataMethod;
    private boolean allowDispatch = true;
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        assert getSupportActionBar() != null;

        menuItems.add ("Enroute");
        menuItems.add ("Order Parts");
        menuItems.add ("Cancel Task");
        menuItems.add ("Task List");

        findElements();
        getTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.clear();
        for(String s : menuItems){
            menu.add(s);
        }

        myMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LocalDateTime date = LocalDateTime.now();

        if (item.getTitle().toString().equals("Enroute") && !startOdometer.getText().toString().equals("")){

            dispatchTime = dtf.format(date);
            taskStatus = "Enroute";
            updateTimeStamp();
            updateOptionMenu();

        } else if (item.getTitle().toString().equals("Arrived") && !endOdometer.getText().toString().equals("")){

            startTime = dtf.format(date);
            taskStatus = "Arrived";
            updateOptionMenu();
            updateTimeStamp();

        } else if (item.getTitle().toString().equals("Complete")){

            endTime = dtf.format(date);
            taskStatus = "Complete";
            updateTimeStamp();

        } else if (item.getTitle().toString().equals("Assign Parts to Task")){

            Intent i = new Intent(this, TaskParts.class);
            i.putExtra("taskID", taskID);
            startActivity(i);

        } else if (item.getTitle().toString().equals("Order Parts")){

            Intent i = new Intent(this, TaskOrder.class);
            i.putExtra("taskID", taskID);
            startActivity(i);

        } else if (item.getTitle().toString().equals("Cancel Task")){

            dataMethod = "Cancel Task";
            subURL = "insertEditDelete.php/";
            query = "DELETE FROM tasktimestamp WHERE TaskTimeStampID = " + timeStampID + ";";

            new databaseInterface().execute();

        } else if (item.getTitle().toString().equals("Task List")){

            loadTaskList();

        } else if (item.getTitle().toString().equals("Undispatch")){

            taskStatus = "Open";
            updateTask();

        } else if (item.getTitle().toString().equals("Incomplete Time")){

            taskStatus = "Incomplete Time";
            updateTask();

        } else if (item.getTitle().toString().equals("Incomplete Assist")){

            taskStatus = "Incomplete Assist";
            updateTask();
        }
        return true;
    }

    private void updateOptionMenu (){//changes the options menu based on the task status

        menuItems.clear();

        if (allowDispatch){

            switch (taskStatus) {

                case "Open":
                case "Incomplete Time":
                case "Incomplete Assist":
                case "Parts On Order":

                    menuItems.add("Enroute");
                    menuItems.add("Order Parts");
                    menuItems.add("Cancel Task");
                    menuItems.add("Task List");
                    startOdometer.setEnabled(true);
                    endOdometer.setEnabled(false);
                    problem.setEnabled(false);
                    break;

                case "Enroute":

                    menuItems.add("Arrived");
                    menuItems.add("Undispatch");
                    menuItems.add("Cancel Task");
                    menuItems.add("Task List");
                    startOdometer.setEnabled(false);
                    endOdometer.setEnabled(true);
                    problem.setEnabled(false);
                    break;

                case "Arrived":

                    menuItems.add("Complete");
                    menuItems.add("Order Parts");
                    menuItems.add("Assign Parts to Task");
                    menuItems.add("Incomplete Time");
                    menuItems.add("Incomplete Assist");
                    menuItems.add("Task List");
                    startOdometer.setEnabled(false);
                    endOdometer.setEnabled(false);
                    problem.setEnabled(true);
                    break;
            }

        }else {

            menuItems.add("Cancel Task");
            menuItems.add("Task List");
        }
        onCreateOptionsMenu(myMenu);
    }

    private void updateTimeStamp(){

        dataMethod = "updateTimeStamp";
        subURL = "insertEditDelete.php/";
        query = "UPDATE tasktimestamp" +
                " SET TaskTimeStampEnroute = '" + dispatchTime +
                "', TaskTimeStampStart = '" + startTime +
                "', TaskTimeStampEnd = '" + endTime +
                "', TaskTimeStampStartMiles = " + startMiles +
                ", TaskTimeStampEndMiles = " + endMiles +
                " WHERE TaskTimeStampID = " + timeStampID + ";";

        new databaseInterface().execute();
    }

    private void updateTask(){

        dataMethod = "updateTask";
        subURL = "insertEditDelete.php/";
        query = "UPDATE task" +
                " SET TaskStatus = '" + taskStatus +
                "', TaskResolution = '" + taskNotes.getText().toString() +
                "' WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void deleteTask(){

        dataMethod = "deleteTask";
        subURL = "insertEditDelete.php/";
        query = "DELETE FROM task WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void getTasks(){

        taskID = Objects.requireNonNull(getIntent().getExtras()).getInt("taskID");
        dataMethod = "getTasks";
        subURL = "getTasks.php/";
        query = "SELECT * FROM task WHERE TaskID = " + taskID + ";";

        new databaseInterface().execute();
    }

    private void getDispatchedTask(){

        for (Task t : tasks){

            taskID = t.getTaskID();
            customerID = t.getCustomerID();
            description = t.getTaskDescription();
            taskStatus = t.getStatus();
        }

        dataMethod = "getDispatchedTask";
        subURL = "";
        query = "SELECT * FROM task" +
                " WHERE EmployeeID = " + TaskList.employeeID +
                " AND TaskStatus = (SELECT TaskStatus FROM task " +
                " WHERE TaskStatus = 'Enroute'" +
                " OR TaskStatus = 'Arrived');";
        new databaseInterface().execute();
    }

    private void getCustomers(){

        if (dispatched.size() > 0) {

            for (Task t : dispatched) {

                if (!taskID.equals(t.getTaskID())) allowDispatch = false;
            }
        }
        updateOptionMenu();

        dataMethod = "getCustomers";
        subURL = "getCustomers.php/";
        query = "SELECT * FROM customer WHERE CustomerID = " + customerID + ";";
        new databaseInterface().execute();
    }

    private void getTimeStamps(){

        for (Customer c : customers){

            customerName = c.getName();
            Address = c.getAddress();
            City = c.getCity();
            State = c.getState();
            Zip = c.getZip();
            Contact = c.getContact();
            Phone = c.getPhone();
        }

        dataMethod = "getTimeStamps";
        subURL = "getTaskTimeStamp.php/";
        query = "SELECT * FROM tasktimestamp WHERE TaskID = " + taskID + ";";
        new databaseInterface().execute();
    }

    private void getLineItems(){

        for (TaskTimeStamp t : taskTimeStamps){

            timeStampID = t.getTimeStampID();
            startMiles = t.getStartMiles();
            endMiles = t.getEndMiles();
            Created = t.getCreated();
            dispatchTime = t.getEnroute();
            startTime = t.getStart();
            endTime = t.getEnd();
        }

        dataMethod = "getLineItems";
        subURL = "getTaskLineItem.php/";
        query = "SELECT * FROM tasklineitem WHERE TaskID = " + taskID + ";";
        new databaseInterface().execute();
    }

    private void getInventory(){

        TaskLineItem t;
        dataMethod = "getInventory";
        subURL = "getInventory.php/";
        query = "SELECT * FROM inventory WHERE ";

        if (taskLineItems.size() > 0) {

            for (int i = 0; i < taskLineItems.size(); i++) {

                t = taskLineItems.get(i);
                query = query + " InventoryID = " + t.getInventoryID();
                if (i < taskLineItems.size() - 1) query = query + " OR ";
            }
            query = query + ";";
            new databaseInterface().execute();

        } else loadElements();
    }

    private void findElements(){//Assign layout items to variables

        customer = findViewById(R.id.TD_customer);
        address = findViewById(R.id.TD_address);
        city = findViewById(R.id.TD_city);
        zip = findViewById(R.id.TD_zip);
        state = findViewById(R.id.TD_state);
        contact = findViewById(R.id.TD_contact);
        phone = findViewById(R.id.TD_phone);
        problem = findViewById(R.id.TD_description);
        total = findViewById(R.id.TD_inventoryTotal);
        created = findViewById(R.id.TD_created);
        startOdometer = findViewById(R.id.TD_startOdometer);
        endOdometer = findViewById(R.id.TD_endOdometer);
        taskNotes = findViewById(R.id.TD_taskNotes);
    }

    private void loadElements(){//populate the layout items with task details

        double partsTotal = 0.0;

        for (TaskLineItem t : taskLineItems) {

            if (t.getTaskID().equals(taskID)) {

                for (Inventory i : inventory) {

                    if (i.getInvID().equals(t.getInventoryID())) partsTotal = partsTotal + i.getPrice();
                }
            }
        }

        String totaltext = "$" + df.format(partsTotal);
        total.setText(totaltext);

        customer.setText(customerName);
        address.setText(Address);
        city.setText(City);
        state.setText(State);
        zip.setText(Zip);
        contact.setText(Contact);
        phone.setText(Phone);
        problem.setText(description);
        created.setText(Created);
        startOdometer.setText(startMiles.toString());
        endOdometer.setText(endMiles.toString());
    }

    private void loadTaskList(){

        Intent i = new Intent(this, TaskList.class);
        i.putExtra("employeeID",employeeID);
        startActivity(i);
    }

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        Integer success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(TaskDetails.this);
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

            switch (dataMethod) {

                case "getTasks":
                    tasks = arrayListBuilder.BuildTask(data);
                    getDispatchedTask();
                    break;

                case "getDispatchedTask":
                    if (success == 1) dispatched = arrayListBuilder.BuildTask(data);
                    getCustomers();
                    break;

                case "getCustomers":
                    customers = arrayListBuilder.BuildCustomer(data);
                    getTimeStamps();
                    break;

                case "getTimeStamps":
                    if (success == 1) taskTimeStamps = arrayListBuilder.BuildTaskTimeStamp(data);
                    getLineItems();
                    break;

                case "getLineItems":
                    if (success == 1) taskLineItems = arrayListBuilder.BuildTaskLineItem(data);
                    getInventory();
                    break;

                case "getInventory":
                    if (success == 1) inventory = arrayListBuilder.BuildInventory(data);
                    loadElements();
                    break;

                case "updateTimeStamp":
                    updateTask();
                    break;

                case "SubmitTask":
                    loadTaskList();
                    break;

                case "Cancel Task":
                    deleteTask();
                    break;

                case "deleteTask":
                    loadTaskList();
                    break;

                case "updateTask":
                    if (taskStatus.equals("Incomplete Time") ||
                            taskStatus.equals("Complete") ||
                            taskStatus.equals("Incomplete Assist")) loadTaskList();
            }
        }
    }
}
