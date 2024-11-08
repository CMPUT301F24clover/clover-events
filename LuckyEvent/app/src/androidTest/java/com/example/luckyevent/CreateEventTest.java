package com.example.luckyevent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.activities.GenerateQrActivity;
import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4 .class)
@LargeTest
public class CreateEventTest {
    @Rule
    public ActivityScenarioRule<GenerateQrActivity> scenario = new ActivityScenarioRule<>(GenerateQrActivity.class);
}
