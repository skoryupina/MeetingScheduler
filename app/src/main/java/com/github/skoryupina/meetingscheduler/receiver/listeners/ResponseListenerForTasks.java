package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;

public class ResponseListenerForTasks  <JSONArray> extends ListenerForService implements Response.Listener<JSONArray>  {
    @Override
    public void onResponse(JSONArray response) {
        Bundle bundle = new Bundle();
        if (response.toString().equals(RESULT_ERROR)) {
            String message = mContext.getString(R.string.rest_login_or_password_error);
            prepareResponse(bundle, message, mTaskCode,
                    MeetingRestClientService.RESULT_ERROR, mReceiver);
        } else {
            bundle.putInt(MeetingRestClientService.TASK_CODE, mTaskCode);
            mReceiver.send(MeetingRestClientService.RESULT_OK, bundle);
        }
        Log.d(TAG, "onResponse " + response.toString());
    }
}