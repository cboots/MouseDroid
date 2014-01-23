package com.cfms.android.mousedroid.test;

import junit.framework.Assert;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.PreferencesActivity;

public class PreferencesActivityTest extends
		ActivityInstrumentationTestCase2<PreferencesActivity> {

	PreferencesActivity mActivity;
	
	public PreferencesActivityTest()
	{
		super(PreferencesActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    	
    }
    
    public void testGetTag(){
    	assertEquals("PreferencesActivity", mActivity.getTag());
    }
    
    public void testGetBoolValid(){
    	boolean booltest1 = PreferencesActivity.getBoolean(mActivity, "test_default_bool_true");
    	boolean booltest2 = PreferencesActivity.getBoolean(mActivity, "test_default_bool_false");
    	
    	assertEquals(true, booltest1);
    	assertEquals(false, booltest2);
    	
    }
    
    public void testGetIntegerValid(){
    	int intTest1 = PreferencesActivity.getInteger(mActivity, "test_default_int_1");
    	int intTest2 = PreferencesActivity.getInteger(mActivity, "test_default_int_2");
    	
    	assertEquals(1, intTest1);
    	assertEquals(2, intTest2);
    	
    }

    public void testGetStringValid(){
    	String stringtest1 = PreferencesActivity.getString(mActivity, "test_default_string_test1");
    	String stringtest2 = PreferencesActivity.getString(mActivity, "test_default_string_test2");
    	
    	assertEquals("test1", stringtest1);
    	assertEquals("test2", stringtest2);
    	
    }
    
    
    public void testGetBoolInvalid(){
    	try{
        	PreferencesActivity.getBoolean(mActivity, "invalid_bool_preference");
    		Assert.fail("Should have thrown Resources.NotFoundException");
    	}catch(Resources.NotFoundException  ex){
    		//Success
    	}
    }
    
    public void testGetStringInvalid(){
    	try{
        	PreferencesActivity.getString(mActivity, "invalid_string_preference");
    		Assert.fail("Should have thrown Resources.NotFoundException");
    	}catch(Resources.NotFoundException  ex){
    		//Success
    	}
    }
    
    public void testGetIntegerInvalid(){
    	try{
        	PreferencesActivity.getInteger(mActivity, "invalid_integer_preference");
    		Assert.fail("Should have thrown Resources.NotFoundException");
    	}catch(Resources.NotFoundException  ex){
    		//Success
    	}
    }
    
    public void testEditor(){
    	PreferencesActivity.GetEditor(mActivity).putInt("test_change_int", 10).commit();
    	assertEquals(10, PreferencesActivity.getInteger(mActivity, "test_change_int"));
    	
    	PreferencesActivity.GetEditor(mActivity).putInt("test_change_int", 20).commit();
    	assertEquals(20, PreferencesActivity.getInteger(mActivity, "test_change_int"));
    }
}
