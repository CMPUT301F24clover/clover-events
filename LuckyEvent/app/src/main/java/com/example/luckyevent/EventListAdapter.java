package com.example.luckyevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * A class implementing the list adapter for an organizer's list of events.
 *
 * @author Mmelve
 * @version 1
 * @since 1
 */
public class EventListAdapter extends ArrayAdapter<String> {
    private List<String> eventNames;
    private Context context;

    public EventListAdapter(@NonNull Context context, List<String> eventNames) {
        super(context, 0, eventNames);
        this.eventNames = eventNames;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content, parent, false);
        }

        String eventName = eventNames.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        textViewTitle.setText(eventName);
        TextView textViewContent = view.findViewById(R.id.text_content);
        textViewContent.setVisibility(View.GONE);

        return view;
    }
}
