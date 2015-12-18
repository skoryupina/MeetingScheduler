package com.github.skoryupina.meetingscheduler.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.skoryupina.Meeting;
import com.github.skoryupina.Participant;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.listeners.AddParticipantErrorListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.AddParticipantResponseListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.ListenerForService;
import com.github.skoryupina.meetingscheduler.receiver.listeners.RequestMeetingsErrorListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.RequestMeetingsResponseListener;

import org.json.JSONArray;

import java.net.URLEncoder;

public class RestClientServiceSupport {

    public void addParticipant(Intent i, String username,String password, String url) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        int id = i.getIntExtra(Meeting.ID, -1);
        String fio = i.getStringExtra(Participant.FIO);
        String position = i.getStringExtra(Participant.POSITION);
        //url = getString(R.string.urlAddParticipant);
        try {
            fio = URLEncoder.encode(fio, "UTF-8");
            position = URLEncoder.encode(position, "UTF-8");
            url += "/" + username + "/" + password + "/" + id + "/" + fio + "/" + position;
            AddParticipantResponseListener<JSONArray> responseListener = new AddParticipantResponseListener<>();
            AddParticipantErrorListener errorListener = new AddParticipantErrorListener();
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.PUT, url, null, responseListener,errorListener);
            request.setRetryPolicy(
                    new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestMeetings(final ResultReceiver receiver, String url, final int code) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        RequestMeetingsResponseListener<JSONArray> responseListener = new RequestMeetingsResponseListener<>();
        RequestMeetingsErrorListener errorListener = new RequestMeetingsErrorListener();
        JsonArrayRequest request =
                new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);
        request.setRetryPolicy(
                new DefaultRetryPolicy(3 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
