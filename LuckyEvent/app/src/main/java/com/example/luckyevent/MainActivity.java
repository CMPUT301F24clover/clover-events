package com.example.luckyevent;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (!loggedIn) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.screen_template_entrant);
            Toolbar toolbar = findViewById(R.id.topBar);
            toolbar.setTitle("Home");
        }
    }
}