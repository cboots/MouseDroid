package com.cfms.android.mousedroid.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.view.Touchpad;
import com.cfms.android.mousedroid.view.Touchpad.TouchpadListener;

public class TrackpadFragment extends Fragment implements TouchpadListener {

	private TrackpadListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.trackpad_fragment, container, false);
		Button button1 = (Button) v.findViewById(R.id.trackpadButton1);
		Button button3 = (Button) v.findViewById(R.id.trackpadButton2);
		Touchpad touchpad = (Touchpad) v.findViewById(R.id.touchpad);
		touchpad.setTouchpadListener(this);
		
		button1.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onButton1Touch(v, event);
			}
		});

		button3.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onButton3Touch(v, event);
			}
		});

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public boolean onButton1Touch(View yourButton, MotionEvent theMotion) {
		switch (theMotion.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.PRESS,
						MouseButton.BUTTON1);
			}

			break;
		case MotionEvent.ACTION_UP:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.RELEASE,
						MouseButton.BUTTON1);
			}
			break;
		}
		return true;
	}

	public boolean onButton3Touch(View yourButton, MotionEvent theMotion) {
		switch (theMotion.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.PRESS,
						MouseButton.BUTTON3);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mListener != null) {
				mListener.onMouseButtonEvent(MouseButtonEvent.RELEASE,
						MouseButton.BUTTON3);
			}
			break;
		}
		return true;
	}

	public void setTrackpadListener(TrackpadListener listener) {
		mListener = listener;
	}

	public interface TrackpadListener {
		public void onMouseMove(int dx, int dy);

		public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb);

		public void onScrollEvent(int ticks);

	}

	@Override
	public void onTouchpadMouseMove(int dx, int dy) {
		if(mListener != null)
		{
			mListener.onMouseMove(dx, dy);
		}
		
	}

	@Override
	public void onTouchpadMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
		if(mListener != null)
		{
			mListener.onMouseButtonEvent(mbe, mb);
		}
		
	}

	@Override
	public void onTouchpadScrollEvent(int ticks) {
		if(mListener != null)
		{
			mListener.onScrollEvent(ticks);
		}
		
	}

}
