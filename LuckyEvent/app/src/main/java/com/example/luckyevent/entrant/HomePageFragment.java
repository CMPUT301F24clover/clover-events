package com.example.luckyevent.entrant;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.activities.MenuActivity;

/**
 * Displays a welcome message, brief details of upcoming events and it also gives the entrant the ability
 * to access other fragments via buttons. This is the first fragment the entrant will see upon successfully
 * signing in
 *
 * @author Amna, Seyi,
 * @see MenuActivity
 * @version 1
 * @since 1
 */
public class HomePageFragment extends Fragment {

    private OnNavigateListener navigateListener;

    public interface OnNavigateListener {
        void onNavigateToScanQr();
        void onNavigateToWaitingList();
        void onNavigateToNotifications();
        void onNavigateToProfile();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigateListener) {
            navigateListener = (OnNavigateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNavigateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_homepage, container, false);
        String firstName = UserSession.getInstance().getFirstName();
        Log.d("HomePageFragment", " My FirstName: " + firstName);
        TextView welcomeTextView = view.findViewById(R.id.welcomeMessage);

        if (firstName != null){
            String welcomeMessage = getString(R.string.welcome_message, firstName);
            welcomeTextView.setText(welcomeMessage);

        }

        else{
            String welcomeMessage = "Welcome!";
            welcomeTextView.setText(welcomeMessage);

        }

        Button scanQrButton = view.findViewById(R.id.scanQrButton);
        scanQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToScanQr();
                }
            }
        });
        Button waitingListButton = view.findViewById(R.id.waitinglistButton);
        waitingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToWaitingList();
                }
            }
        });
        Button notficationButton = view.findViewById(R.id.notificationsButton);
        notficationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToNotifications();
                }
            }
        });
        Button profileButton = view.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToProfile();
                }
            }
        });


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigateListener = null;
    }
}
