package kr.co.ethree.icare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.data.LocationItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.Utils;

/**
 * Created by lee on 2016-01-21.
 */
public class IcareDB {

    private Context mContext;
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;
    private static IcareDB mInstance;

    private static AtomicInteger mOpenCounter = new AtomicInteger();

    public static final String DATABASE_NAME = "icare.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_ICARE = "icare_table";
    public static final String DATABASE_TABLE_LOCATION = "location_table";

    public static final String KEY_ID = "_id";

    public static final String KEY_DATE = "date";
    public static final String KEY_CARE = "care";
    public static final String KEY_TEMPER = "temper";
    public static final String KEY_HUM = "hum";
    public static final String KEY_UV = "uv";

    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";

    private static final String DATABASE_CREATE_ICARE = "create table " +
            DATABASE_TABLE_ICARE + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_DATE + " text, "
            + KEY_CARE + " text, "
            + KEY_TEMPER + " text, "
            + KEY_HUM + " text, "
            + KEY_UV + " text);";

    private static final String DATABASE_CREATE_LOCATION = "create table " +
            DATABASE_TABLE_LOCATION + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_LAT + " text, "
            + KEY_LONG + " text);";

    public IcareDB(Context context) {
        mContext = context;
    }

    public static IcareDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new IcareDB(context);
            open(context);
        }
        return mInstance;
    }

    public static synchronized IcareDB open(Context context) {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDbHelper = new DatabaseHelper(context);
            mDb = mDbHelper.getWritableDatabase();
        }
        return mInstance;
    }

    public void close() {
        if (mOpenCounter.incrementAndGet() == 0) {
            mDbHelper.close();
        }
    }

    public void insertIcareData(IcareItem item) {
        ELog.e(null, "insertIcareData");
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, item.date);
        values.put(KEY_CARE, item.care);
        values.put(KEY_TEMPER, item.temper);
        values.put(KEY_HUM, item.hum);
        values.put(KEY_UV, item.uv);

        mDb.insert(DATABASE_TABLE_ICARE, item.date, values);
    }

    public void sampleIcareData(String date) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);

        mDb.insert(DATABASE_TABLE_ICARE, date, values);
    }

    public ArrayList<String> selectSample() {
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, null, null, null, null, null);

        ArrayList<String> items = new ArrayList();
        if (cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex(KEY_DATE);

            do {
                String item = cursor.getString(dateIndex);

                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return items;
    }

    public ArrayList<IcareItem> selectIcareDay() {
        String date = Utils.getDateTime();
        ELog.e(null, "date : " + date);
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, KEY_DATE + " > date(?,'0 day')", new String[]{date}, null, null, null);

        ArrayList<IcareItem> items = null;
        if (cursor.moveToFirst()) {
            items = addIcareItem(cursor);
        }
        cursor.close();

        return items;
    }

    public ArrayList<IcareItem> selectIcareWeek() {
        String date = Utils.getDateTime();
        ELog.e(null, "date : " + date);
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, KEY_DATE + " > date(?,'-7 day')", new String[]{date}, null, null, null);

        ArrayList<IcareItem> items = null;
        if (cursor.moveToFirst()) {
            items = addIcareItem(cursor);
        }
        cursor.close();

        return items;
    }

    public ArrayList<IcareItem> selectIcareMonth() {
        String date = Utils.getDateTime();
        ELog.e(null, "date : " + date);
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, KEY_DATE + " > date(?,'-1 month')", new String[]{date}, null, null, null);

        ArrayList<IcareItem> items = null;
        if (cursor.moveToFirst()) {
            items = addIcareItem(cursor);
        }
        cursor.close();

        return items;
    }

    public boolean deleteIcare() {
        return  mDb.delete(DATABASE_TABLE_ICARE, null, null) > 0;
    }

    public ArrayList<IcareItem> selectIcareList() {
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, null, null, null, null, null);

        ArrayList<IcareItem> items = null;
        if (cursor.moveToFirst()) {
            items = addIcareItem(cursor);
        }
        cursor.close();

        return items;
    }

    private ArrayList<IcareItem> addIcareItem(Cursor cursor) {
        ArrayList<IcareItem> items = new ArrayList();

        int dateIndex = cursor.getColumnIndex(KEY_DATE);
        int careIndex = cursor.getColumnIndex(KEY_CARE);
        int temperIndex = cursor.getColumnIndex(KEY_TEMPER);
        int humIndex = cursor.getColumnIndex(KEY_HUM);
        int uvIndex = cursor.getColumnIndex(KEY_UV);

        do {
            IcareItem item = new IcareItem();
            item.date = cursor.getString(dateIndex);
            item.care = cursor.getString(careIndex);
            item.temper = cursor.getString(temperIndex);
            item.hum = cursor.getString(humIndex);
            item.uv = cursor.getString(uvIndex);

            items.add(item);
        } while (cursor.moveToNext());

        return items;
    }

    public IcareItem selectLastIcare() {
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, null, null, null, null, null);

        IcareItem item = null;
        if (cursor.moveToLast()) {

            int dateIndex = cursor.getColumnIndex(KEY_DATE);
            int careIndex = cursor.getColumnIndex(KEY_CARE);
            int temperIndex = cursor.getColumnIndex(KEY_TEMPER);
            int humIndex = cursor.getColumnIndex(KEY_HUM);
            int uvIndex = cursor.getColumnIndex(KEY_UV);

            item = new IcareItem();
            item.date = cursor.getString(dateIndex);
            item.care = cursor.getString(careIndex);
            item.temper = cursor.getString(temperIndex);
            item.hum = cursor.getString(humIndex);
            item.uv = cursor.getString(uvIndex);
        }
        cursor.close();

        return item;
    }

    public boolean isPushEnabled() {
        Cursor cursor = mDb.query(DATABASE_TABLE_ICARE, null, null, null, null, null, null);

        boolean isEnabled;
        String last = null;
        String prev = null;

        int lastInt = 0;
        int prevInt = 0;

        if (cursor.moveToLast()) {
            int careIndex = cursor.getColumnIndex(KEY_CARE);
            last = cursor.getString(careIndex);
        }

        if (cursor.moveToPrevious()) {
            int careIndex = cursor.getColumnIndex(KEY_CARE);
            prev = cursor.getString(careIndex);
        }

        if (last != null && !last.equals("null")) {
            lastInt = Utils.getIndexStep(last);
        }

        if (prev != null && !prev.equals("null")) {
            prevInt = Utils.getIndexStep(prev);
        }

        if (lastInt == 3 && prevInt == 3) {
            isEnabled = false;
        } else {
            isEnabled = true;
        }

        cursor.close();

        return isEnabled;
    }

    public void insertLocationData(double lat, double lon) {
        String lat2 = String.valueOf(lat);
        String lon2 = String.valueOf(lon);
        ContentValues values = new ContentValues();
        values.put(KEY_LAT, lat2);
        values.put(KEY_LONG, lon2);

        mDb.insert(DATABASE_TABLE_LOCATION, lat2, values);
    }

    public LocationItem selectLastLocation() {
        Cursor cursor = mDb.query(DATABASE_TABLE_LOCATION, null, null, null, null, null, null);

        LocationItem item = null;
        if (cursor.moveToLast()) {

            int latIndex = cursor.getColumnIndex(KEY_LAT);
            int lonIndex = cursor.getColumnIndex(KEY_LONG);

            item = new LocationItem();
            item.lat = cursor.getString(latIndex);
            item.lon = cursor.getString(lonIndex);
        }
        cursor.close();

        return item;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_ICARE);
            db.execSQL(DATABASE_CREATE_LOCATION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
