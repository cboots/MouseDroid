package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.PreferencesActivity;

public class ConfigTest extends
		ActivityInstrumentationTestCase2<PreferencesActivity> {

	PreferencesActivity mActivity;
	
	public ConfigTest()
	{
		super(PreferencesActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testApplicationInitializedProperly(){
    	assertEquals(true, com.cfms.android.mousedroid.MouseDroidApplication.customApplicationInitialized);
    }
}
