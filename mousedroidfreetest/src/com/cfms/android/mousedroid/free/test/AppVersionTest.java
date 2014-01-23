package com.cfms.android.mousedroid.free.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.MouseDroidActivity;

public class AppVersionTest extends
		ActivityInstrumentationTestCase2<MouseDroidActivity> {

	MouseDroidActivity mActivity;
	
	public AppVersionTest()
	{
		super(MouseDroidActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testAppName(){
    	String app_name = mActivity.getString(com.cfms.android.mousedroid.free.R.string.app_name);
    	assertEquals("Mouse Droid", app_name);
    }
    
    public void testAppPackage(){
    	assertEquals(mActivity.getApplicationInfo().packageName, "com.cfms.android.mousedroid.free");
    }
    
    public void testConfigIsPaidVersion(){
    	assertEquals(false, com.cfms.android.mousedroid.utils.Configuration.isPaidVersion(mActivity));
    }
    
}