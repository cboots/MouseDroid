package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.MotionMouseActivity;

public class MotionMouseActivityTest extends
		ActivityInstrumentationTestCase2<MotionMouseActivity> {

	MotionMouseActivity mActivity;
	
	public MotionMouseActivityTest()
	{
		super(MotionMouseActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	assertTrue("Bluetooth Must Be Enabled For These Tests To Run", BluetoothTestUtils.isBluetoothEnabled());
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    }
    

    public void testGetTag(){
    	assertEquals("MotionMouseActivity", mActivity.getTag());
    	
    }
}
