package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, phoneEdit;
    private Button saveButton;
    private ImageButton backButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private String documentID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);


        nameEdit = findViewById(R.id.nameEditText);
        emailEdit = findViewById(R.id.emailEditText);
        phoneEdit = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.imageButton);
        documentID = getIntent().getStringExtra("profileID");

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);

        profileController.loadProfile(documentID, new OnSuccessListener<ProfileSetup>() {
            @Override
            public void onSuccess(ProfileSetup profile) {
                if (profile != null) {
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
                saveProfileEdit();

                Intent intent = new Intent(EditProfileActivity.this,ViewProfileActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this,ViewProfileActivity.class);
                startActivity(intent);
            }
        });
    }
    private void saveProfileEdit() {
        String name = nameEdit.getText().toString();
        String[] fullName = name.split(" ");
        String firstName = fullName[0];
        String lastName = fullName[1];
        String email = emailEdit.getText().toString();
        String phoneNumber = phoneEdit.getText().toString();
        profileController.editProfile(documentID,firstName,lastName,email,phoneNumber);
        Toast.makeText(EditProfileActivity.this,"Profile saved successfully",Toast.LENGTH_SHORT).show();

    }


}
