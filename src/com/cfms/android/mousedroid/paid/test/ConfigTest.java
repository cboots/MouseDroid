package com.cfms.android.mousedroid.paid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.MouseDroidActivity;

public class ConfigTest extends
		ActivityInstrumentationTestCase2<MouseDroidActivity> {

	MouseDroidActivity mActivity;
	
	public ConfigTest()
	{
		super(MouseDroidActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testAppName(){
    	String app_name = mActivity.getString(com.cfms.android.mousedroid.paid.R.string.app_name);
    	assertEquals("Mouse Droid Pro", app_name);
    }
    
    public void testAppPackage(){
    	assertEquals(mActivity.getApplicationInfo().packageName, "com.cfms.android.mousedroid.paid");
    }
    
    public void testConfigIsPaidVersion(){
    	assertEquals(true, com.cfms.android.mousedroid.utils.Configuration.isPaidVersion(mActivity));
    }
    
    public void testApplicationInitializedProperly(){
    	assertEquals(true, com.cfms.android.mousedroid.MouseDroidApplication.customApplicationInitialized);
    }
}
