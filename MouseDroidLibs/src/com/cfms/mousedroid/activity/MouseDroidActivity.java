package com.cfms.mousedroid.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.bluetooth.BluetoothActivity;

public class MouseDroidActivity extends BluetoothActivity {
	

	protected static final int REQUEST_CONNECT_DEVICE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	SettingsActivity.InitDefaultPreferences(this, false);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mouse_droid_home);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(!isBoundToBTService()){
    		
    	}
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                getBTService().connect(device);
            }
            break;
           default:
               super.onActivityResult(requestCode, resultCode, data);
        	   break;
        }
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mouse_droid_home_menu, menu);
        return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
        if (id == R.id.scan) {
        	if(this.isBoundToBTService()){
            	if(getBTService().isConnected()){
            		getBTService().disconnect();
            	}
            }
			// Launch the DeviceListActivity to see devices and do scan
        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		}
        return false;
    }


	public void testButton(final View target) {
        Intent i = new Intent(this, TrackpadActivity.class);
        startActivity(i);
    }


	public void testButton2(final View target) {
        Intent i = new Intent(this, NumpadActivity.class);
        startActivity(i);
    }

	public void testButton3(final View target) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
	
	public void testButton4(final View target) {
        Intent i = new Intent(this, MouseActivity.class);
        startActivity(i);
    }
	
    private final String TAG = "MouseDroidActivity";
	@Override
	public String getTag() {
		return TAG;
	}
}