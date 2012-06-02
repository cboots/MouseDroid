package com.cfms.android.mousedroid.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.KeyCode;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.view.KeyButton;
import com.cfms.android.mousedroid.view.KeyButton.KeyEventListener;

public class NumpadFragment extends Fragment implements KeyEventListener {


	
	private KeyEventListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MultiTouchActivity activity = (MultiTouchActivity) getActivity();
		View v = inflater.inflate(R.layout.numpad_fragment, container, false);
		
		setupKeys(activity, v);
		
		
		return v;
	}

	private void setupKeys(MultiTouchActivity activity, View root) {
		setupKey(activity, root, R.id.numpad_0, KeyCode.VK_NUMPAD0);
		setupKey(activity, root, R.id.numpad_1, KeyCode.VK_NUMPAD1);
		setupKey(activity, root, R.id.numpad_2, KeyCode.VK_NUMPAD2);
		setupKey(activity, root, R.id.numpad_3, KeyCode.VK_NUMPAD3);
		setupKey(activity, root, R.id.numpad_4, KeyCode.VK_NUMPAD4);
		setupKey(activity, root, R.id.numpad_5, KeyCode.VK_NUMPAD5);
		setupKey(activity, root, R.id.numpad_6, KeyCode.VK_NUMPAD6);
		setupKey(activity, root, R.id.numpad_7, KeyCode.VK_NUMPAD7);
		setupKey(activity, root, R.id.numpad_8, KeyCode.VK_NUMPAD8);
		setupKey(activity, root, R.id.numpad_9, KeyCode.VK_NUMPAD9);
		setupKey(activity, root, R.id.numpad_numlock, KeyCode.VK_NUM_LOCK);
		setupKey(activity, root, R.id.numpad_forwardslash, KeyCode.VK_SLASH);
		setupKey(activity, root, R.id.numpad_asterisk, KeyCode.VK_ASTERISK);
		setupKey(activity, root, R.id.numpad_minus, KeyCode.VK_MINUS);
		setupKey(activity, root, R.id.numpad_plus, KeyCode.VK_PLUS);
		setupKey(activity, root, R.id.numpad_decimal, KeyCode.VK_PERIOD);
		setupKey(activity, root, R.id.numpad_enter, KeyCode.VK_ENTER);
		
	}
	
	private void setupKey(MultiTouchActivity activity, View root, int viewId, int keyCode){
		boolean vibrate = PreferencesActivity.getBoolean(activity, PreferencesActivity.PREF_KEY_VIBRATE);
		KeyButton button = (KeyButton) root.findViewById(viewId);
		button.setKeyCode(keyCode);
		button.setOnTouchListener(activity);
		button.setOnKeyEventListener(this);
		button.setEnableVibrate(vibrate);
		activity.addMoveOutsideEnabledViews(button);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void setKeyEventListener(KeyEventListener listener) {
		mListener = listener;
	}

	@Override
	public void onKeyEvent(int keyCode, KeyEventType type) {
		if(mListener != null)
		{
			mListener.onKeyEvent(keyCode, type);
		}
	}

}
