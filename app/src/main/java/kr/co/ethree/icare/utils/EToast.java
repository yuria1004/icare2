package kr.co.ethree.icare.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class EToast {

	private static Toast toastMain = null;
	
	public static final int LENGTH_SHORT = 0;
	public static final int LENGTH_LONG = 1;
	
	
	public static void show(Context context, String msg, boolean bCenter, int time) {
		if (toastMain != null) {
			toastMain.setText(msg);
			toastMain.show();
		} else {
			toastMain = Toast.makeText(context, msg, time);
			if (bCenter) {
				toastMain.setGravity(Gravity.CENTER, 0, 0);
			}
			toastMain.show();
		}
	}
	
	public static void show(Context context, String msg, int time) {
		if (toastMain != null) {
			toastMain.setText(msg);
			toastMain.show();
		} else {
			toastMain = Toast.makeText(context, msg, time);
			toastMain.show();
		}
	}
	
	public static void show(Context context, int resId, boolean bCenter, int time) {
		if (toastMain != null) {
			toastMain.setText(context.getResources().getString(resId));
			toastMain.show();
		} else {
			toastMain = Toast.makeText(context, resId, time);
			if (bCenter) {
				toastMain.setGravity(Gravity.CENTER, 0, 0);
			}
			toastMain.show();
		}
	}
	
	public static void show(Context context, int resId, int time) {
		if (toastMain != null) {
			toastMain.setText(context.getResources().getString(resId));
			toastMain.show();
		} else {
			toastMain = Toast.makeText(context, resId, time);
			toastMain.show();
		}
	}
	
}
