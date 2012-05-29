package com.cfms.android.mousedroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.KeyCode;

public class KeyButton extends Button {

	int mKeyCode = KeyCode.VK_SPACE;
	boolean mPressed = false;
	private KeyEventListener mListener;
	

	public KeyButton(Context context) {
		super(context);
	}
	
	public KeyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public KeyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}



	public void setKeyCode(int keyCode) {
		mKeyCode = keyCode;
	}

	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if (mListener != null) {
				mPressed = true;
				mListener.onKeyEvent(mKeyCode, KeyEventType.PRESS);
			}
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mListener != null && mPressed) {
				mPressed = false;
				mListener.onKeyEvent(mKeyCode, KeyEventType.RELEASE);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	public void setOnKeyEventListener(KeyEventListener listener) {
		mListener = listener;
	}

	public interface KeyEventListener {

		public void onKeyEvent(int keyCode, KeyEventType type);

	}
	
}
