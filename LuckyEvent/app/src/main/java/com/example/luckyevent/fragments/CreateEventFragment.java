package com.example.luckyevent.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.QRDownloadService;
import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CreateEventFragment handles the creation of new events in the LuckyEvent application.
 * This fragment provides functionality for:
 * - Creating events with customizable parameters
 * - Generating QR codes for event identification
 * - Storing event data in Firebase Firestore
 * - Managing geolocation requirements
 * - Handling waiting list and sample size configurations
 *
 * The fragment integrates with Firebase for data persistence and uses ZXing for QR code generation.
 *
 * @author Aagam, Tola, Amna
 * @see ScanQrFragment
 * @see QRDownloadService
 * @version 2
 * @since 1
 */
public class CreateEventFragment extends Fragment {

    // UI Components
    private TextInputEditText eventName;
    private TextInputEditText dueDate;
    private TextInputEditText dateAndTime;
    private TextInputEditText description;
    private AutoCompleteTextView waitListSize;
    private AutoCompleteTextView sampleSize;
    private MaterialButton createEventButton;
    private MaterialCheckBox geolocationCheckbox;

    // Firebase instance
    private FirebaseFirestore firestore;

    // Selected values for dropdowns
    private int selectedWaitListSize = -1;
    private int selectedSampleSize;

    /**
     * Required empty constructor for Fragment initialization
     */
    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up UI components and event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_event, container, false);

        // Set Toolbar title
        Toolbar toolbar = rootView.findViewById(R.id.topBar);
        toolbar.setTitle("Create Event");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        initializeViews(rootView);
        setupDropdowns();
        setupClickListeners();
        setupCreateEventButton();

        return rootView;
    }

    /**
     * Initializes all UI components and Firebase instance
     * Sets default values for checkboxes and connects TextInputLayouts
     *
     * @param rootView The root view of the fragment
     */
    private void initializeViews(View rootView) {
        waitListSize = rootView.findViewById(R.id.waitingListSizeDropdown);
        sampleSize = rootView.findViewById(R.id.sampleSizeDropdown);
        geolocationCheckbox = rootView.findViewById(R.id.geolocation_checkbox);
        geolocationCheckbox.setChecked(false);  // Default value

        // Initialize TextInputLayouts and their EditTexts
        TextInputLayout eventNameLayout = rootView.findViewById(R.id.input_eventName);
        eventName = (TextInputEditText) eventNameLayout.getEditText();

        TextInputLayout dueDateLayout = rootView.findViewById(R.id.input_due_date);
        dueDate = (TextInputEditText) dueDateLayout.getEditText();

        TextInputLayout dateLayout = rootView.findViewById(R.id.input_date_and_time);
        dateAndTime = (TextInputEditText) dateLayout.getEditText();

        TextInputLayout descriptionLayout = rootView.findViewById(R.id.input_description);
        description = (TextInputEditText) descriptionLayout.getEditText();

        createEventButton = rootView.findViewById(R.id.button_createEvent);
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Configures dropdown menus for waiting list and sample sizes
     * Sets up adapters and item click listeners
     */
    private void setupDropdowns() {
        // Predefined choices for waiting list and sample sizes
        List<Integer> waitingListChoices = Arrays.asList(20, 40, 60, 80, 100);
        List<Integer> sampleListChoices = Arrays.asList(10, 30, 50, 70, 90);

        // Create and set adapters for dropdowns
        ArrayAdapter<Integer> waitingListAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, waitingListChoices
        );

        ArrayAdapter<Integer> sampleSizeAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, sampleListChoices
        );

        waitListSize.setAdapter(waitingListAdapter);
        sampleSize.setAdapter(sampleSizeAdapter);

        // Set up item selection listeners
        waitListSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedWaitListSize = waitingListChoices.get(i));

        sampleSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedSampleSize = sampleListChoices.get(i));
    }

    /**
     * Make date and time fields clickable and show respective DatePicker and TimePicker dialogs
     */
    private void setupClickListeners() {
        dateAndTime.setFocusable(false);  // Prevent keyboard from appearing
        dateAndTime.setOnClickListener(v -> showDateAndTimePicker(dateAndTime));

        dueDate.setFocusable(false);  // Prevent keyboard from appearing
        dueDate.setOnClickListener(v -> showDatePicker(dueDate));
    }

    /**
     * Displays a date and time picker dialog for selecting a date and a time range (From - To)
     * and sets the formatted result in the given {@link TextInputEditText} field.
     * The method first presents a DatePickerDialog to allow the user to select a date. Once the date is selected,
     * a TimePickerDialog is displayed to allow the user to select a "From" time. After the "From" time is selected,
     * a second TimePickerDialog is shown to select the "To" time.
     * @param dateAndTime The {@link TextInputEditText} in which the selected date and time range will be displayed.
     * This field is updated with the formatted date and time string.
     * The time is displayed in 12-hour format (AM/PM), and the leading zero for single-digit hours is removed
     * where applicable (e.g., "08:00 AM" becomes "8:00 AM").
     */
    private void showDateAndTimePicker(final TextInputEditText dateAndTime){
        // First, pick the date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            // Format the selected date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());  // Date format
            String formattedDate = sdf.format(selectedDate.getTime());

            // Then, pick the time
            int hour = selectedDate.get(Calendar.HOUR_OF_DAY);  // Use the date's current time for default
            int minute = selectedDate.get(Calendar.MINUTE);

            // Create the "From" time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute1) -> {
                // Format the selected time
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Calendar fromTime = Calendar.getInstance();
                fromTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromTime.set(Calendar.MINUTE, minute1);
                String fromFormatted = timeFormat.format(fromTime.getTime());

                // Remove the leading zero for single-digit hours (if applicable)
                int hourIn12 = fromTime.get(Calendar.HOUR); // Get the hour in 12-hour format
                if (hourIn12 == 0) hourIn12 = 12; // Handle 12 AM case
                String formattedFromTime = String.format("%d:%02d %s", hourIn12, minute1, fromFormatted.split(" ")[1]);

                // Create the "To" time picker dialog
                TimePickerDialog toTimePickerDialog = new TimePickerDialog(getContext(), (view2, hourOfDayTo, minute1To) -> {
                    // Format the "To" time
                    Calendar toTime = Calendar.getInstance();
                    toTime.set(Calendar.HOUR_OF_DAY, hourOfDayTo);
                    toTime.set(Calendar.MINUTE, minute1To);
                    String toFormatted = timeFormat.format(toTime.getTime());

                    // Remove the leading zero for single-digit hours (if applicable)
                    int hourIn12To = toTime.get(Calendar.HOUR);
                    if (hourIn12To == 0) hourIn12To = 12; // Handle 12 AM case
                    String formattedToTime = String.format("%d:%02d %s", hourIn12To, minute1To, toFormatted.split(" ")[1]);

                    // Combine the date and time range and set it to the TextInputEditText
                    String dateTimeText = formattedDate+ " • " + formattedFromTime + " - " + formattedToTime;
                    dateAndTime.setText(dateTimeText);

                }, hour, minute, false);  // 12-hour format for "To" time
                toTimePickerDialog.show();

            }, hour, minute, false);  // 12-hour format for "From" time
            timePickerDialog.show();

        }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Displays a date picker dialog for selecting a due date and sets the formatted
     * selected date in the given {@link TextInputEditText}.
     * The selected date is formatted as "MMMM d, yyyy" and set to the provided
     * {@link TextInputEditText} field once the user selects a date.
     * @param dueDate The {@link TextInputEditText} where the formatted date will be displayed.
     */
    private void showDatePicker(final TextInputEditText dueDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            // Format the selected date
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());  // Set the desired format
            String formattedDate = sdf.format(selectedDate.getTime());

            // Set the formatted date to the target TextInputEditText
            dueDate.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Sets up the create event button click listener
     * Validates inputs before proceeding with event creation
     */
    private void setupCreateEventButton() {
        createEventButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveEventInfoToFirestore();
            }
        });
    }

    /**
     * Validates all required input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (eventName.getText().toString().trim().isEmpty() ||
                dueDate.getText().toString().trim().isEmpty() ||
                dateAndTime.getText().toString().trim().isEmpty() ||
                description.getText().toString().trim().isEmpty() ||
                sampleSize.getText().toString().trim().isEmpty()) {

            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Generates QR code for the event and updates Firestore with QR content
     * Uses ZXing library for QR code generation
     *
     * @param eventId The unique identifier of the created event
     */
    private void generateQRCode(String eventId) {
        try {
            // Create QR content with application-specific prefix
            String qrContent = "LuckyEvent_" + eventId;

            // Generate QR code bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);

            // Update Firestore with QR content
            Map<String, Object> qrUpdate = new HashMap<>();
            qrUpdate.put("qrContent", qrContent);

            firestore.collection("events")
                    .document(eventId)
                    .update(qrUpdate)
                    .addOnSuccessListener(aVoid -> {
                        // QR code generation successful
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update QR code info", Toast.LENGTH_SHORT).show());

        } catch (WriterException e) {
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves event information to Firebase Firestore
     * Creates new event document and updates user profile
     */
    private void saveEventInfoToFirestore() {
        if (!validateInputs()) {
            return;
        }

        // Verify user authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = user.getUid();
        // Prepare event data
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName.getText().toString().trim());
        eventInfo.put("dueDate", dueDate.getText().toString().trim());
        eventInfo.put("dateAndTime", dateAndTime.getText().toString().trim());
        eventInfo.put("description", description.getText().toString().trim());
        // If the waitList size is -1, it can occupy unlimited entrants
        eventInfo.put("waitListSize", selectedWaitListSize);
        eventInfo.put("sampleSize", selectedSampleSize);
        eventInfo.put("currentWaitList", 0);
        eventInfo.put("organizerId", userID);
        eventInfo.put("status", "active");
        eventInfo.put("createdAt", System.currentTimeMillis());
        eventInfo.put("geolocationRequired", geolocationCheckbox.isChecked());

        // Save event to Firestore
        firestore.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    addEventToProfile(userID, eventId);
                    generateQRCode(eventId);
                    navigateToMyEvents(eventId);
                    Toast.makeText(getContext(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create event", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Adds the created event ID to the user's profile
     *
     * @param userID The ID of the current user
     * @param eventID The ID of the created event
     */
    private void addEventToProfile(String userID, String eventID) {
        firestore.collection("loginProfile")
                .document(userID)
                .update("myEvents", FieldValue.arrayUnion(eventID))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add event to profile", Toast.LENGTH_SHORT).show());
    }
    /**
     * Navigates to the event details screen after successful event creation
     *
     * @param eventId The ID of the created event
     */
    private void navigateToMyEvents(String eventId) {
        DisplayOrganizerEventsFragment displayOrganizerEventsFragment = new DisplayOrganizerEventsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        displayOrganizerEventsFragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OrganizerMenuFragment, displayOrganizerEventsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}