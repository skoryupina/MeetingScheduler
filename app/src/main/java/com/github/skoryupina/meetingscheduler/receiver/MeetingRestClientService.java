package com.github.skoryupina.meetingscheduler.receiver;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.ResultReceiver;

import com.github.skoryupina.Meeting;
import com.github.skoryupina.Participant;

import java.io.UnsupportedEncodingException;

public class MeetingRestClientService extends IntentService {
    public static final int TASK_REQUEST_MEETINGS = 1;
    public static final int TASK_ADD_PARTICIPANT = 2;
    public static final int TASK_GET_DETAIS= 3;
    public static final int TASK_DELETE_MEETING = 4;
    public static final int TASK_ADD_MEETING = 5;
    public static final int TASK_FIND_MEETING_BY_DESCRIPTION = 6;
    public static final int TASK_BACKGROUND_RECEIVE = 7;
    private static String meetingsFileName = "meetings.json";
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 0;
    private NotificationManager mManager;
    public static final String TASK_CODE = "codeTask";
    public static final String RECEIVER = "receiver";
    private String url;
    private String username;
    private String password;

    public MeetingRestClientService() {
        super("MeetingRestClientService");
    }

    protected void onHandleIntent(Intent i) {
        username = i.getStringExtra(Participant.LOGIN);
        password = i.getStringExtra(Participant.PASSWORD);
        int taskCode = i.getIntExtra(TASK_CODE, -1);
        final ResultReceiver receiver = i.getParcelableExtra(RECEIVER);
        switch (taskCode) {
            case TASK_REQUEST_MEETINGS: {

            }break;
            case TASK_ADD_PARTICIPANT:{

            }break;
            case TASK_GET_DETAIS:{

            }break;
            case TASK_DELETE_MEETING:{

            }break;
            case TASK_ADD_MEETING:{

            }break;
            case TASK_FIND_MEETING_BY_DESCRIPTION:{

            }break;

            case TASK_BACKGROUND_RECEIVE:{

            }
        }
    }
}
