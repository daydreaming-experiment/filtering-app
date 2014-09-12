package com.brainydroid.filteringapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.filteringapp.filtering.MetaString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


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

    private AutoCompleteTextView textView;
    private LayoutInflater inflater;
    private HashMap<MetaString, LinearLayout> selectionViews = new HashMap<MetaString, LinearLayout>();
    private LinearLayout selectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectionLayout = (LinearLayout)findViewById(R.id.activity_activities_list);

        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, possibilities);
        textView = (AutoCompleteTextView)findViewById(R.id.activity_activities_autoCompleteTextView);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item " + position + " clicked (id " + id + ")");
                addSelection(adapter.getItemById(id));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.requestFocus();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(textView,
                InputMethodManager.SHOW_FORCED);
    }

    protected void addSelection(final MetaString ms) {
        Log.d(TAG, "Adding selection");

        if (selectionViews.containsKey(ms)) {
            Toast.makeText(this, "You already selected this item", Toast.LENGTH_LONG).show();
            return;
        }

        LinearLayout itemLayout = (LinearLayout)inflater.inflate(R.layout.item_selected_view,
                selectionLayout, false);
        ((TextView)itemLayout.findViewById(R.id.item_text)).setText(ms.getOriginal());
        ((TextView)itemLayout.findViewById(R.id.item_tags)).setText(ms.getJoinedTags());

        ImageButton.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelection(ms);
            }
        };
        itemLayout.findViewById(R.id.item_delete).setOnClickListener(listener);

        selectionLayout.addView(itemLayout);
        selectionViews.put(ms, itemLayout);
    }

    protected void removeSelection(MetaString ms) {
        Log.d(TAG, "Removing selection");
        if (!selectionViews.containsKey(ms)) {
            Log.v(TAG, "Not item to remove");
            return;
        }

        selectionLayout.removeView(selectionViews.get(ms));
    }

}
