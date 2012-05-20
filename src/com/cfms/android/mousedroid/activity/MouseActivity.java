package com.cfms.android.mousedroid.activity;

import android.os.Bundle;

import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;

public class MouseActivity extends BluetoothActivity{


	public MouseActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        setContentView(R.layout.mouse_activity);
	}
	
	
	private String TAG = "MouseActivity";
	@Override
	public String getTag(){
		return TAG;
	}
}
