package com.example.luckyevent.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.Entrant;
import com.example.luckyevent.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EntrantJoinWaitlistActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton joinButton;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_screen);
        db = FirebaseFirestore.getInstance();
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);
        username = findViewById(R.id.SignUpUsernameInput);
        joinButton = findViewById(R.id.button_join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToFirestore();
            }
        });
    }
    private void addUserToFirestore(){

        String FirstName = firstName.getText().toString().trim();
        String Lastname = lastName.getText().toString().trim();
        String Username = username.getText().toString().trim();

        Entrant entrant = new Entrant(FirstName,Lastname,Username);
        db.collection("EventWaitlist")
                .add(entrant)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(EntrantJoinWaitlistActivity.this, "You have successfully joined the Waitlist!" + documentReference.getId(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(EntrantJoinWaitlistActivity.this, "Failed to join the Waitlist!" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
