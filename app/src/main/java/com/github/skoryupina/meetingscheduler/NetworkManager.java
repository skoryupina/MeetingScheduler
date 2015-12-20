package com.github.skoryupina.meetingscheduler;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Ekaterina on 20.12.2015.
 */
public class NetworkManager {
    public static Context mContext;
    public static boolean internetConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
