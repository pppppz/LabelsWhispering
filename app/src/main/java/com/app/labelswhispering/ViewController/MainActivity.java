package com.app.labelswhispering.viewcontroller;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.app.labelswhispering.R;
import com.app.labelswhispering.viewcontroller.services.MyAlarmReceiver;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // public static Toolbar toolbar;
    public static ArrayList<PendingIntent> intentArray;
    public static AlarmManager[] alarmManager;
    public static CoordinatorLayout rootLayout;
    LinearLayout.OnClickListener onClickListener_search = new LinearLayout.OnClickListener() {
        @Override
        public void onClick(View v) {

            final CharSequence[] items = {getString(R.string.type_or_voice), getString(R.string.barcode), getString(R.string.ocr)};

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.search_by));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    Intent intent = null;
                    switch (item) {
                        case 0:
                            intent = new Intent(MainActivity.this, SearchActivity.class);
                            break;
                        case 1:
                            intent = new Intent(MainActivity.this, ScanBarcode.class);
                            break;
                        case 2:
                            intent = new Intent(MainActivity.this, ScanOCR.class);
                            break;
                    }
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    };
    LinearLayout.OnClickListener onClickListener_medicineBox = new LinearLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MedicineBox.class);
            startActivity(intent);
        }
    };
    LinearLayout.OnClickListener onClickListener_schedule = new LinearLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        }
    };
    LinearLayout.OnClickListener onClickListener_settings = new LinearLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    };
    private SharedPreferences sharedPrefs;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            resetAlarms();
        }
    };
    private ParseUser parseUser;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        parseLogIn();
        init();
        setPreference();
        if (parseUser != null) {
            scheduleAlarm();
        }
    }

    private void setPreference() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    private void init() {

        rootLayout = (CoordinatorLayout) findViewById(R.id.main_activity_rootLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle(getString(R.string.app_name));
        }

        //Progress bar
        //  progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //bind linear layout
        LinearLayout linearLayout_search = (LinearLayout) findViewById(R.id.ll_search);
        LinearLayout linearLayout_medicineBox = (LinearLayout) findViewById(R.id.ll_drug);
        LinearLayout linearLayout_schedule = (LinearLayout) findViewById(R.id.ll_schedule);
        LinearLayout linearLayout_settings = (LinearLayout) findViewById(R.id.ll_settings);

        //set listener for linear layout
        linearLayout_search.setOnClickListener(onClickListener_search);
        linearLayout_medicineBox.setOnClickListener(onClickListener_medicineBox);
        linearLayout_schedule.setOnClickListener(onClickListener_schedule);
        linearLayout_settings.setOnClickListener(onClickListener_settings);
    }

    private void parseLogIn() {

        // get user data if null go to login
        parseUser = ParseUser.getCurrentUser();
        if (parseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //parse analytic is track
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    //make alarm by the each time of morning to bedtime = 7 times (before meal and after meal)
    public void scheduleAlarm() {
        int space = 7;
        alarmManager = new AlarmManager[space];
        intentArray = new ArrayList<>();
        for (int i = 0; i < space; ++i) {
            Intent intent = new Intent(this, MyAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
            alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[i].set(AlarmManager.RTC_WAKEUP, calculateTriggerTime(i), pendingIntent);
            intentArray.add(pendingIntent);
            // Log.e(TAG ,"in loop alarm " + i + "time = " + (((calculateTriggerTime(i)-System.currentTimeMillis())/60000)) + " minutes");
        }
    }

    public void resetAlarms() {
        if (intentArray.size() > 0) {
            for (int i = 0; i < intentArray.size(); i++) {
                alarmManager[i].cancel(intentArray.get(i));
            }
            intentArray.clear();
        }
        scheduleAlarm();
    }

    private long calculateTriggerTime(int count) {

        DateTime now = new DateTime();
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
        DateTime Morning = now.withHourOfDay(morning_hour).withMinuteOfHour(morning_minute);
        DateTime Noon = now.withHourOfDay(noon_hour).withMinuteOfHour(noon_minute);
        DateTime Evening = now.withHourOfDay(evening_hour).withMinuteOfHour(evening_minute);
        DateTime Bedtime = now.withHourOfDay(bedtime_hour).withMinuteOfHour(bedtime_minute);
        long triggerTime = 0;

        if (count == 0) {

            triggerTime = Morning.getMillis() - 1800000;
        } else if (count == 1) {
            triggerTime = Morning.getMillis() + 1800000;
        } else if (count == 2) {
            triggerTime = Noon.getMillis() - 1800000;
        } else if (count == 3) {
            triggerTime = Noon.getMillis() + 1800000;
        } else if (count == 4) {

            triggerTime = Evening.getMillis() - 1800000;
        } else if (count == 5) {
            triggerTime = Evening.getMillis() + 1800000;
        } else if (count == 6) {
            triggerTime = Bedtime.getMillis() - 1800000;
        }

  /*      Log.e(TAG , "milli morning" + Morning.getMillis() );
        Log.e(TAG , "milli noon" + Noon.getMillis() );
        Log.e(TAG , "milli evening" + Evening.getMillis() );
        Log.e(TAG , "milli bedtime" + Bedtime.getMillis() );
*/


        return triggerTime;
    }

}

