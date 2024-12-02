package com.example.luckyevent.entrant.displayProfileScreen;

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
import com.example.luckyevent.entrant.MenuActivity;
import com.example.luckyevent.entrant.UserSession;

/**
 * @author Amna
 * This is for registering a users profile
 */
public class RegisterProfileActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button registerButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private ImageButton backButton;

    /**
     * here we get the users input for the name, email, and phone fields and call the registerProfile function from the profile controller to add it to the db
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.RegisterButton);
        backButton = findViewById(R.id.imageButton);

        // Initialize the profile setup and profile controller
        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);


        // set a listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneEditText.getText().toString();

                // make sure the name and email fields arent empty (mandatory)
                if (name.isEmpty() || email.isEmpty()){
                    Toast.makeText(RegisterProfileActivity.this,"Name and email fields are required",Toast.LENGTH_SHORT).show();
                    return;
                }

                // ensure they user provides exactly 2 names, first and last both.
                if (name.split(" ").length < 2 || name.split(" ").length > 2){
                    Toast.makeText(RegisterProfileActivity.this,"Please enter both first and last name (omit middle names)",Toast.LENGTH_SHORT).show();
                    return;
                }

                // since the user enters the first and last name together we split the string after the array and create string variables for first and last name
                String[] firstLastName = name.split(" ");
                String firstName = firstLastName[0];
                String lastName = firstLastName[1];

                // if no phone number is provided enter it into the db as an empty string
                if(phoneNumber.isEmpty()){
                    phoneNumber = "";
                }

                //call registerProfile from the profile controller to add the users input into the db
                profileController.registerProfile(firstName,lastName,email,phoneNumber,documentID ->{
                    Toast.makeText(RegisterProfileActivity.this,"Profile Registered successfully.",Toast.LENGTH_SHORT).show();

                    //in case theres errors going from this activity to the viewProfileActivity use toast messages to prevent the app from crashing
                    try {
                        UserSession.getInstance().setLastName(lastName);
                        Intent intent = new Intent(RegisterProfileActivity.this, ViewProfileActivity.class);
                        intent.putExtra("profileID", documentID);
                        startActivity(intent);
                    } catch (Exception e){
                        Toast.makeText(RegisterProfileActivity.this, "Error navigating to View Profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // set listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });


    }
}
