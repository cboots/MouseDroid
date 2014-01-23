package com.cfms.mousedroid.activity;

import android.os.Bundle;

import com.cfms.mousedroid.BTProtocol;
import com.cfms.mousedroid.BTProtocol.KeyEventType;
import com.cfms.mousedroid.R;
import com.cfms.mousedroid.bluetooth.BluetoothActivity;
import com.cfms.mousedroid.utils.DebugLog;
import com.cfms.mousedroid.view.KeyButton.KeyEventListener;

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
