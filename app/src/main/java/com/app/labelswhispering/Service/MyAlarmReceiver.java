package com.app.labelswhispering.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        long timeInMillis;
        String partOfDay;
        Intent i = new Intent(context, CheckScheduleService.class);
        if (intent.getExtras() != null) {
            timeInMillis = intent.getLongExtra("timeInMillis", 0);
            partOfDay = intent.getStringExtra("partOfDay");
            i.putExtra("timeInMillis", timeInMillis);
            i.putExtra("partOfDay", partOfDay);
        }
        context.startService(i);
    }
}