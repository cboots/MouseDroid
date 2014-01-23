package com.cfms.mousedroid.utils;


//Class that contains common tasks and functions, all static
public class Util {

	private Util() {
	}

	public static String toSentenceCase(String str) {
		String temp = null;
		if (str != null) {
			if (str.length() > 0) {
				temp = str.substring(0, 1).toUpperCase();
				if (str.length() > 1) {
					temp = temp.concat(str.substring(1).toLowerCase());
				}
			}
		}
		return temp;
	}


}