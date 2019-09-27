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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.thegreatmugwump.taskmanager.Objects.TaskLineItem;
import com.example.thegreatmugwump.taskmanager.Objects.TaskTimeStamp;
import com.example.thegreatmugwump.taskmanager.helper.ArrayListBuilder;
import com.example.thegreatmugwump.taskmanager.helper.HttpJsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Reports extends AppCompatActivity {

    private Spinner time;
    private TextView title;
    private TableLayout table;
    private String query = "";
    private String subURL;
    private String dataMethod;
    private String employeeID = TaskList.employeeID;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
    private ArrayListBuilder arrayListBuilder = new ArrayListBuilder();
    private ArrayList<TaskTimeStamp> timestamps = new ArrayList<>();
    private ArrayList<TaskLineItem> taskLineItems = new ArrayList<>();//to be implemented in a future version

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        assert getSupportActionBar() != null;

        findElements();
        loadElements();
        getTimeStamps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Mileage Report");
        menu.add("Task Report");
        menu.add("Task List");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Mileage Report")){

            loadMileageReport();

        }else if (item.getTitle().toString().equals("Task Report")){

            loadTaskReport();

        }else if (item.getTitle().toString().equals("Task List")){

            Intent i = new Intent(this, TaskList.class);
            i.putExtra("employeeID",employeeID);
            startActivity(i);
        }

        return true;
    }

    private void getTimeStamps(){

        dataMethod = "getTimeStamps";
        subURL = "getTaskTimeStamp.php/";
        query = "SELECT *" +
                " FROM tasktimestamp tts, task t" +
                " WHERE t.EmployeeID = " + TaskList.employeeID +
                " AND t.TaskID = tts.TaskID" +
                " AND t.TaskStatus = 'Complete';";

        new databaseInterface().execute();
    }

    private void getInventoryUsed(){

        dataMethod = "getInventoryUsed";
        subURL = "getTaskLineItem.php/";
        query = "SELECT * " +
                " FROM tasklineitem l, task t " +
                " WHERE l.TaskID = t.TaskID " +
                " AND t.EmployeeID = " + TaskList.employeeID + ";";
    }

    private void findElements(){//Assign layout items to variables

        time = findViewById(R.id.Report_Time);
        title = findViewById(R.id.Report_Title);
        table = findViewById(R.id.Report_Table);
    }

    private void loadElements(){//populate the dropdown menu with report options


        ArrayList<String> timeList = new ArrayList<>();

        timeList.add("1 Month");
        timeList.add("3 Month");
        timeList.add("1 Year");

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeList);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.time.setAdapter(adp);

    }

    private void loadMileageReport(){//

        int begin ;
        String reportType = time.getSelectedItem().toString();
        String[] data  = new String[2];
        LocalDateTime now = LocalDateTime.now();
        table.removeAllViews();

        //Set title and date
        String text = "Mileage Report: " + dateFormat.format(now);
        title.setText(text);

        //Load date array with date and mile data
        if (timestamps.size() == 0) {

            noDataMsg();

        }else {
            //Create column headers
            data[0] = "Date ";
            data[1] = "Miles ";
            TableRow tr = newTableRow(data);
            table.addView(tr);

            //Display items that match the report parameters
            if (reportType.equals("1 Month")) {
                begin = 1;

            } else if (reportType.equals("3 Month")) {
                begin = 3;

            } else begin = 12;

            for (TaskTimeStamp t: timestamps){

                LocalDateTime taskDate = LocalDateTime.parse(t.getStart(), dateTimeFormat);

                if(taskDate.isAfter(now.minusMonths(begin))){

                    Integer i = t.getEndMiles() - t.getStartMiles();

                    data[0] = t.getStart().substring(0, 10);
                    data[1] = i.toString();

                    tr = newTableRow(data);
                    table.addView(tr);
                }
            }
            highlightRows();
        }
    }

    private void loadTaskReport(){

        int begin;
        String reportType = time.getSelectedItem().toString();
        String[] data  = new String[3];
        LocalDateTime now = LocalDateTime.now();
        LocalTime travelTime;
        table.removeAllViews();

        //Set title and date
        String text = "Task Report: " + dateFormat.format(now);
        title.setText(text);

        //Load date array
        if (timestamps.size() == 0) {

            noDataMsg();

        }else {

            //Create column headers
            data[0] = "Date ";
            data[1] = "Travel Time";
            data[2] = "Task Time";
            TableRow tr = newTableRow(data);
            table.addView(tr);

            //Display items that match the report parameters
            if (reportType.equals("1 Month")) {
                begin = 1;

            } else if (reportType.equals("3 Month")) {
                begin = 3;

            } else begin = 12;

            for (TaskTimeStamp t: timestamps){

                LocalDateTime taskDate = LocalDateTime.parse(t.getStart(), dateTimeFormat);

                if(taskDate.isAfter(now.minusMonths(begin))){

                    int enrouteHour = Integer.parseInt(t.getEnroute().substring(11,13));
                    int enrouteMinute = Integer.parseInt(t.getEnroute().substring(14,16));
                    travelTime = LocalTime.parse(t.getStart().substring(11)).minusHours(enrouteHour);
                    travelTime.minusMinutes(enrouteMinute);

                    int startHour = Integer.parseInt(t.getStart().substring(11,13));
                    int startMinute = Integer.parseInt(t.getStart().substring(14,16));
                    travelTime = LocalTime.parse(t.getEnd().substring(11)).minusHours(startHour);
                    travelTime.minusMinutes(startMinute);

                    data[0] = t.getStart().substring(0, 10);
                    data[1] = travelTime.format(timeFormat);
                    data[2] = travelTime.format(timeFormat);

                    tr = newTableRow(data);
                    table.addView(tr);
                }
            }
        }
        highlightRows();
    }

    private TableRow newTableRow(String[] data){

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

    private void highlightRows(){//Highlights table rows with alternate white/gray backgrounds

        int count = 0;
        for (int i = 0; i < table.getChildCount(); i++) {

            View view1 = table.getChildAt(i);

            if (view1 instanceof TableRow) {

                if (count % 2 != 0) view1.setBackgroundColor(Color.LTGRAY);
                if (count % 2 == 0) view1.setBackgroundColor(Color.WHITE);
                count++;
            }
        }
    }

    private void noDataMsg(){

        title.setText("No Data Available");
    }

    private class databaseInterface extends AsyncTask<String, String, String> {

        ProgressBar progressBar;
        int success = 0;
        JSONArray data;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //Display progress bar
            progressBar = new ProgressBar(Reports.this);
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

            if (dataMethod.equals("getTimeStamps")) {

                if (success == 1) timestamps = arrayListBuilder.BuildTaskTimeStamp(data);
                getInventoryUsed();

            }else if (dataMethod.equals("getInventoryUsed")) {

                if (success == 1) taskLineItems = arrayListBuilder.BuildTaskLineItem(data);

            }
        }
    }
}
