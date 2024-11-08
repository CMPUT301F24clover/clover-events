package com.example.luckyevent.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.example.luckyevent.R;
import com.example.luckyevent.ScanQR;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Displays the fields needed to sign up as an entrant. This activity navigates to the MenuActivity if the user successfully
 * sign ups as an entrant
 *
 * @author Seyi
 * @see FirebaseDB
 * @see UserSession
 * @version 1
 * @since 1
 */
public class EntrantSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private FirebaseDB firebaseDB;
    private ImageView gobackButton;
    private ImageView profileImage;
    private static final int selectAmount = 1;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDB = new FirebaseDB(this);
        setContentView(R.layout.screen_template_entrant_signup);
        userName = findViewById(R.id.SignUpUsernameInput);
        password = findViewById(R.id.SignUpPasswordInput);
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);
        gobackButton = findViewById(R.id.previousIcon);


        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    String userNameInput = userName.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();
                    String firstNameInput = firstName.getText().toString().trim();
                    String lastNameInput = lastName.getText().toString().trim();
                    String Initials;

                    if (lastNameInput.isEmpty()) {
                        Initials = "";
                    }
                    else {
                        Initials = String.valueOf(lastNameInput.charAt(0));
                    }

                    if (userNameInput.isEmpty() || passwordInput.isEmpty()) {
                        if(userNameInput.isEmpty() && passwordInput.isEmpty()){
                            Toast.makeText(EntrantSignUpActivity.this, "Username field and password field are required", Toast.LENGTH_SHORT).show();
                        }
                        else if(userNameInput.isEmpty()) {
                            Toast.makeText(EntrantSignUpActivity.this, "Username field is required", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EntrantSignUpActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else{
                        if(imageUri == null){
                            imageUri = generateDefaultProfileImage(Initials);
                        }
                        firebaseDB.signUp(userNameInput, passwordInput, firstNameInput, lastNameInput, "entrant", null, null, new FirebaseDB.SignInCallback() {
                            @Override
                            public void onSuccess() {
                                //gets the currently signed in user
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = firebaseUser.getUid();
                                UserSession.getInstance().setUserId(userId);
                                UserSession.getInstance().setFirstName(firstNameInput);

                                Intent intent = new Intent(EntrantSignUpActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish(); // Optional
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(EntrantSignUpActivity.this, "Sign-Up failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        },imageUri.toString());
                    }

                }
            }
        });

        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntrantSignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });

        FloatingActionButton addImageButton = findViewById(R.id.button_add_image); // Or your Button ID
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), selectAmount);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selectAmount && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage = findViewById(R.id.profile_image);
            profileImage.setImageURI(imageUri);
        }
    }

    public Uri generateDefaultProfileImage(String initials) {
        String avatarUrl = "https://ui-avatars.com/api/?name=" + initials + "&size=128&background=0D8ABC&color=fff";
        try {
            FutureTarget<File> futureTarget = Glide.with(this)
                    .asFile()
                    .load(avatarUrl)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .submit();

            File downloadedImage = futureTarget.get();

            if (downloadedImage != null) {
                return Uri.fromFile(downloadedImage);
            } else {
                Log.e("EntrantSignUpActivity", "Downloaded image is null");
                return null;
            }

        } catch (ExecutionException | InterruptedException e) {
            Log.e("EntrantSignUpActivity", "Failed to download image", e);
            return null;
        }
    }

}




