package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import com.android.volley.Response;
import com.github.skoryupina.meetingscheduler.R;

public class BackgroundResponseListener <T> extends ListenerForService implements Response.Listener<T>  {
    @Override
    public void onResponse(T response) {
        Bundle bundle = new Bundle();
        T array = response;
        if (!response.toString().equals(RESULT_ERROR)) {
            if(!response.toString().equals(EMPTY_LIST)){
                JSONArray read = readJsonObject();
                if(read!=null){
                    if(!array.equals(read)){
                        writeJsonObject((JSONArray)array);
                        if(!checkApp()) {
                            showNotification();
                        } else {
                            Intent i = new Intent(mContext.getString(R.string.intent_name)).putExtra("msg", mContext.getString(R.string.intent_received_msg));
                            mRestClientService.sendBroadcast(i);
                            Log.d(TAG, "onResponse " + response.toString());
                        }
                    }
                }
            }
        } else {
            if(checkApp()) {
                Intent i = new Intent(mContext.getString(R.string.intent_name))
                                    .putExtra("msg", mContext.getString(R.string.rest_server_not_available));
                mRestClientService.sendBroadcast(i);
                Log.d(TAG, "onResponse " + response.toString());
            }
        }
    }


}
