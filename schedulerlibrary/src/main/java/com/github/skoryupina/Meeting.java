package com.github.skoryupina;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Ekaterina on 04.11.2015.
 */
public class Meeting {
    private static int autoID = 0;
    private int id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private HashSet<Participant> participantsList = new HashSet<>();
    private Priority priority;

    public enum Priority {
        URGENT,
        PLANNED,
        OPTIONAL
    }

    public Meeting(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = startDate;
    }


    public Meeting(String name, String description, Date startDate, Date endDate,
                   HashSet<Participant> participantsList, Priority priority) {
        this(name, startDate, endDate);
        this.description = description;
        this.priority = priority;

        for (Participant participant : participantsList) {
            addParticipant(participant);
        }
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public HashSet<Participant> getParticipantsList() {
        return participantsList;
    }

    public void setParticipantsList(HashSet<Participant> participantsList) {
        this.participantsList = participantsList;
    }

    public void addParticipant(Participant participant) {
        participantsList.add(participant);
    }

    /***
     * Remove participant from the set
     *
     * @param participant Participant to remove
     * @return true if the participant was in the list and removed
     * false - otherwise
     */
    public boolean deleteParticipant(Participant participant) {
        return participantsList.remove(participant);
    }


    @Override
    public String toString() {
        return "{" + "\"id\":\"" + id + "\", " +
                "\"name\":" + "\"" + name + "\"" +
                ", \"startDate\":" + "\"" + startDate + "\"" +
                ", \"endDate\":" + "\"" + endDate + "\"" +
                ", \"priority\":\"" + priority.toString() + "\"}";
    }


    public String getDetails() {
        StringBuilder details = new StringBuilder("{" + "\"description\":\"" + description + "\"");
        if (participantsList.size() > 0) {
            details.append(", \"participants\": [");
            for (Participant participant : participantsList) {
                details.append(participant.toString() + ",");
            }
            details.deleteCharAt(details.length() - 1);
            details.append("]");
        }
        details.append("}");
        return details.toString();
    }
}
