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

public class CustomListAdapter extends ArrayAdapter<Entrant> {
    private List<Entrant> entrants;
    private Context context;

    public CustomListAdapter(@NonNull Context context, List<Entrant> entrants) {
        super(context, 0, entrants);
        this.entrants = entrants;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_content, parent, false);
        }

        Entrant entrant = entrants.get(position);

        TextView entrantName = view.findViewById(R.id.text_name);
        String entrantFullName = entrant.getFirstName() + " " + entrant.getLastName();

        entrantName.setText(entrantFullName);

        return view;
    }
}
