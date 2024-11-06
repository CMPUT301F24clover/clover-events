package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.Entrant;
import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EntrantJoinWaitlistActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton joinButton;
    private FirebaseFirestore db;
    private String eventID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_screen);
        db = FirebaseFirestore.getInstance();
        joinButton = findViewById(R.id.button_join);
        eventID = getIntent().getStringExtra("eventID");

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToFirestore();
            }
        });
    }
    private void addUserToFirestore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        db.collection("events")
                .document(eventID)
                .update("waitingList", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EntrantJoinWaitlistActivity.this,"You have joined the waitlist",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EntrantJoinWaitlistActivity.this,"Failed to join the waitlist",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
