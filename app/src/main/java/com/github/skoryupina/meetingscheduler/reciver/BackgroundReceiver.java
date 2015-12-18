package com.github.skoryupina.meetingscheduler.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.skoryupina.Participant;

public class BackgroundReceiver extends BroadcastReceiver {
    public static final String TASK_CODE = "codeTask";
    public static final String PREFERENCES = BackgroundReceiver.class.getPackage().getName() + "_preferences";
    public static final String RECEIVER = "receiver";
    private static final int TASK_BACKGROUND_RECEIVE = 7;

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MeetingRestClientService.class);
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (preferences != null) {
            if (preferences.contains(Participant.LOGIN) && preferences.contains(Participant.PASSWORD)) {
                String login = preferences.getString(Participant.LOGIN, "");
                String password = preferences.getString(Participant.PASSWORD, "");
                i.putExtra(TASK_CODE, TASK_BACKGROUND_RECEIVE);
                i.putExtra(Participant.LOGIN, login);
                i.putExtra(Participant.PASSWORD, password);
                context.startService(i);
            }
        }
    }
}
