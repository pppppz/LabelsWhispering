package com.app.labelswhispering;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.app.labelswhispering.Adapter.PagerAdapter;
import com.app.labelswhispering.service.MyAlarmReceiver;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity_Backup extends AppCompatActivity {

    public static Toolbar toolbar;
    public static FloatingActionButton fabBtn;
    public static CoordinatorLayout rootLayout;
    public static ProgressBar progressBar;
    public static ArrayList<PendingIntent> intentArray;
    public static AlarmManager[] alarmManager;
    FloatingActionButton.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity_Backup.this, AddSchedule_Activity.class);
            startActivity(intent);
        }
    };
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private SharedPreferences sharedPrefs;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            resetAlarms();
        }
    };
    private String[] toolbar_items;
    private String TAG = MainActivity_Backup.class.getSimpleName();

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume start");

        /** when return to this view  it's will show add schedule button (1 = tab schedule , 0 = home) */
        if (tabLayout.getSelectedTabPosition() == 1) {
            fabBtn.setVisibility(View.VISIBLE);
        } else {
            fabBtn.setVisibility(View.INVISIBLE);
        }
        Log.e(TAG, "onResume finished");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        fragmentManager = getSupportFragmentManager();
        parseLogIn();
        checkLanguage();
        LoadUI();
        setPreference();
        scheduleAlarm();

    }

    private void checkLanguage() {
        Locale locale;
        String locale_lang = Locale.getDefault().getDisplayLanguage();
        Log.e(TAG, "Lang : " + locale_lang);
        if (locale_lang.equals("th")) {
            locale = new Locale("th", "TH");
            toolbar_items = getResources().getStringArray(R.array.toolbar_items_th);
        } else {
            locale = Locale.US;
            toolbar_items = getResources().getStringArray(R.array.toolbar_items);
        }
        Locale.setDefault(locale);
    }

    private void setPreference() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    private void LoadUI() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        }
        //Progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        /**floating button**/
        fabBtn = (FloatingActionButton) findViewById(R.id.Fab_Event);
        fabBtn.setOnClickListener(fabOnClick);
        fabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_normal_color)));
        fabBtn.setRippleColor(getResources().getColor(R.color.fab_pressed_color));


        //set tab
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_medicine_box_gray_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_schedule_gray_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu_gray_24dp));

        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            private TypedArray imgWhite = getResources().obtainTypedArray(R.array.white_icons);
            private TypedArray imgGray = getResources().obtainTypedArray(R.array.gray_icons);

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                toolbar.setTitle(toolbar_items[position]);

                if (position == 1) {
                    fabBtn.show();
                }
                tab.setIcon(imgWhite.getResourceId(position, 0));
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                fabBtn.hide();
                tab.setIcon(imgGray.getResourceId(position, 0));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void parseLogIn() {

        // get user data if null go to login
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
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

