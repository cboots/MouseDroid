package com.cfms.mousedroid.activity;

import android.os.Bundle;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.bluetooth.BluetoothActivity;

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
