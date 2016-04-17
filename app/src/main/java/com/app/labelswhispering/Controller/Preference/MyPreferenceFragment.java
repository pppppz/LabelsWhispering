package com.app.labelswhispering.Controller.Preference;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.app.labelswhispering.R;

public class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String KEY_1 = "TIME_KEY";
    private static final String KEY_2 = "OCR_KEY";
    private Callback mCallback;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException("Owner must implement URLCallback interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // add listeners for non-default actions
        Preference preference = findPreference(KEY_1);
        preference.setOnPreferenceClickListener(this);

        preference = findPreference(KEY_2);
        preference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(KEY_1)) {
            mCallback.onNestedPreferenceSelected(NestedPreferenceFragment.NESTED_SCREEN_1_KEY);
        }

        if (preference.getKey().equals(KEY_2)) {
            mCallback.onNestedPreferenceSelected(NestedPreferenceFragment.NESTED_SCREEN_2_KEY);
        }

        return false;
    }

    public interface Callback {
        void onNestedPreferenceSelected(int key);
    }
}