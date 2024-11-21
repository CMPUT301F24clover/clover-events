package com.example.luckyevent.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

public class DisplayQrActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_screen);

        String eventName = getIntent().getStringExtra("eventName");
        String eventDate = getIntent().getStringExtra("eventDate");
        String eventDescription = getIntent().getStringExtra("eventDescription");

        TextView eventNameTextView = findViewById(R.id.textView_eventTitle);
        TextView eventDateTextView = findViewById(R.id.textView_dateTime);
        TextView eventDescriptionTextView = findViewById(R.id.textView_description);

        eventNameTextView.setText(eventName != null ? eventName : "Event Name Not Available");
        eventDateTextView.setText(eventDate != null ? eventDate : "Date Not Available");
        eventDescriptionTextView.setText(eventDescription != null ? eventDescription : "Description Not Available");

        eventNameTextView.setText(eventName);
        eventDateTextView.setText(eventDate);
        eventDescriptionTextView.setText(eventDescription);
    }
}
