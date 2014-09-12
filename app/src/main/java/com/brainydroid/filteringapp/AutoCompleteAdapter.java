package com.brainydroid.filteringapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.brainydroid.filteringapp.filtering.Filterer;
import com.brainydroid.filteringapp.filtering.MetaString;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AutoCompleteAdapter implements Filterable, ListAdapter {

    private static String TAG = "AutoCompleteAdapter";

    private static int VIEW_TYPE_NORMAL = 0;
    private static int VIEW_TYPE_NOTHING = 1;

    private LayoutInflater inflater;
    private Filter filter;
    private ArrayList<MetaString> results = null;
    private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
    private HashMap<Long,MetaString> idCache = new HashMap<Long,MetaString>();

    public AutoCompleteAdapter(Context context, ArrayList<String> possibilities) {
        Log.d(TAG, "Initializing");
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filter = new Filterer(this, possibilities);
    }

    public boolean areFilterResultsEmpty() {
        return results == null || results.size() == 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= getCount()) throw new ArrayIndexOutOfBoundsException();
        return !areFilterResultsEmpty();  // The "nothing found" item is not active
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        // If no results, we still have one item for "nothing found"
        if (areFilterResultsEmpty()) return 1;
        return results.size();
    }

    @Override
    public MetaString getItem(int position) {
        if (position >= getCount()) throw new ArrayIndexOutOfBoundsException();
        if (areFilterResultsEmpty()) {
            return new MetaString("Nothing found");
        } else {
            return results.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        MetaString item = getItem(position);
        long id = item.hashCode();
        if (!idCache.containsKey(id)) {
            idCache.put(id, item);
        }
        return id;
    }

    public MetaString getItemById(long id) {
        if (!idCache.containsKey(id)) {
            throw new RuntimeException("Item never shown in list, so id not cached");
        }
        return idCache.get(id);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private int getLayoutTypeId() {
        if (areFilterResultsEmpty()) {
            return R.layout.item_nothing;
        } else {
            return R.layout.item_view;
        }
    }

    private int getViewTypeId() {
        if (areFilterResultsEmpty()) {
            return R.id.item_nothing_view;
        } else {
            return R.id.item_something_view;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null || !(convertView instanceof LinearLayout)) {
            layout = (LinearLayout)inflater.inflate(getLayoutTypeId(), parent, false);
        } else {
            // If we have the right type of view, keep it. Otherwise inflate.
            if (convertView.getId() == getViewTypeId()) {
                layout = (LinearLayout)convertView;
            } else {
                layout = (LinearLayout)inflater.inflate(getLayoutTypeId(), parent, false);
            }
        }

        MetaString item = getItem(position);
        ((TextView)layout.findViewById(R.id.item_text)).setText(item.getOriginal());
        if (!areFilterResultsEmpty()) {
            String joinedTags = item.getJoinedTags();
            TextView tagsView = (TextView)layout.findViewById(R.id.item_tags);
            if (joinedTags == null) {
                tagsView.setVisibility(View.GONE);
            } else {
                tagsView.setVisibility(View.VISIBLE);
                tagsView.setText(joinedTags);
            }
        }

        return layout;
    }

    @Override
    public int getItemViewType(int position) {
        if (areFilterResultsEmpty()) {
            return VIEW_TYPE_NOTHING;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        // We're never empty since we always have at least the "Nothing found." item
        return false;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setResults(ArrayList<MetaString> results) {
        Log.d(TAG, "Setting results");
        boolean oldResultsEmpty = areFilterResultsEmpty();
        this.results = results;
        // Don't update if results were empty before and they still are now
        if (observers.size() > 0 && (areFilterResultsEmpty() != oldResultsEmpty)) {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }
    }
}
