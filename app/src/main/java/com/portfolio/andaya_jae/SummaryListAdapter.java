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

public class SummaryListAdapter extends ArrayAdapter<Summary> {

    private Activity context;
    private List<Summary> summaryList;

    public SummaryListAdapter(Activity context, List<Summary> summaryList) {
        super(context, R.layout.summary_list_layout, summaryList);
        this.context = context;
        this.summaryList = summaryList;
    }

    public SummaryListAdapter(Activity context, int resource, List<Summary> objects, Activity context1, List<Summary> summaryList) {
        super(context, resource, objects);
        this.context = context1;
        this.summaryList = summaryList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.summary_list_layout, null, true);

        TextView tvSummaryUserValue = listViewItem.findViewById(R.id.tvSummaryUserValue);
        TextView tvSummarySystolicValue = listViewItem.findViewById(R.id.tvSummarySystolicValue);
        TextView tvSummaryDiastolicValue = listViewItem.findViewById(R.id.tvSummaryDiastolicValue);
        TextView tvSummaryConditionValue = listViewItem.findViewById(R.id.tvSummaryConditionValue);

        Summary summary = summaryList.get(position);

        tvSummaryUserValue.setText(summary.getUserID());
        tvSummarySystolicValue.setText(summary.getSystolic().toString());
        tvSummaryDiastolicValue.setText(summary.getDiastolic().toString());
        tvSummaryConditionValue.setText(summary.getCondition());
        listViewItem.setBackgroundColor(summary.getColor());

        return listViewItem;
    }
}
