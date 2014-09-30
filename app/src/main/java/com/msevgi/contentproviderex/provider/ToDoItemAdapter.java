package com.msevgi.contentproviderex.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msevgi.contentproviderex.R;

import java.util.List;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {
    private final int mResource;
    private final LayoutInflater mInflator;

    public ToDoItemAdapter(Context context, int resource, List<ToDoItem> items) {
        super(context, resource, items);
        mResource = resource;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflator.inflate(mResource, parent,false);
        }
        final LinearLayout todoView = (LinearLayout) convertView;

        final ToDoItem item = getItem(position);
        final TextView taskView = (TextView) todoView.findViewById(R.id.row);
        taskView.setText(item.getTask());

        return todoView;
    }
}
