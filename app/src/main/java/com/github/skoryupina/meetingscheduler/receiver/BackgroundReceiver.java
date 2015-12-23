package com.github.skoryupina.meetingscheduler.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.skoryupina.Participant;
import com.github.skoryupina.meetingscheduler.R;

public class BackgroundReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MeetingRestClientService.class);
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.shared_preferenced_name), Context.MODE_PRIVATE);
        if (preferences != null) {
            if (preferences.contains(Participant.LOGIN) && preferences.contains(Participant.PASSWORD)) {
                String login = preferences.getString(Participant.LOGIN, "");
                String password = preferences.getString(Participant.PASSWORD, "");
                i.putExtra(MeetingRestClientService.TASK_CODE, MeetingRestClientService.TASK_BACKGROUND_RECEIVE);
                i.putExtra(Participant.LOGIN, login);
                i.putExtra(Participant.PASSWORD, password);
                context.startService(i);
            }
        }
    }
}
