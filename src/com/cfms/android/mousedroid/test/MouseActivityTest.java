package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.MouseActivity;

public class MouseActivityTest extends
		ActivityInstrumentationTestCase2<MouseActivity> {

	MouseActivity mActivity;
	
	public MouseActivityTest()
	{
		super(MouseActivity.class);
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
    	assertEquals("MouseActivity", mActivity.getTag());
    	
    }
}
