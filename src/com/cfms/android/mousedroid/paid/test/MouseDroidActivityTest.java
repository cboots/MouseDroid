package com.cfms.android.mousedroid.paid.test;

import com.cfms.android.mousedroid.MouseDroidActivity;

import android.test.ActivityInstrumentationTestCase2;

public class MouseDroidActivityTest extends
		ActivityInstrumentationTestCase2<MouseDroidActivity> {

	MouseDroidActivity mActivity;
	
	public MouseDroidActivityTest()
	{
		super("com.cfms.android.mousedroid.paid", MouseDroidActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    	
    }
    
    public void testAppName(){
    	String app_name = mActivity.getString(com.cfms.android.mousedroid.paid.R.string.app_name);
    	assertEquals("Mouse Droid Pro", app_name);
    }
    
    public void testAppPackage(){
    	assertEquals(mActivity.getApplicationInfo().packageName, "com.cfms.android.mousedroid.paid");
    }
	
}
