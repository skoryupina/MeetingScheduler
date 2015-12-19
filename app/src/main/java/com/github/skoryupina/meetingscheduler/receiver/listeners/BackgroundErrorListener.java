package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.content.Intent;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.skoryupina.meetingscheduler.R;

public class BackgroundErrorListener extends ListenerForService implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        if (checkApp()) {
            Intent i = new Intent(mContext.getString(R.string.intent_name)).putExtra("msg", mContext.getString(R.string.rest_server_not_available));
            mRestClientService.sendBroadcast(i);
        }
    }


}
