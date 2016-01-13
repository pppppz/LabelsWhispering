package com.app.labelswhispering.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.app.labelswhispering.Model.Schedule;
import com.app.labelswhispering.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class ListScheduleAdapter extends RecyclerView.Adapter<ListScheduleAdapter.ViewHolder> {

    private List<Schedule> schedules;
    private Context mContext;
    private String objectId;
    private String TAG = ListScheduleAdapter.class.getSimpleName();

    public ListScheduleAdapter(Context context, List<Schedule> dataSet) {
        schedules = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_schedule, parent, false);
        return new ViewHolder(view);
    }

    private void setActivateToParse(final boolean isChecked) {


        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.getInBackground(objectId, new GetCallback<Schedule>() {
            @Override
            public void done(Schedule schedule, ParseException e) {
                if (e == null) {
                    schedule.put("Alert", isChecked);
                    schedule.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {

                            Log.e(TAG, "Alert work! ");

                        }
                    });

                } else {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Schedule schedule = schedules.get(position);
        viewHolder.titleTask.setText(schedule.getName());
        boolean activate = schedule.isAlert();
        objectId = schedule.getObjectId();
        viewHolder.toggleButton.setChecked(activate);
        viewHolder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setActivateToParse(isChecked);
            }
        });


        final int second_color = mContext.getResources().getColor(R.color.secondary_text);

        if (!schedule.isMorning()) {
            viewHolder.tvMorning.setTextColor(second_color);
        }

        if (!schedule.isNoon()) {
            viewHolder.tvNoon.setTextColor(second_color);
        }

        if (!schedule.isEvening()) {
            viewHolder.tvEvening.setTextColor(second_color);
        }

        if (!schedule.isBedtime()) {
            viewHolder.tvBedtime.setTextColor(second_color);
        }

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTask, tvMorning, tvNoon, tvEvening, tvBedtime;
        public SwitchCompat toggleButton;


        public ViewHolder(View view) {
            super(view);
            titleTask = (TextView) view.findViewById(R.id.tv_name);
            toggleButton = (SwitchCompat) view.findViewById(R.id.toggleSchedule);
            tvMorning = (TextView) view.findViewById(R.id.tvMorning);
            tvNoon = (TextView) view.findViewById(R.id.tvNoon);
            tvEvening = (TextView) view.findViewById(R.id.tvEvening);
            tvBedtime = (TextView) view.findViewById(R.id.tvBedtime);
        }
    }
}