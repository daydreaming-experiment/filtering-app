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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AutoCompleteAdapter implements Filterable, ListAdapter {

    private static String TAG = "AutoCompleteAdapter";

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

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= getCount()) throw new ArrayIndexOutOfBoundsException();
        return true;
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
        if (results == null) return 0;
        return results.size();
    }

    @Override
    public MetaString getItem(int position) {
        if (position >= getCount() || results == null) throw new ArrayIndexOutOfBoundsException();
        return results.get(position);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView != null && convertView instanceof LinearLayout) {
            layout = (LinearLayout)convertView;
        } else {
            layout = (LinearLayout)inflater.inflate(R.layout.item_view, parent, false);
        }

        MetaString item = getItem(position);
        ((TextView)layout.findViewById(R.id.item_text)).setText(item.getOriginal());
        String joinedTags = item.getJoinedTags();
        TextView tagsView = (TextView)layout.findViewById(R.id.item_tags);
        if (joinedTags == null) {
            tagsView.setVisibility(View.GONE);
        } else {
            tagsView.setVisibility(View.VISIBLE);
            tagsView.setText(joinedTags);
        }

        return layout;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setResults(ArrayList<MetaString> results) {
        Log.d(TAG, "Setting results");
        this.results = results;
        if (observers.size() > 0) {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }
    }
}
