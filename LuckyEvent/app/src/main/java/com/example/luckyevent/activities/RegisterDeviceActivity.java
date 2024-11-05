package com.example.luckyevent.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterDeviceActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton registerButton;
    private FirebaseDB firebaseDB;
    private ImageView gobackButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_template_device_registration);
        registerButton = findViewById(R.id.RegisterButton);
        gobackButton = findViewById(R.id.previousIcon);
        firebaseDB = new FirebaseDB(this);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDB.deviceSignIn(new FirebaseDB.SignInCallback(){
                    @Override
                    public void onSuccess() {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = firebaseUser.getUid();
                        UserSession.getInstance().setUserId(userId);
                        UserSession.getInstance().setFirstName(null);

                        Intent intent = new Intent(RegisterDeviceActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish(); // Optional
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(RegisterDeviceActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterDeviceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });
    }
}
