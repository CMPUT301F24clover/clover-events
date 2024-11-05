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
 * A class implementing the list adapter for an event's list of entrants.
 *
 * @author Mmelve
 * @see Entrant
 * @version 2
 * @since 1
 */
public class EntrantListAdapter extends ArrayAdapter<Entrant> {
    private List<Entrant> entrants;
    private Context context;

    public EntrantListAdapter(@NonNull Context context, List<Entrant> entrants) {
        super(context, 0, entrants);
        this.entrants = entrants;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content, parent, false);
        }

        Entrant entrant = entrants.get(position);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        textViewTitle.setVisibility(View.GONE);
        TextView textViewContent = view.findViewById(R.id.text_content);
        String entrantFullName = entrant.getFirstName() + " " + entrant.getLastName();
        textViewContent.setText(entrantFullName);

        return view;
    }
}
