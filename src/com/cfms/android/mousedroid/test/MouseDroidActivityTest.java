package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.MouseDroidActivity;

public class MouseDroidActivityTest extends
		ActivityInstrumentationTestCase2<MouseDroidActivity> {

	MouseDroidActivity mActivity;
	
	public MouseDroidActivityTest()
	{
		super(MouseDroidActivity.class);
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
    	assertEquals("MouseDroidActivity", mActivity.getTag());
    	
    }
    
}
