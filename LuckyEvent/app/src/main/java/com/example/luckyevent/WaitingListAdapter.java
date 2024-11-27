package com.example.luckyevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A class implementing the list adapter for a list of all the waiting lists a user has joined.
 *
 * @author Mmelve
 * @see WaitingList
 * @version 3
 * @since 1
 */
public class WaitingListAdapter extends ArrayAdapter<WaitingList> {
    private ArrayList<WaitingList> waitingLists;
    private Context context;

    public WaitingListAdapter(@NonNull Context context, ArrayList<WaitingList> waitingLists) {
        super(context, 0, waitingLists);
        this.waitingLists = waitingLists;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_waitinglist, parent, false);
        }

        WaitingList waitingList = waitingLists.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        textViewTitle.setText(waitingList.getEventName());

        TextView textViewDate = view.findViewById(R.id.text_dateTime);
        textViewDate.setText(waitingList.getEventDateTime());

        TextView textViewContent = view.findViewById(R.id.text_content);
        textViewContent.setText(waitingList.getEventDesc());

        return view;
    }
}
