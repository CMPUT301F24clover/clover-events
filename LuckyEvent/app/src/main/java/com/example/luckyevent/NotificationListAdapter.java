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
 * A class implementing the list adapter for a user's list of notifications.
 *
 * @author Mmelve
 * @see Notification
 * @version 1
 * @since 1
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifs;
    private Context context;

    public NotificationListAdapter(@NonNull Context context, ArrayList<Notification> notifs) {
        super(context, 0, notifs);
        this.context = context;
        this.notifs = notifs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_notification, parent, false);
        }

        Notification notif = notifs.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        textViewTitle.setText(notif.getTitle());
        TextView textViewContent = view.findViewById(R.id.text_content);
        textViewContent.setText(notif.getContent());

        return view;
    }
}
