package com.github.skoryupina.meetingscheduler;

public class MeetingItem {
    private int id;
    private String meetingName;
    private String startDate;
    private String endDate;
    private String priority;

    public MeetingItem(int id, String meetingName, String startDate, String endDate, String priority) {
        this.id = id;
        this.meetingName = meetingName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPriority() {
        return priority;
    }
}