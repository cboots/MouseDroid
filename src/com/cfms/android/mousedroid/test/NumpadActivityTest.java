package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.KeyCode;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.NumpadActivity;
import com.cfms.android.mousedroid.activity.NumpadFragment;
import com.cfms.android.mousedroid.view.KeyButton;

public class NumpadActivityTest extends
		ActivityInstrumentationTestCase2<NumpadActivity> {

	NumpadActivity mActivity;
	NumpadFragment mNumpadFragment;

	public NumpadActivityTest() {
		super(NumpadActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
//		assertTrue("Bluetooth Must Be Enabled For These Tests To Run",
//				BluetoothTestUtils.isBluetoothEnabled());
		super.setUp();
		mActivity = this.getActivity();
		mNumpadFragment = (NumpadFragment) mActivity
				.getSupportFragmentManager().findFragmentById(
						R.id.numpad_fragment);

	}

	public void testPreconditions() {
		assertNotNull(mActivity);
		assertNotNull(mNumpadFragment);
	}

	public void testGetTag() {
		assertEquals("NumpadActivity", mActivity.getTag());

	}

	boolean passPress = false;
	boolean passRelease = false;
	
	
	public void testKeyNumLock() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUM_LOCK, keyCode);
				if(keyCode == KeyCode.VK_NUM_LOCK)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_numlock));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}

	public void testKeySlash() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_SLASH, keyCode);
				if(keyCode == KeyCode.VK_SLASH)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_forwardslash));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKeyAsterisk() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_ASTERISK, keyCode);
				if(keyCode == KeyCode.VK_ASTERISK)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_asterisk));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	

	public void testKeyMinus() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_MINUS, keyCode);
				if(keyCode == KeyCode.VK_MINUS)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_minus));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	

	public void testKeyPlus() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_PLUS, keyCode);
				if(keyCode == KeyCode.VK_PLUS)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_plus));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	

	public void testKeyEnter() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_ENTER, keyCode);
				if(keyCode == KeyCode.VK_ENTER)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_enter));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	

	public void testKeyDecimal() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_PERIOD, keyCode);
				if(keyCode == KeyCode.VK_PERIOD)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_decimal));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	

	public void testKey0() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD0, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD0)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_0));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey1() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD1, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD1)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_1));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey2() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD2, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD2)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_2));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey3() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD3, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD3)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_3));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey4() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD4, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD4)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_4));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey5() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD5, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD5)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_5));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey6() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD6, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD6)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_6));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey7() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD7, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD7)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_7));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey8() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD8, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD8)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_8));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
	
	public void testKey9() {
		passPress = false;
		passRelease = false;

		mNumpadFragment.setKeyEventListener(new KeyButton.KeyEventListener() {
			
			@Override
			public void onKeyEvent(int keyCode, KeyEventType type) {
				assertEquals(KeyCode.VK_NUMPAD9, keyCode);
				if(keyCode == KeyCode.VK_NUMPAD9)
				{
					if(type == KeyEventType.PRESS)
					{
						passPress = true;
					}else if(passPress && type == KeyEventType.RELEASE){
						passRelease = true;
					}
				}
			}
		});
		
		TouchUtils.clickView(this,
				mNumpadFragment.getView().findViewById(R.id.numpad_9));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}
}
