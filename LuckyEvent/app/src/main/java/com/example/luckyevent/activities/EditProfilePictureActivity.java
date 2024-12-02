package com.example.luckyevent.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This activity displays the ui elements needed for the editing and deletion of user profile images
 * The selected image url is stored via firebase storage and firestore
 *
 * @author Seyi
 * @see FirebaseDB,UserSession
 * @version 1
 * @since 1
 */
public class EditProfilePictureActivity extends AppCompatActivity {
    private ImageView profile;
    private Button changeProfilePictureButton;
    private Button removeProfilePictureButton;
    private String imageUrl;
    private Uri imageUri;
    private String lastName;
    private ImageButton backButton;
    private static final int selectAmount = 1;
    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private String documentID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);

        // Sets the content view to the edit profile picture layout when the activity is created
        setContentView(R.layout.edit_profile_picture);

        documentID = getIntent().getStringExtra("profileID");
        lastName = getIntent().getStringExtra("lastName");

        // Loads the retrieved image url into the screen's image view
        firebaseDB = new FirebaseDB(this);
        profile = findViewById(R.id.imageView);
        imageUrl = UserSession.getInstance().getProfileUri();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profile);
        }

        changeProfilePictureButton = findViewById(R.id.change_profile_button);
        removeProfilePictureButton = findViewById(R.id.remove_profile_button);
        backButton = findViewById(R.id.imageButton);

        // If the change profile picture button is clicked,
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), selectAmount);

            }
        });

        // When the remove profile picture button is clicked, replace the user's profile picture with default profile picture
        removeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initials = (UserSession.getInstance().getUserName() != null && !UserSession.getInstance().getUserName().isEmpty())
                        ? String.valueOf(UserSession.getInstance().getUserName().charAt(0))
                        :String.valueOf(lastName.charAt(0));
                generateDefaultProfileImage(initials, new DefaultProfileImageCallback() {
                    @Override
                    public void onImageGenerated(Uri generatedUri) {
                        imageUri = generatedUri;
                        updateProfPic();
                        Picasso.get().load(imageUrl).into(profile);

                    }

                    @Override
                    public void onFailure() {
                        Log.e("EditProfilePictureActivity", "onFailure: Failed to generate profile image");
                    }
                });
            }
        });

        // Navigates to the previous screen when clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfilePictureActivity.this, EditProfileActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selectAmount && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profile);
            updateProfPic();

        }
    }

    /**
    * Generates a default profile picture when this method is called upon
     * This was made possible with the help of the picasso api and custom url
     * @param initials This a string that contains the first letter of the users username
     * @param callback This is a callback that is used to inform other methods if the generate default image was successful
    */
    public void generateDefaultProfileImage(String initials, DefaultProfileImageCallback callback) {
        // The custom url used to generate an avatar for the user
        String avatarUrl = "https://ui-avatars.com/api/?name=" + initials + "&size=128&background=0D8ABC&color=fff";

        // The picasso api generates an image by using the custom url provided
        Picasso.get()
                .load(avatarUrl)
                .into(new com.squareup.picasso.Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Load the image uri into the image view when the image is successfully generated
                        File file = new File(getFilesDir(), "default_profile_image.jpg");
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Uri imageUri = Uri.fromFile(file);
                            Log.e("EntrantSignUp", "imageUri:" + imageUri);
                            callback.onImageGenerated(imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.e("EntrantSignUpActivity", "Failed to download image", e);
                        callback.onFailure();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }

    /**
    * Updates the image urls stored on firestore and firebase storage
     * The users id is used as the document name when storing the image url on firestore and firebase storage
    */
    public void updateProfPic(){
        Log.e("EditProfilePictureActivity", "updateProfPic: userId = " +UserSession.getInstance().getUserId());
        firebaseDB.updateProfilePicture(imageUri, UserSession.getInstance().getUserId(), new FirebaseDB.UpdateProfPicCallBack() {
            @Override
            public void onUpdateSuccess() {
                db.collection("profileImages")
                        .document(UserSession.getInstance().getUserId())
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                DocumentSnapshot imageDocument = task1.getResult();
                                String imageUrl = imageDocument.getString("imageUrl");
                                UserSession.getInstance().setProfileUri(imageUrl);
                                Log.d("EditProfilePFPActivity", "Profile Picture Successfully updated");
                            }
                            else{
                                Log.e("EditProfilePFPActivity", "Unable to retrieve download url");
                            }
                        });
            }

            @Override
            public void onUpdateFailure(String errorMessage) {
                Log.e("EditProfilePFPActivity", "Failed to update profile picture");
            }
        });
    }

    // A call back used to inform other methods if the process was successfull
    public interface DefaultProfileImageCallback {
        void onImageGenerated(Uri imageUri);
        void onFailure();
    }
}
