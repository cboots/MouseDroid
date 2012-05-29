package com.cfms.android.mousedroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;

public class MyMouseButton extends Button {

	private MouseButtonEventListener mListener;
	private MouseButton mButton = MouseButton.BUTTON1;

	public MyMouseButton(Context context) {
		super(context);
	}

	public MyMouseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMouseButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setMouseButton(MouseButton button) {
		mButton = button;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.PRESS, mButton);
			}
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.RELEASE, mButton);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	public void setOnMouseButtonEventListener(MouseButtonEventListener listener) {
		mListener = listener;
	}

	public interface MouseButtonEventListener {

		public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb);

	}
}
