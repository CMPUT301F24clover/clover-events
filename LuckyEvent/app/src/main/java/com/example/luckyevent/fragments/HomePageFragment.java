package com.example.luckyevent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;

public class HomePageFragment extends Fragment {

    private OnNavigateListener navigateListener;

    public interface OnNavigateListener {
        void onNavigateToScanQr();
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

        LinearLayout scanQrButton = view.findViewById(R.id.scanQrButton);
        scanQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToScanQr();
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
