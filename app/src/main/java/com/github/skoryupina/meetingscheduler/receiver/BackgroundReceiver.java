package com.github.skoryupina.meetingscheduler.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.skoryupina.Participant;

public class BackgroundReceiver extends BroadcastReceiver {
    public static final String PREFERENCES = BackgroundReceiver.class.getPackage().getName() + "_preferences";

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MeetingRestClientService.class);
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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
