package com.cfms.android.mousedroid.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.view.MyMouseButton;
import com.cfms.android.mousedroid.view.ScrollSlider;
import com.cfms.android.mousedroid.view.ScrollSlider.ScrollListener;
import com.cfms.android.mousedroid.view.Touchpad;
import com.cfms.android.mousedroid.view.Touchpad.TouchpadListener;

public class TrackpadFragment extends Fragment implements TouchpadListener, ScrollListener {

	private TrackpadListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MultiTouchActivity activity = (MultiTouchActivity) getActivity();
		View v = inflater.inflate(R.layout.trackpad_fragment, container, false);
		MyMouseButton button1 = (MyMouseButton) v.findViewById(R.id.trackpad_button1);
		MyMouseButton button3 = (MyMouseButton) v.findViewById(R.id.trackpad_button3);
		Touchpad touchpad = (Touchpad) v.findViewById(R.id.touchpad);
		ScrollSlider slider = (ScrollSlider) v.findViewById(R.id.scroll_slider);
		
		touchpad.setTouchpadListener(this);
		touchpad.setOnTouchListener(activity);
		
		slider.setScrollListener(this);
		slider.setOnTouchListener(activity);
		activity.addMoveOutsideEnabledViews(slider);
		
		button1.setOnTouchListener(activity);
		button1.setMouseButton(MouseButton.BUTTON1);
		button1.setOnMouseButtonEventListener(new MyMouseButton.MouseButtonEventListener() {
			
			@Override
			public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
				onTouchpadMouseButtonEvent(mbe, mb);
			}
		});
		
		button3.setOnTouchListener(activity);
		button3.setMouseButton(MouseButton.BUTTON3);
		button3.setOnMouseButtonEventListener(new MyMouseButton.MouseButtonEventListener() {
			
			@Override
			public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
				onTouchpadMouseButtonEvent(mbe, mb);
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

	@Override
	public void onScrollEvent(int ticks) {
		if(mListener != null)
		{
			mListener.onScrollEvent(ticks);
		}
	}

}
