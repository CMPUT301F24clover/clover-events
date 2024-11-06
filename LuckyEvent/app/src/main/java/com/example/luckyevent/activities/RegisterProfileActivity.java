package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

public class RegisterProfileActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button registerButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private ImageButton backButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.RegisterButton);
        backButton = findViewById(R.id.imageButton);

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty()){
                    Toast.makeText(RegisterProfileActivity.this,"Name and email fields are required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.split(" ").length < 2 || name.split(" ").length > 2){
                    Toast.makeText(RegisterProfileActivity.this,"Please enter both first and last name (omit middle names)",Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] firstLastName = name.split(" ");
                String firstName = firstLastName[0];
                String lastName = firstLastName[1];

                if(phoneNumber.isEmpty()){
                    phoneNumber = "";
                }



                profileController.registerProfile(firstName,lastName,email,phoneNumber,documentID ->{
                    Toast.makeText(RegisterProfileActivity.this,"Profile Registered successfully.",Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent(RegisterProfileActivity.this, ViewProfileActivity.class);
                        intent.putExtra("profileID", documentID);
                        startActivity(intent);
                    } catch (Exception e){
                        Toast.makeText(RegisterProfileActivity.this, "Error navigating to View Profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });


    }
}
