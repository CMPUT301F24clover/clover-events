package com.example.luckyevent.organizer.loginSection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.entrant.MenuActivity;
import com.example.luckyevent.entrant.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText facilityName;
    private EditText facilityCode;
    private FirebaseDB firebaseDB;
    private ImageView goBackButton;
    private TextView goBackText;
    private FirebaseFirestore db;

    /**
     * Displays the fields needed to sign up as an organizer. This activity navigates to the MenuActivity if the user successfully
     * sign ups as an organizer
     *
     * @author Seyi
     * @see FirebaseDB
     * @see UserSession
     * @version 1
     * @since 1
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.organizer_signup_new);

        userName = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        firstName = findViewById(R.id.firstname_editText);
        lastName = findViewById(R.id.lastname_editText);
        facilityName = findViewById(R.id.facility_code_editText);
        facilityCode = findViewById(R.id.facility_code_editText);
        goBackButton = findViewById(R.id.previousIcon);
        goBackText = findViewById(R.id.textView7);


        firebaseDB = new FirebaseDB(this);

        /*
         * Takes the text from the fields provided and signs up the user as an organizer. The organizer sign up
         * functionality is handled by FirebaseDB's signup function. The user is redirected to the OrganizerMenuActivity
         * when successfully registered
         */
        signUpButton = findViewById(R.id.sign_in_button);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String userNameInput = userName.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
                String firstNameInput = firstName.getText().toString().trim();
                String lastNameInput = lastName.getText().toString().trim();
                String organizerNameInput = facilityName.getText().toString().trim();
                String facilityCodeInput = facilityCode.getText().toString().trim();


                if (userNameInput.isEmpty() || passwordInput.isEmpty()) {
                    if(userNameInput.isEmpty() && passwordInput.isEmpty()){
                        Toast.makeText(OrganizerSignUpActivity.this, "Username field and password field are required", Toast.LENGTH_SHORT).show();
                    }
                    else if(userNameInput.isEmpty()) {
                        Toast.makeText(OrganizerSignUpActivity.this, "Username field is required", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrganizerSignUpActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    firebaseDB.signUp(userNameInput, passwordInput, firstNameInput, lastNameInput, "organizer", organizerNameInput, facilityCodeInput, new FirebaseDB.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            // Gets the currently signed in user
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            UserSession.getInstance().setUserId(userId);

                            // Stores the user information into the UserSession class if the sign in was successful
                            db.collection("loginProfile")
                                    .whereEqualTo("userId", userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                            String firstName = document.getString("firstName");
                                            Log.d("LoginActivity", "firstName: " + firstName);
                                            UserSession.getInstance().setFirstName(firstName);


                                            Intent intent = new Intent(OrganizerSignUpActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Log.e("LoginActivity", "Failed to retrieve user profile data.");
                                        }

                                    });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(OrganizerSignUpActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        /*
         * Navigates to the previous activity when pressed (OrganizerSignInActivity)
         */
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerSignInActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });

        goBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerSignInActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });
    }

}
