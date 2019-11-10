package com.portfolio.andaya_jae;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReadingListAdapter extends ArrayAdapter<Reading> {

    private Activity context;
    private List<Reading> readingList;

    public ReadingListAdapter(Activity context, List<Reading> readingList) {
        super(context, R.layout.list_layout, readingList);
        this.context = context;
        this.readingList = readingList;
    }

    public ReadingListAdapter(Activity context, int resource, List<Reading> objects, Activity context1, List<Reading> readingList) {
        super(context, resource, objects);
        this.context = context1;
        this.readingList = readingList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvListUserIDValue = listViewItem.findViewById(R.id.tvListUserIDValue);
        TextView tvListDateTimeValue = listViewItem.findViewById(R.id.tvListDateTimeValue);
        TextView tvListSystolicValue = listViewItem.findViewById(R.id.tvListSystolicValue);
        TextView tvListDiastolicIDValue = listViewItem.findViewById(R.id.tvListDiastolicValue);
        TextView tvListConditionValue = listViewItem.findViewById(R.id.tvListConditionValue);

        Reading reading = readingList.get(position);

        tvListUserIDValue.setText(reading.getUserID());
        SimpleDateFormat dest = new SimpleDateFormat(("dd/MMM/yyyy hh:mm"), Locale.ENGLISH);
        tvListDateTimeValue.setText(dest.format(reading.getDate()));
        tvListSystolicValue.setText(String.valueOf(reading.getSystolic()));
        tvListDiastolicIDValue.setText(String.valueOf(reading.getDiastolic()));
        tvListConditionValue.setText(reading.getCondition());
        listViewItem.setBackgroundColor(reading.getColor());

        return listViewItem;
    }
}
