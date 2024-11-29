package com.example.luckyevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<EventListAdapter.EventItem> {
    private ArrayList<EventItem> eventItems;
    private Context context;

    public EventListAdapter(@NonNull Context context, ArrayList<EventItem> eventItems) {
        super(context, 0, eventItems);
        this.eventItems = eventItems;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_organizereventlist, parent, false);
        }

        EventItem eventItem = eventItems.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        TextView textViewDate = view.findViewById(R.id.text_date);
        TextView textViewTime = view.findViewById(R.id.text_time);
        TextView textViewDescription = view.findViewById(R.id.text_description);

        textViewTitle.setText(eventItem.getEventName());
        textViewDate.setText(eventItem.getEventDate());
        textViewTime.setText(eventItem.getEventTime());
        textViewDescription.setText(eventItem.getEventDesc());

        return view;
    }

    public static class EventItem implements Comparable<EventItem> {
        private String eventId;
        private String eventName;
        private long createdAt;
        private String eventDate;
        private String eventTime;
        private String eventDesc;

        public EventItem(String eventId, String eventName, long createdAt, String eventDate,
                         String eventTime, String eventDesc) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.createdAt = createdAt;
            this.eventDate = eventDate;
            this.eventTime = eventTime;
            this.eventDesc = eventDesc;
        }

        public String getEventDesc() {
            return eventDesc;
        }

        public String getEventTime() {
            return eventTime;
        }

        public String getEventDate() {
            return eventDate;
        }

        public String getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        @Override
        public int compareTo(EventItem other) {
            // Sort in descending order (most recent first)
            return Long.compare(other.createdAt, this.createdAt);
        }
    }
}