package com.github.skoryupina;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import javax.ejb.Stateless;

@Stateless
@Path("/")
public class SchedulerBack {
    public static ArrayList<Meeting> meetings = new ArrayList<>();

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


    private void addMeetingToList(String name,
                            String description,
                            String startdate,
                            String enddate,
                            String priority) {
        try {
            name = URLDecoder.decode(name, "UTF-8");
            description = URLDecoder.decode(description, "UTF-8");
            startdate = URLDecoder.decode(startdate, "UTF-8");
            enddate= URLDecoder.decode(enddate, "UTF-8");
            priority = URLDecoder.decode(priority, "UTF-8");
            if (priority.endsWith("\r\n")){
                priority = priority.substring(0, priority.length() - 2);
            }
            Meeting meeting = new Meeting(name,description,startdate,enddate,priority);
            meetings.add(meeting);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }
}
