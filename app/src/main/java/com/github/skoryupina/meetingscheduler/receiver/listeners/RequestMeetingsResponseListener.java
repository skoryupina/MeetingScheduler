package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;

import org.json.JSONArray;

public class RequestMeetingsResponseListener<JSONArray> extends ListenerForService implements Response.Listener<JSONArray>  {
    public static final int mCode = -1;
        @Override
        public void onResponse(JSONArray response) {
            Bundle bundle = new Bundle();
            JSONArray array = response;
            if (response.toString().equals(ListenerForService.RESULT_OK)) {
                String message = mContext.getString(R.string.rest_login_or_password_error);
                prepareResponse(bundle, message, mCode, MeetingRestClientService.RESULT_ERROR, mReceiver);
            }  else if(response.toString().equals(EMPTY_LIST)) {
                String message = mContext.getString(R.string.data_not_found);
                prepareResponse(bundle, message, mCode, MeetingRestClientService.RESULT_ERROR, mReceiver);
            } else {
                if(mCode==MeetingRestClientService.TASK_REQUEST_MEETINGS)
                    //writeJsonObject(array);
                bundle.putString(mContext.getString(R.string.result), response.toString());
                bundle.putInt(MeetingRestClientService.TASK_CODE, mCode);
                mReceiver.send(MeetingRestClientService.RESULT_OK, bundle);
            }
        }
}
