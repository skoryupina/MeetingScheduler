package com.github.skoryupina.meetingscheduler.receiver.listeners;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.github.skoryupina.meetingscheduler.MainActivity;
import com.github.skoryupina.meetingscheduler.R;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;


public class ListenerForService {
    protected static final String TAG = "MeetingClientService";
    public static ResultReceiver mReceiver = null;
    public static Context mContext = null;
    public static final int RETRY_TIMEOUT = 3000;
    public static int mTaskCode = -1;
    protected static final String RESULT_OK = "[{\"result\":\"OK\"}]";
    protected static final String RESULT_ERROR = "[{\"result\":\"Error\"}]";
    protected static final String EMPTY_LIST = "[]";
    private static String meetingsFileName = "meetings.json";
    public static MeetingRestClientService mRestClientService = null;

    public void prepareResponse(Bundle bundle, String extraText, int TASK_TYPE, int RESULT_CODE, ResultReceiver resultReceiver) {
        bundle.putString(Intent.EXTRA_TEXT, extraText);
        bundle.putInt(MeetingRestClientService.TASK_CODE, TASK_TYPE);
        resultReceiver.send(RESULT_CODE, bundle);
    }

    protected void writeJsonObject(JSONArray array) {
        FileOutputStream outputStream = null;
        if (array != null) {
            try {
                outputStream = mRestClientService.openFileOutput(meetingsFileName, Context.MODE_PRIVATE);
                outputStream.write(array.toString().getBytes());
            } catch (IOException e) {
                Log.e(TAG, "writeJsonObject " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "writeJsonObject " + e.getMessage());
                    }
                }
            }
        }
    }

    protected JSONArray readJsonObject() {
        JSONArray array = null;
        String path = mRestClientService.getFilesDir().getAbsolutePath() + "/" + meetingsFileName;
        File file = new File(path);
        FileInputStream stream = null;
        if (file.exists()) {
            try {
                stream = new FileInputStream(file);
                String jsonStr = null;

                FileChannel channel = stream.getChannel();
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                jsonStr = Charset.defaultCharset().decode(buffer).toString();
                Log.d(TAG, "readJsonObject " + jsonStr);
                if (jsonStr != null) {
                    array = new JSONArray(jsonStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "readJsonObject " + e.getMessage());
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "readJsonObject " + e.getMessage());
                }
                return array;
            }

        } else {
            File jFile = new File(mRestClientService.getFilesDir(), meetingsFileName);
            return new JSONArray();
        }
    }

    protected boolean checkApp() {
        ActivityManager am = (ActivityManager) mRestClientService.getSystemService(Context.ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase(mContext.getString(R.string.app_full_name))) {
            return true;
        } else {
            return false;
        }
    }

    public static void showNotification(){
        Context context = ListenerForService.mContext;
        NotificationManager manager = (NotificationManager)ListenerForService.mContext.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MainActivity.class);
        Notification notification = new Notification(R.mipmap.ic_launcher, context.getString(R.string.notif_new_meetings_received), System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra(context.getString(R.string.started_from_notif), true);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.notif_new_meetings_received), pendingNotificationIntent);
        manager.notify(0, notification);
    }

}
