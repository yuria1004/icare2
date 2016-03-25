package kr.co.ethree.icare.utils;


import android.util.Log;

public class ELog {

	private static final String TAG = "Icare";
	
	private static boolean isDebug = false;
	
	private static final int VERBOSE = 2;				// Log.v
	private static final int DEBUG = 3;					// Log.d
	private static final int INFO = 4;					// Log.i
	private static final int WARN = 5;					// Log.w
	private static final int ERROR = 6;					// Log.e
	

	public static void v(String tag, String msg) {
		if (isDebug) {
			if (tag == null) {
				tag = TAG;
			}
			println(VERBOSE, tag, msg);
		}
	}
	

	public static void d(String tag, String msg) {
		if (isDebug) {
			if (tag == null) {
				tag = TAG;
			}
			println(DEBUG, tag, msg);
		}
	}
	

	public static void i(String tag, String msg) {
		if (isDebug) {
			if (tag == null) {
				tag = TAG;
			}
			println(INFO, tag, msg);
		}
	}
	

	public static void w(String tag, String msg) {
		if (isDebug) {
			if (tag == null) {
				tag = TAG;
			}
			println(WARN, tag, msg);
		}
	}
	

	public static void e(String tag, String msg) {
		if (isDebug) {
			if (tag == null) {
				tag = TAG;
			}
			println(ERROR, tag, msg);
		}
	}
	

	public static int println(int priority, String tag, String msg) {
		return Log.println(priority, tag, msg);
	}
	
}
