package com.app.labelswhispering.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.labelswhispering.MainActivity;
import com.app.labelswhispering.Model.Schedule;
import com.app.labelswhispering.More_detail;
import com.app.labelswhispering.NotificationActivity;
import com.app.labelswhispering.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

public class CheckScheduleService extends IntentService {

    private final String TAG = CheckScheduleService.class.getSimpleName();
    private List<Schedule> scheduleList = new ArrayList<>();
    private int EatTimeCondition;
    private long timeInMillis;
    private String partOfDay;
    private Intent it;
    private SharedPreferences sharedPrefs;

    public CheckScheduleService() {
        super("CheckScheduleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("CheckScheduleService", "Service running");
        getExtra(intent);
        checkTimeCondition();
        queryBy();
    }

    private void getExtra(Intent intent) {
        it = intent;
        if (it.getExtras() != null) {
            timeInMillis = it.getLongExtra("timeInMillis", 0);
            partOfDay = it.getStringExtra("partOfDay");
            Log.e(TAG, "timeInMillis = " + timeInMillis + "partOfDay = " + partOfDay);
        } else {
            Log.e(TAG, "getExtra null");
        }
    }


    private void queryBy() {
        // check internet stated if working query online else query offline
        if (isOnline()) {
            queryOnline();
        } else {
            queryOffline();
        }
    }


    private void createAlert() {
        Log.e(TAG, "checkAlert()");

        int i, getNumberOfRow = scheduleList.size();
        Log.e(TAG, "Number of row : " + getNumberOfRow);
        Schedule schedule;
        String EatTimeConditionTEXT;

        if (EatTimeCondition == 1 || EatTimeCondition == 2) {
            EatTimeConditionTEXT = "morning";
        } else if (EatTimeCondition == 3 || EatTimeCondition == 4) {
            EatTimeConditionTEXT = "afternoon";
        } else if (EatTimeCondition == 5 || EatTimeCondition == 6) {
            EatTimeConditionTEXT = "evening";
        } else {
            EatTimeConditionTEXT = "bedtime";
        }
        if (getNumberOfRow != 0) {
            for (i = 0; i < getNumberOfRow; i++) {
                schedule = scheduleList.get(i);
                if (schedule.isAlert()) {
                    if (EatTimeCondition == 1 && schedule.isBeforeMeal() && schedule.isMorning()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and before " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 2 && schedule.isAfterMeal() && schedule.isMorning()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and after " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 3 && schedule.isBeforeMeal() && schedule.isNoon()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and before " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 4 && schedule.isAfterMeal() && schedule.isNoon()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and after " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 5 && schedule.isBeforeMeal() && schedule.isEvening()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and before " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 6 && schedule.isAfterMeal() && schedule.isEvening()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and after " + EatTimeConditionTEXT);
                    } else if (EatTimeCondition == 7 && schedule.isBeforeMeal() && schedule.isBedtime()) {
                        createNotification(i, schedule.getName(), "Take " + schedule.getAmount() + " tablet and before " + EatTimeConditionTEXT);
                        //  bedtime_alert = true;
                    } else {
                        Log.e(TAG, "not in condition");
                    }
                    Log.e(TAG, "------------------end loop------------------");
                }
            }
        }
    }


    private void checkTimeCondition() {

        /** get Data of time from preference for check condition **/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        DateTime Now = new DateTime();

        String[] key_Morning = sharedPrefs.getString("timeMorning_Key", "07:00").split(":");
        int morning_hour = Integer.parseInt(key_Morning[0]);
        int morning_minute = Integer.parseInt(key_Morning[1]);

        String[] key_Noon = sharedPrefs.getString("timeNoon_Key", "10:00").split(":");
        int noon_hour = Integer.parseInt(key_Noon[0]);
        int noon_minute = Integer.parseInt(key_Noon[1]);
        String[] key_Evening = sharedPrefs.getString("timeEvening_Key", "17:00").split(":");
        int evening_hour = Integer.parseInt(key_Evening[0]);
        int evening_minute = Integer.parseInt(key_Evening[1]);

        String[] key_Bedtime = sharedPrefs.getString("timeBedtime_Key", "21:00").split(":");
        int bedtime_hour = Integer.parseInt(key_Bedtime[0]);
        int bedtime_minute = Integer.parseInt(key_Bedtime[1]);


        /** set Datetime from preference **/
        DateTime Morning = null;
        DateTime Noon = null;
        DateTime Evening = null;
        DateTime Bedtime = null;
        if (timeInMillis > 0) {
            Log.e(TAG, "alertAgain");
            switch (partOfDay) {
                case "morning":
                    Morning = Now.withMillis(timeInMillis);
                    break;
                case "afternoon":
                    Noon = Now.withMillis(timeInMillis);
                    break;
                case "evening":
                    Evening = Now.withMillis(timeInMillis);
                    break;
                case "bedtime":
                    Bedtime = Now.withMillis(timeInMillis);
                    break;
                default:
                    Log.e(TAG, "something was wrong");
                    break;
            }
        } else {
            Log.e(TAG, "not alertAgain");
            Morning = Now.withHourOfDay(morning_hour).withMinuteOfHour(morning_minute);
            Noon = Now.withHourOfDay(noon_hour).withMinuteOfHour(noon_minute);
            Evening = Now.withHourOfDay(evening_hour).withMinuteOfHour(evening_minute);
            Bedtime = Now.withHourOfDay(bedtime_hour).withMinuteOfHour(bedtime_minute);
        }

        /** convert duration into minute (for easy to compare) **/
        long _duration_morning = new Duration(Now, Morning).getStandardMinutes();
        long _duration_noon = new Duration(Now, Noon).getStandardMinutes();
        long _duration_evening = new Duration(Now, Evening).getStandardMinutes();
        long _duration_bedtime = new Duration(Now, Bedtime).getStandardMinutes();

        Log.e(TAG, "duration morning " + _duration_morning);
        Log.e(TAG, "duration noon " + _duration_noon);
        Log.e(TAG, "duration evening " + _duration_evening);
        Log.e(TAG, "duration bedtime " + _duration_bedtime);

        /** check alert time & reset alert time**/

        /** if morning */
        if (_duration_morning == 0) {
            Log.e(TAG, "in Morning before " + _duration_morning);
            EatTimeCondition = 1;
        } else if (_duration_morning == -30) {
            Log.e(TAG, "in Morning after " + _duration_morning);
            EatTimeCondition = 2;

            /** if noon */
        } else if (_duration_noon == 0) {
            Log.e(TAG, "in Noon before" + _duration_noon);
            EatTimeCondition = 3;
        } else if (_duration_noon == -30) {
            Log.e(TAG, "in Noon after " + _duration_noon);
            EatTimeCondition = 4;
        }

        /** if evening */
        else if (_duration_evening == 0) {
            Log.e(TAG, "In Evening before" + _duration_evening);
            EatTimeCondition = 5;
        } else if (_duration_evening == -30) {
            Log.e(TAG, "In Evening after" + _duration_evening);
            EatTimeCondition = 6;
        }

        /** if bedtime **/
        else if (_duration_bedtime == 30) {
            Log.e(TAG, "In Bedtime" + _duration_bedtime);
            EatTimeCondition = 7;
        }
    }

    private void createNotification(int nId, String title, String body) {
        Log.e(TAG, "create notification id:" + nId);
        String notificationKey = getString(R.string.PreferenceShowNotificationKey);
        boolean makeNotification = sharedPrefs.getBoolean(notificationKey, false);
        if (makeNotification) {
            /** Done intent*/
            Intent doneIntent = new Intent(this, MainActivity.class);
            doneIntent.putExtra("notificationId", nId);
            PendingIntent dIntent = NotificationActivity.getDismissIntent(nId, this);

            /** More intent */
            Intent more_intent = new Intent(this, More_detail.class);
            //PendingIntent mIntent = PendingIntent.getActivity(this, 0 , more_intent, PendingIntent.FLAG_CANCEL_CURRENT);
            more_intent.putExtra("notificationId", nId);
            PendingIntent mIntent = More_detail.getDismissIntent(nId, this);

            /**Go to main intent*/
            Intent goMain_intent = new Intent(this, MainActivity.class);
            PendingIntent sIntent = PendingIntent.getActivity(this, nId, goMain_intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.ic_alarm_add_white_24dp)
                    .setContentTitle("It's time to take " + title + ".")
                    .setContentText(body)
                    .addAction(R.drawable.ic_done_white_24dp, "Done", dIntent)
                    .addAction(R.drawable.ic_more_white_24dp, "More Detail", mIntent)
                    .setContentIntent(sIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(nId, mBuilder.build());
            Log.e(TAG, " -------end------- clear noti Id:" + nId);
        }

        //create Alert fullscreen
        wakeUpAndSound(title);


    }

    private void wakeUpAndSound(String body) {
        Log.e(TAG, "text send to alarm activity = " + body);
        it.removeExtra("timeInMillis");
        it.removeExtra("partOfDay");
        Intent intentAlarm = new Intent(this, AlarmActivity.class);
        intentAlarm.putExtra("textName", body);
        intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentAlarm);
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    private void queryOffline() {

        Log.e(TAG, "internet is offline , queryOffline();");
        //pattern query
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> items, ParseException error) {
                //check if list task have some data = clear
                scheduleList.clear();
                if (error == null) {
                    int size = items.size();
                    for (int i = 0; i < size; i++) {
                        scheduleList.add(items.get(i));
                    }
                    Log.e(TAG, "Size in queryOnline : " + size);
                } else {
                    Log.e(TAG, String.valueOf(error));
                }
                createAlert();
            }
        });
    }

    private void queryOnline() {

        Log.e(TAG, "internet is working , queryOnline();");

        //pattern query
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> items, ParseException error) {
                //check if list task have some data = clear
                scheduleList.clear();
                if (error == null) {
                    int size = items.size();
                    for (int i = 0; i < size; i++) {
                        scheduleList.add(items.get(i));
                    }
                    Log.e(TAG, "Size in queryOnline : " + size);
                } else {
                    Log.e(TAG, String.valueOf(error));
                }
                ParseObject.pinAllInBackground(items);
                createAlert();
            }
        });
    }
}


