package com.example.luckyevent.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays the ui elements that are needed for the addition and deletion of event posters
 * Firebase storage and firestore was utilized to store the event posters
 *
 * @author Seyi
 * @version 1
 * @since 1
 */
public class EventPosterFragment extends Fragment {
    private FirebaseFirestore db;
    private MaterialButton editPosterButton;
    private MaterialButton removePosterButton;
    private ImageView poster;
    private Uri imageUri;
    private static final int selectAmount = 1;
    private String eventId;
    private FirebaseStorage storage;
    private InputStream inputStream;
    private String path;
    private StorageReference storageRef;
    private String Tag = "EventPosterFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_poster, container, false);
        eventId = getArguments().getString("eventId");
        storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
        db = FirebaseFirestore.getInstance();

        // Check if this event has a poster document in the eventPosters collection
        db.collection("eventPosters").document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String posterUrl = documentSnapshot.getString("PosterUrl");
                            if (posterUrl != null) {
                                // Load the image url into th image view
                                Log.d(Tag, "PosterUrl: " + posterUrl);
                                Picasso.get().load(posterUrl).into(poster);
                                imageUri = Uri.parse(posterUrl);
                            } else {
                                Log.d(Tag, "PosterUrl field is missing in the document.");
                            }
                        } else {
                            Log.d(Tag, "No such document found.");
                            poster.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(Tag, "Error getting document", e);
                    }
                });


        editPosterButton = view.findViewById(R.id.add_edit_poster_button);
        removePosterButton = view.findViewById(R.id.delete_event_poster_button);
        poster = view.findViewById(R.id.imageView2);

        // When clicked, prompt the organizer to input their desired event poster
        editPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Event Poster"), selectAmount);

            }
        });

        removePosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEventPoster();
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selectAmount && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(poster);
            updateEventPoster();
        }
    }

    /**
     * This method removes the event poster's image url from the database
     * All traces of the target event poster are removed from firebase storage and firestore
     */
    public void removeEventPoster() {
        if (eventId == null) {
            Log.e(Tag, "Event ID is null");
            return;
        }
        //The target path of our document
        storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
        path = "eventPosters/" + eventId;
        storageRef = storage.getReference().child(path);

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(Tag, "Poster successfully deleted from Firebase Storage");
                // After successfully deleting the image url from the firebase storage, the url is then deleted from firestore
                db.collection("eventPosters").document(eventId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(Tag, "DocumentSnapshot successfully deleted!");
                                poster.setImageResource(android.R.drawable.ic_menu_gallery); // Reset poster image
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(Tag, "Error deleting document", e);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(Tag, "Error deleting file from Firebase Storage: " + e.getMessage(), e);
            }
        });
    }

    /**
     * This method updates the image url of the event poster that is stored in the database
     * All traces of the event poster are first removed from the database before the replacement
     * is uploaded
     */
    public void updateEventPoster(){
        try {

            inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            Log.e("EventPosterFragment", "updateEventPoster:" + eventId);
            path = "eventPosters/" + eventId;

            // We delete the target document from the firebase storage path
            storageRef = storage.getReference().child(path);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
                @Override
                public void onSuccess(Void unused) {
                    Log.d(Tag, "updateProfilePicture: file successfully deleted");
                    // After we have successfully removed the image poster form the firebase storage
                    // we then proceed to remove the event poster from firestore
                    db.collection("eventPosters").document(eventId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(Tag, "DocumentSnapshot successfully deleted!");
                                    // Once all traces of the event poster has been removed, we upload the replacement
                                    addEventPoster();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(Tag, "Error deleting document", e);
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(Tag, "updateProfilePicture: Deletion failed");
                    addEventPoster();
                }
            });
        }
        catch (FileNotFoundException e) {
            Log.e(Tag, "File not found: " + e.getMessage(), e);
        }
    }


    /**
     * This method adds the event poster's image url into the database
     * The image url is add to the project's firebase storage and firestore
     *
     */
    public void addEventPoster() {
        if (imageUri == null || eventId == null) {
            Log.e(Tag, "Image URI or Event ID is null");
            return;
        }

        try {
            // We try to convert the image url into an input steam before we upload it to firebase storage
            inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            storageRef.putStream(inputStream)
                    .addOnSuccessListener(taskSnapshot -> {
                        // After successfully uploading the image url into firebase storage,
                        // we upload the same url into firestore
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Map<String, Object> imageData = new HashMap<>();
                            imageData.put("PosterUrl", downloadUrl);
                            db.collection("eventPosters").document(eventId).set(imageData)
                                    .addOnSuccessListener(aVoid ->
                                            Log.e(Tag, "Image URL saved to Firestore"))
                                    .addOnFailureListener(e ->
                                            Log.e(Tag, "Failed to save URL to Firestore: " + e.getMessage()));
                        }).addOnFailureListener(e -> {
                            Log.e(Tag, "Error getting download URL: " + e.getMessage(), e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(Tag, "Error uploading file: " + e.getMessage(), e);
                    });
        } catch (FileNotFoundException e) {
            Log.e(Tag, "File not found: " + e.getMessage(), e);
        }
    }





}
