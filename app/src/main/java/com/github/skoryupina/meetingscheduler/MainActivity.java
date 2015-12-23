package com.github.skoryupina.meetingscheduler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.support.v4.widget.SwipeRefreshLayout;

import com.github.skoryupina.Meeting;
import com.github.skoryupina.Participant;
import com.github.skoryupina.meetingscheduler.receiver.BackgroundReceiver;
import com.github.skoryupina.meetingscheduler.receiver.DownloadReceiver;
import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;


public class MainActivity extends ActionBarActivity implements DownloadReceiver.Receiver, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MainActivity";
    private SharedPreferences preferences;
    private String login;
    private String password;
    String mFIO;
    String mDescription;
    String mPost;
    String mMeetingName;
    String mStartDate;
    String mEndDate;
    String mPriority;
    ListView mListView;
    DownloadReceiver mReceiver;
    JSONArray array = null;
    ArrayList<MeetingItem> mAcceptedMeetingsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /***
     * Date and time pickers staff
     */
    int mYear = 2015;
    int mMonth = 10;
    int mDay = 25;
    int mHour = 14;
    int mMinute = 35;
    TextView mTVStartDate;
    TextView mTVEndDate;
    TextView mTVStartTime;
    TextView mTVEndTime;

    /***
     * Communication staff
     */
    Intent i;
    private PendingIntent mPendingIntent;
    private BroadcastReceiver mBackgrounReceiver;

    /***
     * Alarm interval settings
     */
    private static final int ALARM_INTERVAL = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /***
         * ActionBar settings
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        mListView = (ListView) findViewById(R.id.meetingList);
        registerForContextMenu(mListView);

        /***
         * Intent settings (communication with BackgroundReceiver)
         */
        Intent receiverIntent = new Intent(this, BackgroundReceiver.class);
        /***
         * method getBroadcast defines type of object to be called with intent
         */
        mPendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);
        /***
         * Handing prepared PendingIntent to AlarmManager.
         * It will be working according to given scheduler (timetable).
         */
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); //retrieve alarm manager
        /***
         * AlarmManager.RTC - Alarm time in System.currentTimeMillis()
         * System.currentTimeMillis() - get current time for trigger
         *
         */

        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ALARM_INTERVAL, mPendingIntent);
        /***
         * swipe settings
         */
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getOverflowMenu();

        /***
         * setting network manager
         */
        NetworkManager.mContext = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(getString(R.string.intent_name));
        mBackgrounReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra(getString(R.string.intent_msg_tag));
                if (msg.equals(getString(R.string.intent_received_msg))) {
                    array = JsonWorkHelper.readJsonObject();
                    fillListView();
                } else {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        };
        /***
         * receiver registration
         */
        this.registerReceiver(mBackgrounReceiver, intentFilter);
        /***
         * checking presence of login and password in preferences file
         */
        preferences = getSharedPreferences(getString(R.string.shared_preferenced_name), Context.MODE_PRIVATE);
        if (preferences != null) {
            if (preferences.contains(Participant.LOGIN) && preferences.contains(Participant.PASSWORD)) {
                login = preferences.getString(Participant.LOGIN, "");
                password = preferences.getString(Participant.PASSWORD, "");
                if (getIntent().getBooleanExtra(getString(R.string.started_from_notif), false)) {
                    array = JsonWorkHelper.readJsonObject();
                    fillListView();
                } else {
                    if (!NetworkManager.internetConnected())
                        Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    else {
                        startSendService(MeetingRestClientService.TASK_REQUEST_MEETINGS);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.auth_request, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mBackgrounReceiver);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        int taskCode = resultData.getInt(MeetingRestClientService.TASK_CODE);
        switch (taskCode) {
            case MeetingRestClientService.TASK_REQUEST_MEETINGS: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK:
                        try {
                            setProgressBarIndeterminateVisibility(false);
                            String result = resultData.getString(getString(R.string.result));
                            array = new JSONArray(result);
                            fillListView();
                        } catch (JSONException e) {
                            Log.e(TAG, "onReceiveResult " + e.getMessage());
                        }
                        break;
                    case MeetingRestClientService.RESULT_ERROR:
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
            break;
            case MeetingRestClientService.TASK_DELETE_MEETING: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK: {
                        Toast.makeText(this, R.string.delete_message, Toast.LENGTH_SHORT).show();
                        startSendService(MeetingRestClientService.TASK_REQUEST_MEETINGS);
                    }
                    break;
                    case MeetingRestClientService.RESULT_ERROR: {
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
            break;
            case MeetingRestClientService.TASK_GET_DETAIS: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK: {
                        String result = resultData.getString(getString(R.string.result));
                        result = JsonWorkHelper.parseDescription(result);
                        showTextDialog(true, result);
                    }
                    break;
                    case MeetingRestClientService.RESULT_ERROR: {
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            break;
            case MeetingRestClientService.TASK_ADD_PARTICIPANT: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK: {
                        Toast.makeText(this, R.string.participant_add_message, Toast.LENGTH_SHORT).show();
                        startSendService(MeetingRestClientService.TASK_REQUEST_MEETINGS);
                    }
                    break;
                    case MeetingRestClientService.RESULT_ERROR: {
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
            break;
            case MeetingRestClientService.TASK_FIND_MEETING_BY_DESCRIPTION: {
                mSwipeRefreshLayout.setRefreshing(false);
                setProgressBarIndeterminateVisibility(false);
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK:
                        try {
                            String result = resultData.getString(getString(R.string.result));
                            array = new JSONArray(result);
                            fillListView();
                        } catch (JSONException e) {
                            Log.e(TAG, "onReceiveResult " + e.getMessage());
                        }
                        break;
                    case MeetingRestClientService.RESULT_ERROR: {
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
            break;
            case MeetingRestClientService.TASK_ADD_MEETING: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK:
                        Toast.makeText(this, R.string.add_message, Toast.LENGTH_SHORT).show();
                        startSendService(MeetingRestClientService.TASK_REQUEST_MEETINGS);
                        break;
                    case MeetingRestClientService.RESULT_ERROR:
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
            break;
            case MeetingRestClientService.TASK_BACKGROUND_RECEIVE: {
                switch (resultCode) {
                    case MeetingRestClientService.RESULT_OK:
                        try {
                            setProgressBarIndeterminateVisibility(false); ////TODO understand
                            String result = resultData.getString(getString(R.string.result));
                            array = new JSONArray(result);
                            fillListView();
                        } catch (JSONException e) {
                            Log.e(TAG, "onReceiveResult " + e.getMessage());
                        }
                        break;
                    case MeetingRestClientService.RESULT_ERROR:
                        String errorMessage = resultData.getString(Intent.EXTRA_TEXT);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                Intent intent;
                Activity currentActivity = this;
                intent = new Intent(currentActivity, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                currentActivity.startActivity(intent);
            }
            break;
            case R.id.action_about: {
                showTextDialog(false, null);
            }
            break;
            case R.id.action_exit: {
                showAlert();
            }
            break;
            case R.id.action_search: {
                enterDescription();
            }
            break;
            case R.id.action_add: {
                showMeetingDialog();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillListView() {

        if (array != null) {
            try {
                mListView.setAdapter(null);
                mListView = (ListView) findViewById(R.id.meetingList);
                mListView.setAdapter(null);
                mAcceptedMeetingsList = new ArrayList<MeetingItem>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    mAcceptedMeetingsList.add(new MeetingItem(
                            item.getInt(Meeting.ID),
                            item.getString(Meeting.NAME),
                            item.getString(Meeting.STARTDATE),
                            item.getString(Meeting.ENDDATE),
                            item.getString(Meeting.PRIORITY)));
                }
                mListView.setAdapter(new MeetingListAdapter(this, R.layout.list_item, mAcceptedMeetingsList));

            } catch (JSONException e) {
                Log.e(TAG, "fillListView " + e.getMessage());
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchMeetings();
    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchMeetings() {
        // showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        if (!NetworkManager.internetConnected())
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        else {
            startSendService(MeetingRestClientService.TASK_REQUEST_MEETINGS);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showAlert() {

        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void enterDescription() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.desc_title)
                .setMessage(R.string.input_descrip);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        dialog.setView(input);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDescription = input.getText().toString().trim();
                if (!mDescription.equals("")) {
                    if (!NetworkManager.internetConnected())
                        Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    else {
                        startSendService(MeetingRestClientService.TASK_FIND_MEETING_BY_DESCRIPTION);
                    }

                }
            }

        })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showTextDialog(boolean isDecription, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                });
        if (isDecription) {
            dialog.setTitle(R.string.description)
                    .setMessage(message);
        } else {
            dialog.setTitle(R.string.about)
                    .setMessage(R.string.about_toast);
        }
        dialog.show();
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSendService(int code) {
        mReceiver = new DownloadReceiver(new Handler());
        mReceiver.setReceiver(this);
        i = new Intent(this, MeetingRestClientService.class);
        switch (code) {
            case MeetingRestClientService.TASK_DELETE_MEETING:
            case MeetingRestClientService.TASK_GET_DETAIS: {
                i.putExtra(Meeting.ID, SwipeDetector.swipeID);
            }
            break;
            case MeetingRestClientService.TASK_ADD_PARTICIPANT: {
                i.putExtra(Meeting.ID, SwipeDetector.swipeID);
                i.putExtra(Participant.FIO, mFIO);
                i.putExtra(Participant.POSITION, mPost);
            }
            break;
            case MeetingRestClientService.TASK_FIND_MEETING_BY_DESCRIPTION: {
                setProgressBarIndeterminateVisibility(true);
                i.putExtra(Meeting.DESCRIPTION, mDescription);
            }
            break;
            case MeetingRestClientService.TASK_ADD_MEETING: {
                i.putExtra(Meeting.DESCRIPTION, mDescription);
                i.putExtra(Meeting.NAME, mMeetingName);
                i.putExtra(Meeting.STARTDATE, mStartDate);
                i.putExtra(Meeting.ENDDATE, mEndDate);
                i.putExtra(Meeting.PRIORITY, mPriority);
            }
            break;
        }
        i.putExtra(Participant.LOGIN, login);
        i.putExtra(Participant.PASSWORD, password);
        i.putExtra(MeetingRestClientService.TASK_CODE, code);
        i.putExtra(MeetingRestClientService.RECEIVER, mReceiver);
        this.startService(i);
    }


    public void showParticipantDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View participantDialogView = factory.inflate(
                R.layout.dialog_participant, null);
        final AlertDialog.Builder partDialog = new AlertDialog.Builder(this);
        partDialog.setView(participantDialogView)
                .setTitle(R.string.participantTitle)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText fn = (EditText) participantDialogView.findViewById(R.id.nameText);
                        EditText pos = (EditText) participantDialogView.findViewById(R.id.postText);
                        mFIO = fn.getText().toString().trim();
                        mPost = pos.getText().toString().trim();
                        if (NetworkManager.internetConnected())
                            startSendService(MeetingRestClientService.TASK_ADD_PARTICIPANT);
                        else
                            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                    }
                });

        partDialog.show();
    }

    private void showMeetingDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View meetingDialogView = factory.inflate(
                R.layout.new_meeting_dialog, null);
        final AlertDialog.Builder newMeetingDialog = new AlertDialog.Builder(this);
        mTVStartDate = (TextView) meetingDialogView.findViewById(R.id.startDatePickerText);
        mTVEndDate = (TextView) meetingDialogView.findViewById(R.id.endDatePickerText);
        mTVStartTime = (TextView) meetingDialogView.findViewById(R.id.beginTimeText);
        mTVEndTime = (TextView) meetingDialogView.findViewById(R.id.endTimeText);
        mTVStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(myCallBack);
            }
        });
        mTVEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(myCallBackEnd);
            }
        });
        mTVStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(myTimeBeginCall);
            }
        });
        mTVEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(myTimeEndCall);
            }
        });
        newMeetingDialog.setView(meetingDialogView)
                .setTitle(R.string.new_meeting)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText mn = (EditText) meetingDialogView.findViewById(R.id.etMeetingName);
                        EditText dn = (EditText) meetingDialogView.findViewById(R.id.etDescription);
                        RadioGroup rad = (RadioGroup) meetingDialogView.findViewById(R.id.radioPriority);
                        RadioButton check = (RadioButton) meetingDialogView.findViewById(rad.getCheckedRadioButtonId());
                        TextView etStartDate = (TextView) meetingDialogView.findViewById(R.id.startDatePickerText);
                        TextView etStartTime = (TextView) meetingDialogView.findViewById(R.id.beginTimeText);
                        TextView etEndDate = (TextView) meetingDialogView.findViewById(R.id.endDatePickerText);
                        TextView etEndTime = (TextView) meetingDialogView.findViewById(R.id.endTimeText);

                        mMeetingName = mn.getText().toString().trim();
                        mDescription = dn.getText().toString().trim();
                        mStartDate = etStartDate.getText().toString().trim() + " " + etStartTime.getText().toString().trim();
                        mEndDate = etEndDate.getText().toString().trim() + " " + etEndTime.getText().toString().trim();
                        mPriority = check.getText().toString().trim();
                        if (NetworkManager.internetConnected())
                            startSendService(MeetingRestClientService.TASK_ADD_MEETING);
                        else
                            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });

        newMeetingDialog.show();
    }

    private void showDataPicker(DatePickerDialog.OnDateSetListener myCallBack) {
        DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, mYear, mMonth, mDay);
        tpd.show();
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            MainActivity.this.mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mTVStartDate.setText(MainActivity.this.mYear + "-" + (mMonth + 1) + "-" + mDay);
        }
    };

    DatePickerDialog.OnDateSetListener myCallBackEnd = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            MainActivity.this.mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mTVEndDate.setText(MainActivity.this.mYear + "-" + (mMonth + 1) + "-" + mDay);
        }
    };

    private void showTimePicker(TimePickerDialog.OnTimeSetListener myCallBack) {
        TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, mHour, mMinute, true);
        tpd.show();
    }

    TimePickerDialog.OnTimeSetListener myTimeBeginCall = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            MainActivity.this.mMinute = minute;
            mTVStartTime.setText(mHour + ":" + MainActivity.this.mMinute);
        }
    };

    TimePickerDialog.OnTimeSetListener myTimeEndCall = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            MainActivity.this.mMinute = minute;
            mTVEndTime.setText(mHour + ":" + MainActivity.this.mMinute);
        }
    };





}
