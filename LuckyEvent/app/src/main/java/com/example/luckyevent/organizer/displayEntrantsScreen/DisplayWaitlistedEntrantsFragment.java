package com.example.luckyevent.organizer.displayEntrantsScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.organizer.sendNotification.CreateNotificationFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * This Fragment manages the display of event waiting lists with dual view functionality:
 * 1. A list view showing all entrants in the waiting list
 * 2. A map view displaying the geographical location of entrants
 *
 * Key Features:
 * - Toggle between list and map views
 * - Real-time updates using Firebase Firestore
 * - Notification system for waiting list entrants
 * - Interactive map using Leaflet.js
 *
 * @author Tola, Mmelve
 * @see Entrant
 * @see EntrantListAdapter
 * @version 1
 * @since 1
 */
public class DisplayWaitlistedEntrantsFragment extends Fragment {
    private static final String TAG = "WaitingListFragment";

    // Lists to store entrant data
    private ArrayList<String> entrantIdsList;       // Stores entrant IDs for notification purposes
    private ArrayList<Entrant> entrantsList;        // Stores complete entrant objects for display
    private EntrantListAdapter listAdapter;         // Adapter for displaying entrants in ListView

    // Firebase references
    private CollectionReference profilesRef;         // Reference to user profiles collection
    private CollectionReference entrantsRef;        // Reference to event's waiting list collection
    private ListenerRegistration reg;               // Firestore listener registration for cleanup

    // UI Components
    private ConstraintLayout listContainer;         // Container for list view
    private ConstraintLayout mapContainer;          // Container for map view
    private MaterialButton toggleViewButton;        // Button to switch between list and map views
    private WebView mapWebView;                     // WebView for displaying the map
    private boolean isMapView = false;              // Track current view state
    private List<MapMarker> mapMarkers = new ArrayList<>();  // Store map markers for entrants
    private final Gson gson = new Gson();           // For JSON serialization of map markers

    /**
     * Inner class representing a marker on the map
     * Stores location and name information for each entrant
     */
    private static class MapMarker {
        final double lat;
        final String name;
        final double lng;

        MapMarker(String name, double lat, double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
    }

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up Firebase references, list view, web view, and notification button
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_waitinglist, container, false);

        Toolbar toolbar = view.findViewById(R.id.topBar);
        toolbar.setTitle("Waiting List");

        // set up back button
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();  // Optional: Navigate back
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        toolbar.setNavigationOnClickListener(v -> callback.handleOnBackPressed());

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");

            initializeViews(view);

            // Set up Firebase references
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            profilesRef = db.collection("loginProfile");
            entrantsRef = db.collection("events").document(eventId).collection("waitingList");

            setupListView(view);
            setupWebView(view);

            // Configure view toggle functionality
            toggleViewButton.setOnClickListener(v -> toggleView());

            // Start listening for waiting list updates
            getEntrantsList();

            setupNotificationButton(view, eventId);
        }

        return view;
    }

    /**
     * Initializes the main UI components of the fragment
     */
    private void initializeViews(View view) {
        listContainer = view.findViewById(R.id.list_container);
        mapContainer = view.findViewById(R.id.map_container);
        toggleViewButton = view.findViewById(R.id.toggle_view_button);
    }

    /**
     * Sets up the WebView for displaying the map
     * Enables JavaScript and loads the Leaflet map HTML file
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(View view) {
        mapWebView = view.findViewById(R.id.map_web_view);
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mapWebView.loadUrl("file:///android_asset/leafletmap.html");
    }

    /**
     * Initializes the ListView and its adapter for displaying entrants
     */
    private void setupListView(View view) {
        ListView listview = view.findViewById(R.id.customListView);
        entrantIdsList = new ArrayList<>();
        entrantsList = new ArrayList<>();
        listAdapter = new EntrantListAdapter(getContext(), entrantsList);
        listview.setAdapter(listAdapter);
    }

    /**
     * Updates the map markers when in map view
     * Converts markers to JSON and passes them to JavaScript
     */
    private void updateMap() {
        if (isMapView && mapWebView != null && !mapMarkers.isEmpty()) {
            String markersJson = gson.toJson(mapMarkers);
            mapWebView.evaluateJavascript(
                    "javascript:android.updateMarkers('" + markersJson + "')",
                    null
            );
        }
    }

    /**
     * Handles switching between list and map views
     * Updates UI visibility and button text accordingly
     */
    private void toggleView() {
        isMapView = !isMapView;
        if (isMapView) {
            listContainer.setVisibility(View.GONE);
            mapContainer.setVisibility(View.VISIBLE);
            toggleViewButton.setText("Show List");
            updateMap();
        } else {
            listContainer.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.GONE);
            toggleViewButton.setText("Show Map");
        }
    }

    /**
     * Sets up real-time listener for waiting list updates from Firestore
     * Updates both list and map views when data changes
     */
    private void getEntrantsList() {
        reg = entrantsRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e(TAG, "Error listening to waiting list", error);
                return;
            }

            entrantIdsList.clear();
            entrantsList.clear();
            mapMarkers.clear();

            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentSnapshot entrantDoc : snapshot.getDocuments()) {
                    String entrantId = entrantDoc.getString("userId");
                    Double latitude = entrantDoc.getDouble("latitude");
                    Double longitude = entrantDoc.getDouble("longitude");

                    if (entrantId != null) {
                        entrantIdsList.add(entrantId);
                        fetchEntrantDetails(entrantId, latitude, longitude);
                    }
                }
            } else {
                updateEmptyListView();
                updateMap();
            }
        });
    }

    /**
     * Updates the UI when the waiting list is empty
     */
    private void updateEmptyListView() {
        TextView textView = getView().findViewById(R.id.text_emptyList);
        textView.setText(R.string.no_entrants);
        textView.setVisibility(View.VISIBLE);
    }

    /**
     * Fetches detailed information for each entrant from their profile
     * Updates both list and map markers with the retrieved data
     */
    private void fetchEntrantDetails(String entrantId, Double latitude, Double longitude) {
        profilesRef.document(entrantId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String name = String.format("%s %s",
                        document.getString("firstName"),
                        document.getString("lastName"));

                Entrant entrant = new Entrant(entrantId, name);
                entrantsList.add(entrant);

                if (latitude != null && longitude != null) {
                    mapMarkers.add(new MapMarker(name, latitude, longitude));
                }

                listAdapter.notifyDataSetChanged();
                if (isMapView) {
                    updateMap();
                }
            }
        }).addOnFailureListener(e ->
                Log.e(TAG, "Error fetching entrant details", e));
    }

    /**
     * Sets up the notification button and its click behavior
     * Shows toast if list is empty, otherwise opens notification dialog
     */
    private void setupNotificationButton(View view, String eventId) {
        FloatingActionButton notificationButton = view.findViewById(R.id.create_notification_fab);
        notificationButton.setOnClickListener(v -> {
            if (entrantIdsList.isEmpty()) {
                Toast.makeText(getContext(), "No one to send notifications to.",
                        Toast.LENGTH_SHORT).show();
            } else {
                showNotificationDialog(eventId);
            }
        });
    }

    /**
     * Creates and shows the notification dialog
     * Passes necessary data to the dialog fragment
     */
    private void showNotificationDialog(String eventId) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("entrantIdsList", entrantIdsList);
        bundle.putString("eventId", eventId);
        CreateNotificationFragment dialog = new CreateNotificationFragment();
        dialog.setArguments(bundle);
        dialog.show(getParentFragmentManager(), "dialog");
    }

    /**
     * Cleanup methods to remove Firestore listeners when fragment is destroyed
     */
    @Override
    public void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reg != null) {
            reg.remove();
        }
    }
}