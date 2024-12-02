package com.example.luckyevent.organizer.eventDetails;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A fragment that retrieves and displays the QR code of a specific event from Firestore.
 *
 * @author Tola, Aagam
 * @see EventDetailsFragment
 * @version 1
 * @since 1
 */
public class QrDisplayFragment extends Fragment {
    private static final String TAG = "QrDisplayFragment";
    private static final String ARG_EVENT_ID = "eventId";

    private FirebaseFirestore db;
    private String eventId;

    private ImageView qrImageView;
    private ProgressBar progressBar;

    public QrDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for the fragment and sets up the UI components, including the toolbar
     * and back button. It retrieves the event ID from the fragment arguments and fetches the QR code
     * associated with the event from Firestore. If the event ID is not provided, a toast message is shown.
     *
     * @param inflater The LayoutInflater object used to inflate the fragment's layout.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previous state (if any).
     * @return The inflated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.organizer_qr_display, container, false);

        // Set Toolbar title
        Toolbar toolbar = rootView.findViewById(R.id.topBar);
        toolbar.setTitle("Event QR Code");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        eventId = getArguments().getString("eventId");
        db = FirebaseFirestore.getInstance();

        qrImageView = rootView.findViewById(R.id.qrImageView);
        progressBar = rootView.findViewById(R.id.progressBar);

        if (eventId != null) {
            fetchAndDisplayQRCode();
        } else {
            Toast.makeText(getContext(), "Event ID not provided", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    /**
     * Fetches the QR code content from Firestore and displays it as a QR code image.
     */
    private void fetchAndDisplayQRCode() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String qrContent = document.getString("qrContent");
                    if (qrContent != null) {
                        generateAndDisplayQRCode(qrContent);
                    } else {
                        Log.e(TAG, "QR content is null");
                        Toast.makeText(getContext(), "QR code content not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Document does not exist");
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error getting document", task.getException());
                Toast.makeText(getContext(), "Failed to fetch QR code data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Generates and displays the QR code from the provided content.
     *
     * @param qrContent The content to encode in the QR code.
     */
    private void generateAndDisplayQRCode(String qrContent) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = barcodeEncoder.encodeBitmap(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );
            qrImageView.setImageBitmap(qrBitmap);
            qrImageView.setVisibility(View.VISIBLE); // Make the ImageView visible
            progressBar.setVisibility(View.GONE); // Hide the ProgressBar
        } catch (WriterException e) {
            Log.e(TAG, "QR generation error", e);
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
