package com.cfms.android.mousedroid.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cfms.android.mousedroid.R;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
