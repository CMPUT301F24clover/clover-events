package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePageActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_template_homepage);

        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            UserSession.getInstance().setUserId(userID);

            db.collection("loginProfile")
                    .document(userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists() && doc.contains("hasUserProfile")) {
                                Boolean hasUserProfile = doc.getBoolean("hasUserProfile");

                                if (hasUserProfile != null && hasUserProfile) {
                                    Intent intent = new Intent(HomePageActivity.this, ViewProfileActivity.class);
                                    intent.putExtra("profileID", userID);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(HomePageActivity.this,"No profile found, please register you profile",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(HomePageActivity.this, RegisterProfileActivity.class);
                                    intent.putExtra("profileID", userID);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(HomePageActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(HomePageActivity.this,"Unable to find user document",Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this,"No user currently logged in",Toast.LENGTH_SHORT).show();
        }

    }
}
