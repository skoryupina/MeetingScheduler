package com.github.skoryupina;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import javax.ejb.Stateless;

@Stateless
@Path("/")
public class SchedulerBack {
    public static ArrayList<Meeting> meetings = new ArrayList<>();

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String RESULT_OK = "[{\"result\":\"OK\"}]";
    private static final String RESULT_ERROR = "[{\"result\":\"Error\"}]";
    private static final String EMPTY_LIST = "[]";
    @GET
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public String getWelcomePage() {
        return "<html>" +
                "<head>\n" +
                " <meta http-equiv=\"CONTENT-TYPE\" content=\"text/html; charset=UTF-8\"/>\n" +
                " <title>Meetings server</title>\n" +
                "</head>\n" +
                "<body>" +
                "<form action=\"createMeeting\" method=\"GET\">" +
                "<h2>Новая встреча</h2>\n" +
                "<table>\n" +
                "    <tr>\n" +
                "      <td>Name</td>\n" +
                "      <td><input type=\"text\" id=\"name\" required placeholder=\"Input name\" name=\"name\" size=\"80\"/></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td>Description</td>\n" +
                "      <td><input type=\"text\" id=\"description\" required placeholder=\"Input description\" name=\"description\"size=\"80\"/></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td class=\"insert\">Start date</td>\n" +
                "      <td><input type=\"text\" id=\"startDate\" name=\"startDate\" size=\"80\" required placeholder=\"Input start date (yyyy-mm-dd HH:mm)\" pattern=\"^(19|20)\\d\\d-((0((1-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)|(2-(0[1-9]|1[0-9]|2[0-8]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)|([3-9]-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)))|(1[012]-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$))$\" /></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td class=\"insert\">Finish date</td>\n" +
                "      <td><input type=\"text\" id=\"endDate\" name=\"endDate\" size=\"80\" required placeholder=\"Input finish date (ГГГГ-ММ-ДД ЧЧ:мм)\" pattern=\"^(19|20)\\d\\d-((0((1-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)|(2-(0[1-9]|1[0-9]|2[0-8]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)|([3-9]-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)))|(1[012]-(0[1-9]|[12][0-9]|3[01]) ([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$))$\" /></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td class=\"insert\">Priority</td>\n" +
                "      <td><input type=\"text\" id=\"priority\" name=\"priority\" size=\"80\" required placeholder=\"Input priority (URGENT, PLANNED,OPTIONAL)\"></td>\n" +
                "   </tr>\n" +
                "  </table>\n" +
                "<input type=\"reset\" value=\"CLEAR\" name=\"clear\"/>\n" +
                "&nbsp;&nbsp;\n" +
                "<input type=\"submit\" value=\"SAVE\" name=\"submit\"/>\n" +
                "&nbsp;&nbsp;</form></body></html>";
    }

    @GET
    @Path("/createMeeting")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String createMeeting(@QueryParam(Meeting.NAME) String name,
                                @QueryParam(Meeting.DESCRIPTION) String description,
                                @QueryParam(Meeting.STARTDATE) String begindate,
                                @QueryParam(Meeting.ENDDATE) String enddate,
                                @QueryParam(Meeting.PRIORITY) String priority
    ) {
        addMeetingToList(name, description, begindate, enddate, priority);
        return meetings.toString();
    }

    /***
     * Add meeting to the list of meetings. Params are characteristics of new meeting
    */

    private void addMeetingToList(String name,
                                  String description,
                                  String startdate,
                                  String enddate,
                                  String priority) {
        try {
            name = URLDecoder.decode(name, "UTF-8");
            description = URLDecoder.decode(description, "UTF-8");
            startdate = URLDecoder.decode(startdate, "UTF-8");
            enddate = URLDecoder.decode(enddate, "UTF-8");
            priority = URLDecoder.decode(priority, "UTF-8");
            if (priority.endsWith("\r\n")) {
                priority = priority.substring(0, priority.length() - 2);
            }
            Meeting meeting = new Meeting(name, description, startdate, enddate, priority);
            meetings.add(meeting);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    //1. Get today meetings
    @GET
    @Path("/getTodayMeetings")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String getTodayMeetings(@QueryParam(Participant.LOGIN) String login,
                                   @QueryParam(Participant.PASSWORD) String password) {
        if (LOGIN.equals(login) && PASSWORD.equals(password)) {
            ArrayList<Meeting> todayMeetings = getTodayMeetings();
            if (todayMeetings != null)
                return todayMeetings.toString();
            else
                return EMPTY_LIST;
        } else
            return RESULT_ERROR;

    }

    private ArrayList<Meeting> getTodayMeetings() {
        final int TIME_LENGTH = 6; //" HH:mm"
        Date currentDateTime = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat formatDateTime = new SimpleDateFormat(DATETIME_FORMAT);
        try {
            String todayDate = formatDate.format(currentDateTime);
            ArrayList<Meeting> todayMeetings = new ArrayList<>();
            for (Meeting m : meetings) {
                String startDate = m.getStartDate();
                startDate = startDate.substring(0, startDate.length() - TIME_LENGTH);
                if (startDate.equals(todayDate)) {
                    Date endDate = formatDateTime.parse(m.getEndDate());
                    int compare = currentDateTime.compareTo(endDate);
                    System.out.println(compare);
                    if (compare == -1 || compare == 0) {
                        todayMeetings.add(m);
                    }
                }
            }
            return todayMeetings;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //2. Add participant to meeting
    @PUT
    @Path("/addParticipant/{"
            + Participant.LOGIN + "}/{"
            + Participant.PASSWORD + "}/{"
            + Meeting.ID + "}/{"
            + Participant.FIO + "}/{"
            + Participant.POSITION + "}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    public String addParticipant(@PathParam(Participant.LOGIN ) String login,
                                 @PathParam(Participant.PASSWORD) String password,
                                 @PathParam(Meeting.ID) String id,
                                 @PathParam(Participant.FIO ) String fio,
                                 @PathParam(Participant.POSITION) String position) {
        if (LOGIN.equals(login) && PASSWORD.equals(password)) {
            try {
                fio = URLDecoder.decode(fio, "UTF-8");
                position = URLDecoder.decode(position, "UTF-8");
                Participant participant = new Participant(fio,position);
                Meeting m = findMeetingById(Integer.parseInt(id));
                if (m != null) {
                    meetings.remove(m);
                    ArrayList<Participant> participants = m.getParticipantsList();
                    participants.add(participant);
                    m.setParticipantsList(participants);
                    meetings.add(m);
                }
                return RESULT_OK;
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
                return RESULT_ERROR;
            }
        } else
            return RESULT_ERROR;

    }

    private Meeting findMeetingById(int id) {
        Meeting meeting = null;
        for (Meeting m : meetings) {
            if (m.getID() == id) {
                meeting = m;
            }
        }
        return meeting;
    }

    //3. Request detailed meeting info
    @GET
    @Path("/getDescription")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String getDescription(@PathParam(Participant.LOGIN ) String login,
                                 @PathParam(Participant.PASSWORD) String password,
                                 @PathParam(Meeting.ID) String id) {
        String description;
        if (LOGIN.equals(login) && PASSWORD.equals(password)) {
            try {
                Meeting meeting = findMeetingById(Integer.parseInt(id));
                if (meeting != null) {
                    description = meeting.getDetails();
                } else
                    description = EMPTY_LIST;
                return description;
            } catch (Exception e) {
                e.printStackTrace();
                return RESULT_ERROR;
            }
        } else {
            return RESULT_ERROR;
        }
    }
}
