/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */


package it.crs4.most.voip.examples;

import java.util.List;

import it.crs4.most.voip.interfaces.IBuddy;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BuddyArrayAdapter extends ArrayAdapter<IBuddy> {

    public BuddyArrayAdapter(Context context, int textViewResourceId, List<IBuddy> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.buddy_row, null);
            viewHolder = new ViewHolder();
            viewHolder.uri = (TextView)convertView.findViewById(R.id.textBuddyUri);
            viewHolder.status = (TextView)convertView.findViewById(R.id.textBuddyState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        IBuddy buddy = getItem(position);
        viewHolder.uri.setText(buddy.getUri());
        viewHolder.status.setText(buddy.getStatusText());
        return convertView;
    }

    private class ViewHolder {
        public TextView uri;
        public TextView status;
    }
}