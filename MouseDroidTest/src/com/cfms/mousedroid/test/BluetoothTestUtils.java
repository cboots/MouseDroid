package com.cfms.mousedroid.test;

import android.bluetooth.BluetoothAdapter;

public class BluetoothTestUtils {

	
	public static boolean isBluetoothEnabled(){
		return BluetoothAdapter.getDefaultAdapter().isEnabled();
	}
//	
//	public static void ensureBluetoothEnabled() throws Exception{
//
//        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(btAdapter.isEnabled())
//        	return;
//        
//        if(btAdapter.enable()){
//        	
//        	
//        	
//        }else{
//        	throw new Exception("Bluetooth Not Available");
//        }
//	}
}
