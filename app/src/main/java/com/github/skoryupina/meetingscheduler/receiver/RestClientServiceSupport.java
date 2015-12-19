package com.github.skoryupina.meetingscheduler.receiver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.skoryupina.Meeting;
import com.github.skoryupina.Participant;
import com.github.skoryupina.meetingscheduler.MainActivity;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.listeners.BackgroundErrorListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.BackgroundResponseListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.ErrorListenerForTasks;
import com.github.skoryupina.meetingscheduler.receiver.listeners.ListenerForService;
import com.github.skoryupina.meetingscheduler.receiver.listeners.RequestMeetingsErrorListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.RequestMeetingsResponseListener;
import com.github.skoryupina.meetingscheduler.receiver.listeners.ResponseListenerForTasks;
import com.github.skoryupina.meetingscheduler.R;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RestClientServiceSupport {

    public static void addParticipant(Intent i, String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        int id = i.getIntExtra(Meeting.ID, -1);
        String fio = i.getStringExtra(Participant.FIO);
        String position = i.getStringExtra(Participant.POSITION);
        String url = ListenerForService.mContext.getString(R.string.url_add_participant);
        try {
            fio = URLEncoder.encode(fio, "UTF-8");
            position = URLEncoder.encode(position, "UTF-8");
            url += "/" + username + "/" + password + "/" + id + "/" + fio + "/" + position;
            /**
             * Listeners
             */
            ListenerForService.mTaskCode = MeetingRestClientService.TASK_ADD_PARTICIPANT;
            ResponseListenerForTasks<JSONArray> responseListener = new ResponseListenerForTasks<JSONArray>();
            ErrorListenerForTasks errorListener = new ErrorListenerForTasks();
            //
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.PUT, url, null, responseListener, errorListener)
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    return params;
                }
            };
            request.setRetryPolicy(
                    new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestMeetings(String url, final int code) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        /**
         * Listeners
         */
        ListenerForService.mTaskCode = code;
        RequestMeetingsResponseListener<JSONArray> responseListener = new RequestMeetingsResponseListener<JSONArray>();
        RequestMeetingsErrorListener errorListener = new RequestMeetingsErrorListener();
        //
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public static void cancelMeetingRequest(Intent i, final String login, final String password) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        String url = ListenerForService.mContext.getString(R.string.url_cancel_meeting);
        final int id = i.getIntExtra(Meeting.ID, -1);

        try {
            /**
             * Listeners
             */
            ListenerForService.mTaskCode = MeetingRestClientService.TASK_DELETE_MEETING;
            ResponseListenerForTasks<JSONArray> responseListener = new ResponseListenerForTasks<JSONArray>();
            ErrorListenerForTasks errorListener = new ErrorListenerForTasks();
            //
            JsonArrayRequest request =
                    new JsonArrayRequest(Request.Method.DELETE, url, null, responseListener, errorListener) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Accept-Charset", "UTF-8");
                            headers.put(Meeting.ID, String.valueOf(id));
                            headers.put(Participant.LOGIN, login);
                            headers.put(Participant.PASSWORD, password);
                            return headers;
                        }
                    };
            request.setRetryPolicy(
                    new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addMeeting(Intent i, final String login, final String password) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);

        String meetingName = i.getStringExtra(Meeting.NAME);
        String description = i.getStringExtra(Meeting.DESCRIPTION);
        String startDate = i.getStringExtra(Meeting.STARTDATE);
        String endDate = i.getStringExtra(Meeting.ENDDATE);
        String priority = i.getStringExtra(Meeting.PRIORITY);
        String url = ListenerForService.mContext.getString(R.string.url_add_meeting);

        try {
            meetingName = URLEncoder.encode(meetingName, "UTF-8");
            description = URLEncoder.encode(description, "UTF-8");
            startDate = URLEncoder.encode(startDate, "UTF-8");
            endDate = URLEncoder.encode(endDate, "UTF-8");
            priority = URLEncoder.encode(priority, "UTF-8");
            url += "/" + login + "/" + password + "/" + meetingName
                    + "/" + description + "/" + startDate + "/" + endDate + "/" + priority;

            /**
             * Listeners
             */
            ListenerForService.mTaskCode = MeetingRestClientService.TASK_ADD_MEETING;
            ResponseListenerForTasks<JSONArray> responseListener = new ResponseListenerForTasks<JSONArray>();
            ErrorListenerForTasks errorListener = new ErrorListenerForTasks();
            //
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, responseListener, errorListener) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            return params;
                        }
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                            return params;
                        }
                    };
            request.setRetryPolicy(
                    new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getMeetingsBackground(String url) {
        RequestQueue queue = Volley.newRequestQueue(ListenerForService.mContext);
        /**
         * Listeners
         */
        ListenerForService.mTaskCode = MeetingRestClientService.TASK_ADD_MEETING;
        BackgroundResponseListener<JSONArray> responseListener = new BackgroundResponseListener<JSONArray>();
        BackgroundErrorListener errorListener = new BackgroundErrorListener();
        //
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, responseListener,errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(ListenerForService.RETRY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


}
