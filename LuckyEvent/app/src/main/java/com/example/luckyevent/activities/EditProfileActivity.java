package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

/**
 * @author Amna
 * This implements editting/updating functionality. it allows users to edit their profile information like name, phone number and email
 */
public class EditProfileActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, phoneEdit;
    private Button saveButton;
    private ImageButton backButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private String documentID;
    private ImageView profile;
    private String imageUrl;
    private FloatingActionButton editProfilePictureButton;

    /**
     * here we get the users input and load up the profile information for the user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        profile = findViewById(R.id.imageView);
        imageUrl = UserSession.getInstance().getProfileUri();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profile);
        }

        nameEdit = findViewById(R.id.nameEditText);
        emailEdit = findViewById(R.id.emailEditText);
        phoneEdit = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.imageButton);
        editProfilePictureButton = findViewById(R.id.editProfilePictureButton);
        // get the document id to load to users profile information
        documentID = getIntent().getStringExtra("profileID");

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);

        // load the profile information by using the loadProfile from the profileController
        profileController.loadProfile(documentID, new OnSuccessListener<ProfileSetup>() {
            @Override
            public void onSuccess(ProfileSetup profile) {
                if (profile != null) {
                    // add the profile information to the fields
                    nameEdit.setText(profile.getName());
                    emailEdit.setText(profile.getEmail());
                    phoneEdit.setText(profile.getPhoneNumber());
                    Toast.makeText(EditProfileActivity.this,"Profile loaded successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfileActivity.this,"Unable to load profile",Toast.LENGTH_SHORT).show();
                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileEdit(); // when they click save we use the editProfile from the profileController to update the information
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when they click the back button it brings the user back to the profile view page
                Intent intent = new Intent(EditProfileActivity.this,ViewProfileActivity.class);
                startActivity(intent);
            }
        });

        editProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, EditProfilePictureActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);
            }
        });

    }

    /**
     * ensures all the input from the user is valid then updates the information in the db
     */
    private void saveProfileEdit() {
        String name = nameEdit.getText().toString();
        String[] fullName = name.split(" "); // split the name into first and last
        String email = emailEdit.getText().toString();
        if (fullName.length != 2) { //give warning to make sure only first and last name was entered
            Toast.makeText(EditProfileActivity.this, "Please only enter first and last name", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
        } else{
            String firstName = fullName[0];
            String lastName = fullName[1];
            String phoneNumber = phoneEdit.getText().toString();
            //call the profile controller to update to profile information using editProfile
            profileController.editProfile(documentID, firstName, lastName, email, phoneNumber);
            Toast.makeText(EditProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();

            // once the profile is updated bring the user back to the view profile page to display the new information
            Intent intent = new Intent(EditProfileActivity.this,ViewProfileActivity.class);
            intent.putExtra("profileID",documentID);
            startActivity(intent);
        }

    }

}
