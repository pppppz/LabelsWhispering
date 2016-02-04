package com.app.labelswhispering.Service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.labelswhispering.R;
import com.skyfishjy.library.RippleBackground;
import com.victor.ringbutton.RingButton;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Random;

public class AlarmActivity extends Activity {

    private static final String POSITION = "Position";
    private static final String ORIGINAL_VOLUME = "OriginalVolume";
    private final String TAG = AlarmActivity.class.getSimpleName();
    TextView tvListMedicineName;
    private Vibrator vibrator;
    private MediaPlayer m_MediaPlayer;
    private int m_iPosition;
    private int m_iOriginalAlarmVolume;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

  /*      final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_Alarm_Activity);
        relativeLayout.setBackground(wallpaperDrawable);
*/
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setTextName();
        initButtons();

        m_iPosition = 0;
        m_iOriginalAlarmVolume = -1;
        m_MediaPlayer = null;
    }

    @Override
    protected void onResume() {

        prepareSound();
        if (m_iPosition != 0) {
            m_MediaPlayer.seekTo(m_iPosition);
        }
        if (sharedPrefs.getBoolean("vibrate", false)) {
            startVibrate();
        }
        m_MediaPlayer.start();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        m_MediaPlayer.pause();
        m_iPosition = m_MediaPlayer.getCurrentPosition();
        outState.putInt(POSITION, m_iPosition);
        outState.putInt(ORIGINAL_VOLUME, m_iOriginalAlarmVolume);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPosition = savedInstanceState.getInt(POSITION);
        m_iOriginalAlarmVolume = savedInstanceState.getInt(ORIGINAL_VOLUME);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (m_iOriginalAlarmVolume != -1) {
            final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, m_iOriginalAlarmVolume, 0);
        }
        if (vibrator != null) {
            stopVibrate();
        }
        stopAndReleaseMediaPlayer();
        super.onDestroy();
    }

    private void startVibrate() {
        // The "0" means to repeat the pattern starting at the beginning
        // CUIDADO: If you start at the wrong index (e.g., 1) then your pattern will be off --
        // You will vibrate for your pause times and pause for your vibrate times !
        long pattern[] = {0, 100, 200, 300, 400};
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);
    }

    private void stopVibrate() {
        vibrator.cancel();
    }

    private void setTextName() {

        Intent intent = getIntent();
        String title = intent.getStringExtra("textName");
        tvListMedicineName = (TextView) findViewById(R.id.textViewCurrentTime);
        if (title != null) {
            tvListMedicineName.setText(title);
        } else {
            tvListMedicineName.setText(null);
        }

    }

    private void initButtons() {

        RingButton ringButton = (RingButton) findViewById(R.id.ringButton);
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        ringButton.setOnClickListener(new RingButton.OnClickListener() {
            @Override
            public void clickUp() {
                //Toast.makeText(getApplicationContext(), "Click up", Toast.LENGTH_SHORT).show();
                rippleBackground.stopRippleAnimation();
                m_MediaPlayer.stop();
                makeAlarmLater();
                finish();
            }

            @Override
            public void clickDown() {
                //Toast.makeText(getApplicationContext(), "Click down", Toast.LENGTH_SHORT).show();
                rippleBackground.stopRippleAnimation();
                m_MediaPlayer.stop();
                finish();
            }
        });
    }

    private void makeAlarmLater() {

        //make id
        Random r = new Random();
        int id = r.nextInt();

        DateTime time = new DateTime();
        long now = time.getMillis();
        String partOfDay = "null";

        if (now >= time.withHourOfDay(5).getMillis() && now < time.withHourOfDay(12).getMillis()) {
            partOfDay = "morning";
        } else if (now >= time.withHourOfDay(12).getMillis() && now < time.withHourOfDay(17).getMillis()) {
            partOfDay = "afternoon";
        } else if (now >= time.withHourOfDay(17).getMillis()) {//&& now < time.withHourOfDay(21).getMillis()){
            partOfDay = "evening";
        }/*else if (now >= time.withHourOfDay(21).getMillis()){
            partOfDay = "bedtime";
        }*/ else {
            Log.e(TAG, "Something was wrong");
        }

        //get gap time for snooze later
        String snoozeLaterKey = getString(R.string.PreferenceSnoozeLater);
        int timeInMinutes = Integer.parseInt(sharedPrefs.getString(snoozeLaterKey, "2"));
        long timeInMills = System.currentTimeMillis() + timeInMinutes * 60000;
        //make alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra("timeInMillis", timeInMills);
        intent.putExtra("partOfDay", partOfDay);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent);
        Toast.makeText(this, "Snooze in " + timeInMinutes + " minutes later. id = " + id, Toast.LENGTH_LONG).show();
        Log.e(TAG, "made alarm later");
    }

    private void prepareSound() {
        SharedPreferences getAlarms = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //String str = getString(R.string.PreferenceAlarmNoiseKey);
        String alarms = getAlarms.getString("ringtone", android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString());
        Uri uri = Uri.parse(alarms);
        stopAndReleaseMediaPlayer();
        m_MediaPlayer = new MediaPlayer();
        try {
            m_MediaPlayer.setDataSource(this, uri);
            final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int iVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            if (iVolume <= 0) {
                // if we are on silent mode, we'll change the volume and revert when finished.
                m_iOriginalAlarmVolume = iVolume;
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
            }
            m_MediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            m_MediaPlayer.prepare();
            m_MediaPlayer.setLooping(true);
        } catch (IOException e) {
            Log.e(TAG, "Problem playing the selected sound");
        }
    }

    private void stopAndReleaseMediaPlayer() {
        if (m_MediaPlayer == null) {
            return;
        }
        if (m_MediaPlayer.isPlaying()) {
            m_MediaPlayer.stop();
        }
        m_MediaPlayer.release();
        m_MediaPlayer = null;
    }
}
