package com.cfms.mousedroid.free.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.mousedroid.activity.SettingsActivity;

public class AppVersionTest extends
		ActivityInstrumentationTestCase2<SettingsActivity> {

	SettingsActivity mActivity;
	
	public AppVersionTest()
	{
		super(SettingsActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testAppName(){
    	String app_name = mActivity.getString(com.cfms.mousedroid.paid.R.string.app_name);
    	assertEquals("Mouse Droid", app_name);
    }
    
    public void testAppPackage(){
    	assertEquals(mActivity.getApplicationInfo().packageName, "com.cfms.mousedroid.free");
    }
    
    public void testConfigIsPaidVersion(){
    	assertEquals(false, com.cfms.mousedroid.utils.Configuration.isPaidVersion(mActivity));
    }
    
}
