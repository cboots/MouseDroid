package com.cfms.android.mousedroid.activity;

import android.os.Bundle;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.TrackpadFragment.TrackpadListener;
import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;

public class TrackpadActivity extends BluetoothActivity implements TrackpadListener{

	private TrackpadFragment mTrackpadFragment;
	
	public TrackpadActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        setContentView(R.layout.trackpad_activity);
        
        mTrackpadFragment = (TrackpadFragment) getSupportFragmentManager().findFragmentById(
						R.id.trackpad_fragment);
        
        mTrackpadFragment.setTrackpadListener(this);
        
	}
	

	@Override
	public void onMouseMove(int dx, int dy) {
		if(isBoundToBTService()){
			if(getBTService().isConnected()){
					byte[] packet = BTProtocol.getMouseMovePacket((short)dx, (short)dy);
					getBTService().write(packet);	
			}
		}
	}

	@Override
	public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
		if(isBoundToBTService()){
			if(getBTService().isConnected()){
				
				byte[] packet = BTProtocol.getMouseButtonEventPacket(mb, mbe);
				
				getBTService().write(packet);
			}
		}
	}

	@Override
	public void onScrollEvent(int ticks) {
		if(isBoundToBTService()){
			if(getBTService().isConnected()){
				byte[] packet = BTProtocol.getMouseWheelEventPacket((short)ticks);
				
				getBTService().write(packet);
			}
		}
	}
	
	private String TAG = "TrackpadActivity";
	@Override
	public String getTag(){
		return TAG;
	}

}
