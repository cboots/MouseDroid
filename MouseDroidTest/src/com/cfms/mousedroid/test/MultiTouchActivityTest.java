package com.cfms.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.mousedroid.bluetooth.BluetoothActivity;

public class MultiTouchActivityTest extends
		ActivityInstrumentationTestCase2<BluetoothActivity> {

	private BluetoothActivity mActivity;

	public MultiTouchActivityTest()
	{
		super(BluetoothActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	assertEquals(true, BluetoothTestUtils.isBluetoothEnabled());
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    }
    

    public void testGetTag(){
    	assertEquals("BluetoothActivity", mActivity.getTag());	
    }
    
    
}
