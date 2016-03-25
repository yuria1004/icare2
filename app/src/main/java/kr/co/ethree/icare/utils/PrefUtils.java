package kr.co.ethree.icare.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lee on 2016-01-11.
 */
public class PrefUtils {

    /*********************************************************************
     * gcm preference
     *********************************************************************/
    private static final String GCM = "gcm";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";

    public static boolean storeRegId(Context context, String regId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(GCM, Context.MODE_PRIVATE).edit();
        int appVersion = Utils.getAppVerstion(context);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);

        return editor.commit();
    }

    public static String restoreRegId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(GCM, Context.MODE_PRIVATE);
        return pref.getString(PROPERTY_REG_ID, "");
    }

    public static int restoreAppVersion(Context context) {
        SharedPreferences pref = context.getSharedPreferences(GCM, Context.MODE_PRIVATE);
        return pref.getInt(PROPERTY_APP_VERSION, 0);
    }

    /*********************************************************************
     * setting preference
     *********************************************************************/
    private static final String SETTING = "setting";
    private static final String CONNECT = "connect";
    private static final String PUSH = "push";
    private static final String CHECK = "check";
    private static final String NAME = "name";

    public static boolean setConnect(Context context, boolean isOn) {
        ELog.e(null, "SharedPreferences setConnect : " + isOn);
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CONNECT, isOn);

        return editor.commit();
    }

    public static boolean setPush(Context context, boolean isOn) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PUSH, isOn);

        return editor.commit();
    }

    public static boolean setCheck(Context context, boolean isHand) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CHECK, isHand);

        return editor.commit();
    }

    public static boolean setName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        editor.putString(NAME, name);

        return editor.commit();
    }

    public static boolean isConnect(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(CONNECT, false);
    }

    public static boolean isPush(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(PUSH, true);
    }

    public static boolean isHand(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(CHECK, false);
    }

    public static String getName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        return pref.getString(NAME, "");
    }

}
