package com.example.luckyevent.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.Notification;
import com.example.luckyevent.NotificationListAdapter;
import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Displays a message informing the user of the option to sign in via device id. Clicking the register device
 * button signs in the user using their device id.
 *
 * @author Seyi
 * @see FirebaseDB
 * @version 1
 * @since 1
 */
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

        /**
         * Registers the users device device when clicked. This is done with the help of the deviceSignIn
         * function from the FireBaseDB class. The class uses the device id to sign in if the id is in the database, otherwise
         * a new account is created. Successfully registering the device navigates to the next activity (MenuActivity)
         */
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

        /**
         * Goes back to the previous activity when clicked (RegisterDeviceActivity)
         */
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterDeviceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
