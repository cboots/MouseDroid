package com.cfms.mousedroid.utils;

import android.util.Log;

public class DebugLog {

	//TODO Disable debug/verbose logs in release branch
	public static final boolean D = true;
	public static final boolean V = true;
	public static final boolean I = true;
	public static final boolean W = true;
	public static final boolean E = true;
	public static final boolean WTF = true;
	
	
	public static void D(String tag, String msg){
		if(D)
			Log.d(tag, msg);
	}
	

	public static void D(String tag, String msg, Throwable tr){
		if(D)
			Log.d(tag, msg, tr);
	}


	
	public static void V(String tag, String msg){
		if(V)
			Log.v(tag, msg);
	}
	

	public static void V(String tag, String msg, Throwable tr){
		if(V)
			Log.v(tag, msg, tr);
	}
	
	
	public static void I(String tag, String msg){
		if(I)
			Log.i(tag, msg);
	}
	

	public static void I(String tag, String msg, Throwable tr){
		if(I)
			Log.i(tag, msg, tr);
	}
	
	
	public static void W(String tag, String msg){
		if(W)
			Log.w(tag, msg);
	}
	

	public static void W(String tag, String msg, Throwable tr){
		if(W)
			Log.w(tag, msg, tr);
	}	
	
	
	public static void E(String tag, String msg){
		if(E)
			Log.e(tag, msg);
	}
	

	public static void E(String tag, String msg, Throwable tr){
		if(E)
			Log.e(tag, msg, tr);
	}
	
	
	public static void WTF(String tag, String msg){
		if(WTF)
			Log.wtf(tag, msg);
	}
	

	public static void WTF(String tag, String msg, Throwable tr){
		if(WTF)
			Log.wtf(tag, msg, tr);
	}
}
