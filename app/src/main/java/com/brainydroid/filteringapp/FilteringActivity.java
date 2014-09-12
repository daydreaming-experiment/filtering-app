package com.brainydroid.filteringapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.brainydroid.filteringapp.filtering.Filterer;
import com.brainydroid.filteringapp.filtering.Timer;

import java.util.ArrayList;
import java.util.Arrays;


public class FilteringActivity extends ActionBarActivity {

    private static String TAG = "FilteringActivity";

    private static ArrayList<String> possibilities = new ArrayList<String>(Arrays.asList(new String[]{
            // From Home
            "Cooking|food, eat", "Eating", "Housework (dishes, cleaning)", "Care (shower, makeup)", "Nursing", "Petting",
            "Intimate relations", "Having a nap", "Resting", "Playing music", "Making a drawing", "Video game",
            "Watching a program", "Computer, Internet or Email", "Reading",
            // From Commuting
            "Driving", "Biking", "Walking", "Train, metro, bus", "Listening to music or the radio",
            "Attempting to daydream", "Reading", "Writing or texting", "Talking", "Watching a program",
            "Video game (on phone)",
            // From Outside
            "Walking", "Biking", "Gardening", "Resting", "Having a nap", "Reading", "Talking",
            "Attempting to daydream", "Shopping", "Petting", "Exercising", "Physical game",
            // From Public place
            "Shopping, groceries", "Reading (library)", "Watching screen (cinema)", "Eating (restaurant)",
            "Talking", "Exercising",
            // From Work
            "Attending a presentation, conference, or class", "Computer, Internet or Email|google, facebook, twitter", "Reading",
            "Writing (coding)", "Making a drawing", "Walking", "Waiting", "Actively thinking (reasoning)"
    }));

    private Timer timer = new Timer();
    private AutoCompleteTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, possibilities);
        textView = (AutoCompleteTextView)findViewById(R.id.activity_activities_autoCompleteTextView);
        textView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.requestFocus();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(textView,
                InputMethodManager.SHOW_FORCED);
    }
}
