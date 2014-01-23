package com.cfms.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.mousedroid.activity.SettingsActivity;

public class ConfigTest extends
		ActivityInstrumentationTestCase2<SettingsActivity> {

	SettingsActivity mActivity;
	
	public ConfigTest()
	{
		super(SettingsActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testApplicationInitializedProperly(){
    	assertEquals(true, com.cfms.mousedroid.MouseDroidApplication.customApplicationInitialized);
    }
}
