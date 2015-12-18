package com.github.skoryupina;

/**
 * Created by Ekaterina on 04.11.2015.
 */
public class Participant {
    /***
     * FIO of the meeting participant
     */
    private String fio;
    /***
     * Position of the meeting participant
     */
    private Position position;

    private String login;
    private String password;
    /***
     * Types of positions in the project
     */
    public enum Position {
        CHIEF,
        COMMERCIAL_MANAGER,
        PROJECT_MANAGER,
        BUSINESS_ANALYST,
        TEAM_LEAD,
        SENIOR_JAVA_DEVELOPER,
        SENIOR_QA,
        FRONT_END_DEVELOPER,
        ANDROID_DEVELOPER,
        SUPPORT_ENGINEER
    }

    public Participant(String fio, Position position, String login) {
        setFio(fio);
        setPosition(position);
    }

    public Participant() {
        setFio("Default name");
        setPosition(Position.ANDROID_DEVELOPER);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "{" +
                "\"fio=\":" + "\""+fio + "\"" +
                ", \"position\":" + "\"" + position +"\""+
                "}";
    }

}
