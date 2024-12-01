package com.example.luckyevent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.fragments.EventSettingDetailsFragment;

import java.util.ArrayList;

public class EventSettingsListAdapter extends ArrayAdapter<EventSetting> {
    private ArrayList<EventSetting> eventSettingList;
    private FragmentActivity fragmentActivity; // Added reference to the FragmentActivity

    public EventSettingsListAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<EventSetting> eventSettingList) {
        super(context, 0, eventSettingList);
        this.fragmentActivity = fragmentActivity;
        this.eventSettingList = eventSettingList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }

        EventSetting eventSetting = eventSettingList.get(position);
        if (eventSetting != null) {
            TextView eventNameTextView = convertView.findViewById(R.id.event_title);
            eventNameTextView.setText(eventSetting.getEventName());

            TextView eventDateTextView = convertView.findViewById(R.id.event_date);
            eventDateTextView.setText(eventSetting.getEventDate());

            TextView eventDescTextView = convertView.findViewById(R.id.event_details);
            eventDescTextView.setText(eventSetting.getEventDesc());

            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventSetting.getEventId());

            LinearLayout eventSettingLayout = convertView.findViewById(R.id.event_setting_layout);
            eventSettingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventSettingDetailsFragment eventSettingDetailsFragment = new EventSettingDetailsFragment();
                    eventSettingDetailsFragment.setArguments(bundle);

                    FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.OrganizerMenuFragment, eventSettingDetailsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        return convertView;
    }
}
