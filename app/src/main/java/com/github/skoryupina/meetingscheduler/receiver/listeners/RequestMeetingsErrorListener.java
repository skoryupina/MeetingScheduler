package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;

public class RequestMeetingsErrorListener extends ListenerForService implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        Bundle bundle = new Bundle();
        String message = mContext.getString(R.string.rest_server_not_available);
        prepareResponse(bundle, message, mTaskCode,
                MeetingRestClientService.RESULT_ERROR, mReceiver);
    }
}
