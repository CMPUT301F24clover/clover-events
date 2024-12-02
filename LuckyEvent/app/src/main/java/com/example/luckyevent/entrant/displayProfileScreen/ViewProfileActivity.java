package com.example.luckyevent.entrant.displayProfileScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.example.luckyevent.shared.LoginActivity;
import com.example.luckyevent.entrant.MenuActivity;
import com.example.luckyevent.entrant.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the view profile functionality. displays the users information, such as name, email, and phone number.
 * Allows users to logout, go back to the home page, or edit their information
 */
public class ViewProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText;
    private Button editButton;
    private ImageButton backButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private Button logOutButton;
    private ImageView profile;
    private String imageUrl;
    private CheckBox enableNotificationsCheckBox;
    private FirebaseFirestore db;
    private String TAG  = "ViewProfileActivity";

    /**
     * Loads the profile information, implements logout, edit and back button functionality
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        db = FirebaseFirestore.getInstance();

        profile = findViewById(R.id.imageView);
        imageUrl = UserSession.getInstance().getProfileUri();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profile);
        }

        enableNotificationsCheckBox = findViewById(R.id.checkBox);
        nameText = findViewById(R.id.nameField);
        emailText = findViewById(R.id.emailField);
        phoneText = findViewById(R.id.phoneField);
        editButton = findViewById(R.id.editButton);
        backButton = findViewById(R.id.imageButton);
        logOutButton = findViewById(R.id.button3);

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);

        // get the document if to load the profile information
        String documentID = getIntent().getStringExtra("profileID");

        profileController.loadProfile(documentID, profile ->{
            if (profile != null) {
                // add the information to the textViews
                nameText.setText(profile.getName());
                emailText.setText(profile.getEmail());
                phoneText.setText(profile.getPhoneNumber());
                Toast.makeText(ViewProfileActivity.this,"Profile loaded",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(ViewProfileActivity.this,"Unable to load profile",Toast.LENGTH_SHORT).show();
            }
        });

        // set listener for the edit button that brings users from view profile to the edit page
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);
            }
        });
        // set listener for the back button that brings users from view profile to the home/menu page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        // set listener for the logout button that brings users from view profile to the login/sign up page
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Boolean isNotificationDisabled = UserSession.getInstance().isNotificationDisabled();
        enableNotificationsCheckBox.setChecked(!Boolean.TRUE.equals(isNotificationDisabled));


        // When clicked disable or enable the notification for the current user
        // The initial state of this is stored in firestore
        enableNotificationsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentState = Boolean.TRUE.equals(UserSession.getInstance().isNotificationDisabled());
                Map<String, Object> map = new HashMap<>();
                map.put("notificationsDisabled", !currentState);

                db.collection("loginProfile")
                        .document(UserSession.getInstance().getUserId())
                        .update(map)
                        .addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                enableNotificationsCheckBox.setChecked(currentState);
                                UserSession.getInstance().setNotificationDisabled(!currentState);
                            } else {
                                // Revert checkbox state on failure
                                enableNotificationsCheckBox.setChecked(!currentState);
                                Log.e(TAG, "onClick: Failed to update notifications state");
                            }
                        });
            }
        });



    }
}
