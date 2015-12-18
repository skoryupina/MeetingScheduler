package com.github.skoryupina.meetingscheduler.receiver.listeners;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class RequestMeetingsErrorListener extends ListenerForService implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        ////TODO write method
    }
}
