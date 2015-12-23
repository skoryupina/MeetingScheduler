package com.github.skoryupina.meetingscheduler;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new FragmentSettings()).commit();
        }
    }
}
