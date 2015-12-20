package com.github.skoryupina.meetingscheduler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skoryupina.meetingscheduler.receiver.MeetingRestClientService;
import java.util.ArrayList;

import static com.github.skoryupina.Meeting.*;

public class MeetingListAdapter extends ArrayAdapter<MeetingItem> {
    private static final String TAG = "MeetingListAdapter";
    public static MainActivity mContext;
    private ArrayList<MeetingItem> meetingItems;
    private MeetingViewHolder meetingHolder;
    SwipeDetector swipeDetector;

    public class MeetingViewHolder {
        TextView meetingName;
        TextView startDate;
        TextView endDate;
        ImageView priorityIcon;
        //swipe delete layouts
        RelativeLayout listItem;
        LinearLayout mainView;
    }

    public MeetingListAdapter(Context context, int layoutResource, ArrayList<MeetingItem> items) {
        super(context,layoutResource,items);
        this.meetingItems = items;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item, null);
            meetingHolder = new MeetingViewHolder();
            //main layouts
            meetingHolder.mainView = (LinearLayout) view.findViewById(R.id.mainview);
            meetingHolder.listItem = (RelativeLayout) view.findViewById(R.id.listitem);

            //meeting values
            meetingHolder.meetingName = (TextView) view.findViewById(R.id.meetingName);
            meetingHolder.startDate = (TextView) view.findViewById(R.id.startDate);
            meetingHolder.endDate = (TextView) view.findViewById(R.id.endDate);
            meetingHolder.priorityIcon = (ImageView) view.findViewById(R.id.imagePriority);
            view.setTag(meetingHolder);
        } else meetingHolder = (MeetingViewHolder) view.getTag();

        MeetingItem meetingItem = meetingItems.get(pos);

        if (meetingItem != null) {
            meetingHolder.meetingName.setText(meetingItem.getMeetingName());
            meetingHolder.startDate.setText(meetingItem.getStartDate());
            meetingHolder.endDate.setText(meetingItem.getEndDate());

            String a = meetingItem.getPriority();
            if (a.equals(Priority.URGENT.toString())) {
                meetingHolder.priorityIcon.setImageResource(R.drawable.ic_urgent);
            } else if (a.equals(Priority.PLANNED.toString())) {
                meetingHolder.priorityIcon.setImageResource(R.drawable.ic_planned);
            } else if (a.equals(Priority.OPTIONAL.toString())) {
                meetingHolder.priorityIcon.setImageResource(R.drawable.ic_possible);
            }
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) meetingHolder.mainView.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        meetingHolder.mainView.setLayoutParams(params);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!swipeDetector.isNone()) {
                    Log.d(TAG, "onLongClick swipe ? isNone=" + swipeDetector.isNone());
                    return true;
                } else {
                    Log.d(TAG, "onLongClick notSwipe?  isNone=" + swipeDetector.isNone());
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.menu_context);
                    popupMenu
                            .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.add: {
                                            MeetingItem ti = getItem(pos);
                                            SwipeDetector.swipeID = ti.getId();
                                            mContext.showParticipantDialog();
                                            return true;
                                        }
                                        case R.id.info: {
                                            MeetingItem ti = getItem(pos);
                                            SwipeDetector.swipeID = ti.getId();
                                            if (NetworkManager.internetConnected())
                                                mContext.startSendService(MeetingRestClientService.TASK_GET_DETAIS);
                                            else
                                                Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    }
                                    return true;
                                }
                            });

                    popupMenu.show();
                    return true;
                }
            }
        });
        swipeDetector = new SwipeDetector(meetingHolder, pos);
        view.setOnTouchListener(swipeDetector);
        return view;
    }
}