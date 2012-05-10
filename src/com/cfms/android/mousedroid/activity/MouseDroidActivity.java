package com.cfms.android.mousedroid.activity;

import android.os.Bundle;

import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;

public class MouseDroidActivity extends BluetoothActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private final String TAG = "MouseDroidActivity";
	@Override
	public String getTag() {
		return TAG;
	}
}