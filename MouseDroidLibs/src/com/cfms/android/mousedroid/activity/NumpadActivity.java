package com.cfms.android.mousedroid.activity;

import android.os.Bundle;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;
import com.cfms.android.mousedroid.utils.DebugLog;
import com.cfms.android.mousedroid.view.KeyButton.KeyEventListener;

public class NumpadActivity extends BluetoothActivity implements KeyEventListener {

	private boolean D = true;
	private NumpadFragment mNumpadFragment;
	
	public NumpadActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        setContentView(R.layout.numpad_activity);
        
        mNumpadFragment = (NumpadFragment) getSupportFragmentManager().findFragmentById(
						R.id.numpad_fragment);
        
        mNumpadFragment.setKeyEventListener(this);
        
	}
	

	private String TAG = "NumpadActivity";
	@Override
	public String getTag(){
		return TAG;
	}

	@Override
	public void onKeyEvent(int keyCode, KeyEventType type) {
		if(D) DebugLog.D(getTag(), "onKeyEvent: " + keyCode + " " + type);
		if(isBoundToBTService()){
			if(getBTService().isConnected()){
				byte[] packet = BTProtocol.getKeyEventPacket(type, keyCode);
				
				getBTService().write(packet);
			}
		}
	}

}
