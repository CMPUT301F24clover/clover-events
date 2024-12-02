package com.example.luckyevent.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyevent.R;
import com.example.luckyevent.fragments.ImageListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ArrayList<HashMap<String, String>> imageList;
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_images);

        recyclerView = findViewById(R.id.imageListView);
        db = FirebaseFirestore.getInstance();
        imageList = new ArrayList<>();

        adapter = new ImageListAdapter(this, imageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Set LayoutManager
        recyclerView.setAdapter(adapter);  // Set the adapter to the RecyclerView

        loadImagesFromFirestore();
    }

    private void loadImagesFromFirestore() {
        db.collection("eventPosters").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Log the document data
                        Log.d("FirestoreData", "Document data: " + document.getData());

                        String url = document.getString("PosterUrl"); // Use the correct key
                        if (url != null && !url.isEmpty()) {
                            HashMap<String, String> image = new HashMap<>();
                            image.put("url", url);
                            image.put("description", "Event Poster");
                            image.put("id", document.getId());
                            image.put("collection", "eventPosters");
                            imageList.add(image);
                        } else {
                            Log.e("FirestoreData", "No PosterUrl field found for eventPoster document " + document.getId());
                        }
                    }
                    // Load profileImages collection
                    db.collection("profileImages").get()
                            .addOnSuccessListener(profileSnapshots -> {
                                for (QueryDocumentSnapshot document : profileSnapshots) {
                                    // Log the document
                                    Log.d("FirestoreData", "Document data: " + document.getData());

                                    String url = document.getString("imageUrl");
                                    if (url != null && !url.isEmpty()) {
                                        HashMap<String, String> image = new HashMap<>();
                                        image.put("url", url);
                                        image.put("description", "Profile Picture");
                                        image.put("id", document.getId());
                                        image.put("collection", "profileImages");
                                        imageList.add(image);
                                    } else {
                                        Log.e("FirestoreData", "No imageUrl field found for profileImages document " + document.getId());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to load profile images", Toast.LENGTH_SHORT).show();
                                Log.e("BrowseImagesActivity", "Error loading profile images", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load event posters", Toast.LENGTH_SHORT).show();
                    Log.e("BrowseImagesActivity", "Error loading event posters", e);
                });
    }

    public void removeImage(String id, String collection, int position) {
        // Remove image from Firestore
        db.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove image from the UI (RecyclerView)
                    imageList.remove(position);  // Remove image from the list
                    adapter.notifyItemRemoved(position);

                    Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to remove image", Toast.LENGTH_SHORT).show();
                    Log.e("BrowseImagesActivity", "Error deleting image", e);
                });
    }
}
