package com.github.skoryupina.meetingscheduler.receiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.github.skoryupina.Meeting;
import com.github.skoryupina.Participant;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.listeners.ListenerForService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MeetingRestClientService extends IntentService {
    //for Debug
    private static final String TAG = "MeetingClientService";

    public static final int TASK_REQUEST_MEETINGS = 1;
    public static final int TASK_ADD_PARTICIPANT = 2;
    public static final int TASK_GET_DETAIS = 3;
    public static final int TASK_DELETE_MEETING = 4;
    public static final int TASK_ADD_MEETING = 5;
    public static final int TASK_FIND_MEETING_BY_DESCRIPTION = 6;
    public static final int TASK_BACKGROUND_RECEIVE = 7;

    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 0;

    public static final String TASK_CODE = "codeTask";
    public static final String RECEIVER = "receiver";

    public MeetingRestClientService() {
        super("MeetingRestClientService");
    }

    protected void onHandleIntent(Intent i) {

        String login = i.getStringExtra(Participant.LOGIN);
        String password = i.getStringExtra(Participant.PASSWORD);
        int taskCode = i.getIntExtra(TASK_CODE, -1);
        ListenerForService.mReceiver = i.getParcelableExtra(RECEIVER);
        ListenerForService.mContext = getApplicationContext();
        switch (taskCode) {
            case TASK_REQUEST_MEETINGS: {
                String url = getString(R.string.url_request_meeting);
                url += "?" + Participant.LOGIN + "=" + login + "&" + Participant.PASSWORD + "=" + password;
                RestClientServiceSupport.requestMeetings(url, taskCode);
            }
            break;
            case TASK_ADD_PARTICIPANT: {
                RestClientServiceSupport.addParticipant(i, login, password);
            }
            break;
            case TASK_GET_DETAIS: {
                final int id = i.getIntExtra(Meeting.ID, -1);
                String url = getString(R.string.url_get_details);
                url += "?" + Participant.LOGIN + "=" + login + "&" + Participant.PASSWORD + "=" + password +
                        "&" + Meeting.ID + "=" + id;
                RestClientServiceSupport.requestMeetings(url, taskCode);
            }
            break;
            case TASK_DELETE_MEETING: {
                RestClientServiceSupport.cancelMeetingRequest(i, login, password);
            }
            break;
            case TASK_ADD_MEETING: {
                RestClientServiceSupport.addMeeting(i, login, password);
            }
            break;
            case TASK_FIND_MEETING_BY_DESCRIPTION: {
                try {
                    String description = i.getStringExtra(Meeting.DESCRIPTION);
                    description = URLEncoder.encode(description, "UTF-8");
                    String url = getString(R.string.url_get_meeting_by_description);
                    url += "?" + Participant.LOGIN + "=" + login + "&" + Participant.PASSWORD + "=" + password
                            + "&" + Meeting.DESCRIPTION + "=" + description;
                    RestClientServiceSupport.requestMeetings(url, taskCode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onHandleIntent " + e.getMessage());
                }
            }
            break;

            case TASK_BACKGROUND_RECEIVE: {
                String url = getString(R.string.url_request_meeting);
                url += "?" + Participant.LOGIN + "=" + login + "&" + Participant.PASSWORD + "=" + password;
                RestClientServiceSupport.getMeetingsBackground(url);
            }
            break;
        }
    }
}
