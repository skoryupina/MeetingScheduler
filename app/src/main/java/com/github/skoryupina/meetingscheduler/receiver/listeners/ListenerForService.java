package com.github.skoryupina.meetingscheduler.receiver.listeners;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;

public class ListenerForService {
    public static final ResultReceiver mReceiver = null;
    public static final Context mContext = null;
    public static final int RETRY_TIMEOUT = 3000;
    public static final String RESULT_OK = "[{\"result\":\"OK\"}]";
    public static final String RESULT_ERROR = "[{\"result\":\"Error\"}]";
    public static final String EMPTY_LIST = "[]";

    public void prepareResponse(Bundle bundle,String extraText, int TASK_TYPE, int RESULT_CODE,  ResultReceiver resultReceiver){
        bundle.putString(Intent.EXTRA_TEXT, extraText);
        bundle.putInt(MeetingRestClientService.TASK_CODE, TASK_TYPE);
        resultReceiver.send(RESULT_CODE, bundle);
    }
}
