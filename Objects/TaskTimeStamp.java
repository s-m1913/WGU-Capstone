package com.example.thegreatmugwump.taskmanager.Objects;

//Creates an TaskTimeStamp Object
public class TaskTimeStamp {

    private int timeStampID;
    private int taskID;
    private int startMiles;
    private int endMiles;
    private String created;
    private String enroute;
    private String startTime;
    private String endTime;

    public TaskTimeStamp(int timeStampID, int taskID, String created, int startMiles, int endMiles, String enroute, String startTime, String endTime) {

        this.timeStampID = timeStampID;
        this.taskID = taskID;
        this.startMiles = startMiles;
        this.endMiles = endMiles;
        this.created = created;
        this.enroute = enroute;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getStartMiles() {
        return startMiles;
    }

    public void setstartMiles(int miles) {
        this.startMiles = miles;
    }

    public int getEndMiles() {
        return endMiles;
    }

    public void setsEndMiles(int miles) {
        this.endMiles = miles;
    }

    public String getCreated() {
        return created;
    }

    public int getTimeStampID() {
        return timeStampID;
    }

    public void setTimeStampID(int timeStampID) {
        this.timeStampID = timeStampID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getStart() {
        return startTime;
    }

    public void setStart(String start) {
        this.startTime = start;
    }

    public String getEnd() {
        return endTime;
    }

    public void setEnd(String end) {
        this.endTime = end;
    }

    public String getEnroute() {
        return enroute;
    }

    public void setEnroute(String enroute) {
        this.enroute = enroute;
    }
}
