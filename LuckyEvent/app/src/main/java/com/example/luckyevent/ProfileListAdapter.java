package com.example.luckyevent;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ProfileListAdapter extends ArrayAdapter<Profile> {
    private List<Profile> originalList;
    private List<Profile> displayList;
    private List<Profile> firstNameFilteredList;
    private List<Profile> lastNameFilteredList;

    public ProfileListAdapter(Context context, List<Profile> Profiles) {
        super(context, 0, Profiles);
        this.originalList = Profiles;
        this.displayList = new ArrayList<>(Profiles);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
        }

        displayList.addAll(originalList);
        Profile profile = displayList.get(position);

        TextView usernameTextView = convertView.findViewById(R.id.userName_textView);
        usernameTextView.setText(profile.getUserName());

        TextView fullnameTextView = convertView.findViewById(R.id.fullName_textView);
        fullnameTextView.setText(profile.getFullName());



        return convertView;
    }

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Profile getItem(int position) {
        return originalList.get(position);
    }

    public void filterByUserName(String query){
        displayList.clear();
        if (query.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            for (Profile profile : originalList) {
                if (profile.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    displayList.add(profile);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByFirstName(String query) {
            for (Profile profile : originalList) {
                if (profile.getFirstName().toLowerCase().contains(query)) {
                    firstNameFilteredList.add(profile);
                }
            }
        notifyDataSetChanged();
    }

    public void filterByLastName(String query) {
        for (Profile profile : originalList) {
            if (profile.getLastName().toLowerCase().contains(query)) {
                lastNameFilteredList.add(profile);
            }
        }
        notifyDataSetChanged();
    }

}
