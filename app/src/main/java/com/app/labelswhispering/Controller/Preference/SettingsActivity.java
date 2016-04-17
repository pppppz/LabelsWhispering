package com.app.labelswhispering.Controller.Preference;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.app.labelswhispering.R;


public class SettingsActivity extends AppCompatActivity implements MyPreferenceFragment.Callback {

    // Preference keys not carried over from ZXing project
    public static final String KEY_SOURCE_LANGUAGE_PREFERENCE = "sourceLanguageCodeOcrPref";
    public static final String KEY_TOGGLE_TRANSLATION = "preference_translation_toggle_translation";
    public static final String KEY_CONTINUOUS_PREVIEW = "preference_capture_continuous";
    public static final String KEY_PAGE_SEGMENTATION_MODE = "preference_page_segmentation_mode";
    public static final String KEY_OCR_ENGINE_MODE = "preference_ocr_engine_mode";
    public static final String KEY_CHARACTER_BLACKLIST = "preference_character_blacklist";
    public static final String KEY_CHARACTER_WHITELIST = "preference_character_whitelist";
    public static final String KEY_TOGGLE_LIGHT = "preference_toggle_light";
    // Preference keys carried over from ZXing project
    public static final String KEY_AUTO_FOCUS = "preferences_auto_focus";
    public static final String KEY_DISABLE_CONTINUOUS_FOCUS = "preferences_disable_continuous_focus";
    public static final String KEY_HELP_VERSION_SHOWN = "preferences_help_version_shown";
    public static final String KEY_NOT_OUR_RESULTS_SHOWN = "preferences_not_our_results_shown";
    public static final String KEY_REVERSE_IMAGE = "preferences_reverse_image";
    public static final String KEY_PLAY_BEEP = "preferences_play_beep";
    public static final String KEY_VIBRATE = "preferences_vibrate";
    public static final String KEY_ALARM_SNOOZE = "snooze_duration";


    //Preference Alarms
    public static final String KEY_VOLUME_BEHAVIOR = "volume_button_setting";
    public static final String KEY_FADE_IN_TIME_SEC = "fade_in_time_sec";
    static final String KEY_DEFAULT_RINGTONE = "default_ringtone";
    static final String KEY_AUTO_SILENCE = "auto_silence";
    static final String KEY_PREALARM_DURATION = "prealarm_duration";
    private static final String TAG_NESTED = "TAG_NESTED";
    private static final int ALARM_STREAM_TYPE_BIT = 1 << AudioManager.STREAM_ALARM;
    private static final String KEY_ALARM_IN_SILENT_MODE = "alarm_in_silent_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.contentSettings, new MyPreferenceFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        // this if statement is necessary to navigate through nested and main fragments
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.contentSettings, NestedPreferenceFragment.newInstance(key), TAG_NESTED).addToBackStack(TAG_NESTED).commit();
    }

}