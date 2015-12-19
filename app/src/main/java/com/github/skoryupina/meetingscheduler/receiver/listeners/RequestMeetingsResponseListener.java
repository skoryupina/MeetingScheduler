package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.os.Bundle;
import com.android.volley.Response;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;
import org.json.JSONArray;

public class RequestMeetingsResponseListener<JSONArray> extends ListenerForService implements Response.Listener <JSONArray> {
    @Override
    public void onResponse( JSONArray response) {
        Bundle bundle = new Bundle();
        org.json.JSONArray array = (org.json.JSONArray)response;
        if (response.toString().equals(ListenerForService.RESULT_OK)) {
            String message = mContext.getString(R.string.rest_login_or_password_error);
            prepareResponse(bundle, message, mTaskCode, MeetingRestClientService.RESULT_ERROR, mReceiver);
        } else if (response.toString().equals(EMPTY_LIST)) {
            String message = mContext.getString(R.string.data_not_found);
            prepareResponse(bundle, message, mTaskCode, MeetingRestClientService.RESULT_ERROR, mReceiver);
        } else {
            if (mTaskCode == MeetingRestClientService.TASK_REQUEST_MEETINGS) {
                writeJsonObject(array);
            }
            bundle.putString(mContext.getString(R.string.result), response.toString());
            bundle.putInt(MeetingRestClientService.TASK_CODE, mTaskCode);
            mReceiver.send(MeetingRestClientService.RESULT_OK, bundle);
        }
    }
}
