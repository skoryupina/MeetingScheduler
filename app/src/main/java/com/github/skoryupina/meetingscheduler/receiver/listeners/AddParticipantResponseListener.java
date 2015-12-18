package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.os.Bundle;

import com.android.volley.Response;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;

public class AddParticipantResponseListener<JSONArray> extends ListenerForService implements Response.Listener<JSONArray> {
    @Override
    public void onResponse(JSONArray response) {
        Bundle bundle = new Bundle();
        if (response.toString().equals(RESULT_ERROR)) {
            String message = mContext.getString(R.string.rest_login_or_password_error);
            prepareResponse(bundle, message, MeetingRestClientService.TASK_ADD_PARTICIPANT,
                    MeetingRestClientService.RESULT_ERROR, mReceiver);
        } else {
            bundle.putInt(MeetingRestClientService.TASK_CODE, MeetingRestClientService.TASK_ADD_PARTICIPANT);
            mReceiver.send(MeetingRestClientService.RESULT_OK, bundle);
        }
    }
}
