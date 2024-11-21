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

        switch (waitingList.getWaitingListStatus()) {
            case "Waitlisted": {
                Button acceptButton = view.findViewById(R.id.button_acceptInvitation);
                acceptButton.setVisibility(View.GONE);
                Button declineButton = view.findViewById(R.id.button_declineInvitation);
                declineButton.setVisibility(View.GONE);
                TextView enrolledText = view.findViewById(R.id.text_enrolled);
                enrolledText.setVisibility(View.GONE);
                break;
            }
            case "Chosen": {
                Button leaveButton = view.findViewById(R.id.button_leaveWaitingList);
                leaveButton.setVisibility(View.GONE);
                TextView enrolledText = view.findViewById(R.id.text_enrolled);
                enrolledText.setVisibility(View.GONE);
                break;
            }
            case "Enrolled": {
                Button leaveButton = view.findViewById(R.id.button_leaveWaitingList);
                leaveButton.setVisibility(View.GONE);
                Button acceptButton = view.findViewById(R.id.button_acceptInvitation);
                acceptButton.setVisibility(View.GONE);
                Button declineButton = view.findViewById(R.id.button_declineInvitation);
                declineButton.setVisibility(View.GONE);
                break;
            }
        }

        return view;
    }
}
