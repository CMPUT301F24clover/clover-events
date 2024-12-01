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

/**
 * A class implementing the list adapter for a list of events.
 *
 * @author Mmelve, Tola
 * @see Event
 * @version 1
 * @since 1
 */
public class EventListAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventListAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_event, parent, false);
        }

        Event event = events.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        TextView textViewDateTime = view.findViewById(R.id.text_dateTime);
        TextView textViewDescription = view.findViewById(R.id.text_description);

        textViewTitle.setText(event.getName());
        textViewDateTime.setText(event.getDateTime());
        textViewDescription.setText(event.getDesc());

        return view;
    }
}