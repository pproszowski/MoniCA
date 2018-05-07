package com.example.powder.monica;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StorageArrayAdapter extends ArrayAdapter<String>{
    private final Context context;
    private final String[] values;

    public StorageArrayAdapter(Context context, String[] values){
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        String s = values[position];
        if (s.contains(".jpg")) {
            imageView.setImageResource(R.drawable.ic_image_gray_24dp);
        } else {
            imageView.setImageResource(R.drawable.ic_music_note_gray_24dp);
        }

        return rowView;
    }
}
