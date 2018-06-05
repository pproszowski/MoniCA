package com.example.powder.monica;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MeetingsListAdapter extends ArrayAdapter<MeetingItem> implements Filterable {

    private List<MeetingItem> filteredList;

    private List<MeetingItem> meetingItems;

    private MeetingFilter meetingFilter;

    @NonNull
    @Override
    public Filter getFilter() {
        if(meetingFilter == null){
            meetingFilter = new MeetingFilter();
        }

        return meetingFilter;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public MeetingItem getItem(int position) {
        return filteredList.get(position);
    }

    public MeetingsListAdapter(Context context, List<MeetingItem> meetingItems) {
        super(context, 0, meetingItems);
        this.filteredList = meetingItems;
        this.meetingItems = meetingItems;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MeetingItem meetingItem = getItem(position);



        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meeting_item, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.meetingCheckBox);

        checkBox.setOnClickListener(v -> {
            CheckBox cb = (CheckBox) v;
            MeetingItem mi = (MeetingItem) cb.getTag();
            if (checkBox.isChecked()) {
                mi.check();
            } else {
                mi.unCheck();
            }
        });

        checkBox.setTag(meetingItem);
        checkBox.setChecked(meetingItem.isChecked());

        TextView meetingName = (TextView) convertView.findViewById(R.id.meetingName);
        TextView meetingDate = (TextView) convertView.findViewById(R.id.meetingDate);

        meetingName.setText(meetingItem.getName());
        meetingDate.setText(meetingItem.getDate());

        return convertView;
    }

    private class MeetingFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            System.out.println("HOP");
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<MeetingItem> tempList = new ArrayList<>();

                for (MeetingItem meetingItem: meetingItems) {
                    if (meetingItem.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(meetingItem);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = meetingItems.size();
                filterResults.values = meetingItems;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<MeetingItem>) results.values;
            notifyDataSetChanged();
        }
    }
}
