package kr.co.ethree.icare.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.DetectedActivity;
import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.data.LocationItem;
import kr.co.ethree.icare.database.IcareDB;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.EToast;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

/**
 * Created by lee on 2016-01-08.
 */
public class BaseActivity extends FragmentActivity implements BtlinkerDataListener, OnLocationUpdatedListener {

    private static final String ACTION_NOTI = "kr.co.ethree.icare.ACTION_NOTI";
    private static final int SETTING = 100;

    private LocationGooglePlayServicesProvider mProvider;

    private boolean is_gps;
    private boolean is_network;

    private double mLat;
    private double mLong;

    private static BaseActivity mContext;

    private IcareDB mDb;

    public String mDate;
    public String mIndex;
    public String mTemper;
    public String mHum;
    public String mUv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#4eb6b9"));
        }

        lastLocation();
        if (LocationEnabled()) {
            startLocation();
        } else {
            setGpsPopup();
        }
        mDb = IcareDB.getInstance(getApplicationContext());

        ReceiveDeviceDataService.setBtlinkerDataListener(this);
        mContext = this;

    }

    @Override
    protected void onDestroy() {
        stopLocation();
        handler.removeMessages(0);

        super.onDestroy();
    }

    private void setIndex(String temper, String hum) {
        int index = Utils.getHeatIndex(temper, hum);
        mIndex = String.valueOf(index);
    }

    public ArrayList<IcareItem> getIcareList() {
        return mDb.selectIcareList();
    }

    public IcareItem getLastIcare() {
        return mDb.selectLastIcare();
    }

    public ArrayList<IcareItem> getIcareDay() {
        return mDb.selectIcareDay();
    }

    public ArrayList<IcareItem> getIcareWeek() {
        return mDb.selectIcareWeek();
    }

    public ArrayList<IcareItem> getIcareMonth() {
        return mDb.selectIcareMonth();
    }

    public void insertIcare(IcareItem item) {
        mDb.insertIcareData(item);
    }

    public boolean isPushEnabled() {
        return mDb.isPushEnabled();
    }

    public void closeDb() {
        if (mDb != null) {
            mDb.close();
        }
    }

    @Override
    public void getMusicbr(String s) {

    }

    @Override
    public void getBluetoothData(Map<String, String> map) {
        ELog.e(null, "BaseActivity getBluetoothData");
        ELog.e(null, "data : " + map);
        if (PrefUtils.isHand(getApplicationContext()) && map.containsValue("8111B0")) {
            ReceiveDeviceDataService.SendHumitureCommand();
        }

        if (map.containsValue("8111B6")) {
            ReceiveDeviceDataService.sendUltravioletCommand();
        }

        final String temper = map.get("Tem");
        final String hum = map.get("hum");
        if (temper != null && hum != null) {;
            mTemper = temper;
            mHum = hum;
            setIndex(temper, hum);
        }

        final String uv = map.get("uv");
        if (uv != null) {
            mUv = uv;
            mDate = Utils.getDateTime();
            IcareItem item = new IcareItem();

            item.date = mDate;
            item.care = mIndex;
            item.temper = mTemper;
            item.hum = mHum;
            item.uv = mUv;
            mDb.insertIcareData(item);
        }
    }

    @Override
    public void getBluetoothConnectState(boolean b) {
        ELog.e(null, "BaseActivity getBluetoothConnectState : " + b);
//        PrefUtils.setConnect(getApplicationContext(), b);
    }

    @Override
    public void getBluetoothRSSI(int i) {

    }

    @Override
    public void getBluetoothWriteState(boolean b) {

    }

    private boolean LocationEnabled() {
        boolean isAll = SmartLocation.with(this).location().state().isAnyProviderAvailable();
        return isAll;
    }

    private void lastLocation() {
        Location location = SmartLocation.with(this).location().getLastLocation();
        if (location != null) {
            mLat = location.getLatitude();
            mLong = location.getLongitude();
            ELog.e(null, "lat : " + location.getLatitude() + " , long : " + location.getLongitude());
        }
    }

    private void startLocation() {
        mProvider = new LocationGooglePlayServicesProvider();
        mProvider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(mProvider).start(this);

        handler.sendEmptyMessageDelayed(0, 10000);
    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();
    }

    private void setGpsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 사용");
        builder.setMessage("ICARE 에서 내 위치 정보를 사용하려면, 단말기의 설정에서 '위치 서비스' 사용을 허용해 주세요");
        builder.setNegativeButton("설정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, SETTING);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("취소", null);
        builder.show();
    }

    public String myLocation() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address;

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(mLat, mLong, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list == null || list.size() == 0) {
            address = "현재위치를 찾을 수 없습니다.";
        } else {
            Address addr = list.get(0);
            address = addr.getAdminArea() + " " + addr.getLocality() + " " + addr.getThoroughfare() + " " + addr.getFeatureName();
        }
        return address;
    }

    public String lastAdreess(double lat, double lon) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address;

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lon, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list == null || list.size() == 0) {
            address = "현재위치를 찾을 수 없습니다.";
        } else {
            Address addr = list.get(0);
            address = addr.getAdminArea() + " " + addr.getLocality() + " " + addr.getThoroughfare() + " " + addr.getFeatureName();
        }
        return address;
    }

    public double getLat() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }

    public void insertLocation() {
        mDb.insertLocationData(mLat, mLong);
    }

    public LocationItem getLastLocation() {
        return mDb.selectLastLocation();
    }

    @Override
    public void onLocationUpdated(Location location) {
        ELog.e(null, "onLocationUpdated lat : " + location.getLatitude() + " , long : " + location.getLongitude());
        mLat = location.getLatitude();
        mLong = location.getLongitude();

        handler.removeMessages(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ELog.e(null, "requestCode : " + requestCode + " , resultCode : " + resultCode);
        if (requestCode == SETTING) {
            startLocation();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EToast.show(getApplicationContext(), "현재 위치 서비스를 일시적으로\n사용 할 수 없습니다.", EToast.LENGTH_SHORT);
        }
    };

    public static class NotiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ELog.e(null, "NotiReceiver bt connected : " + intent.getAction());
            boolean isHand = PrefUtils.isHand(context);
            if (intent.getAction().equals(ACTION_NOTI)) {
                if (ReceiveDeviceDataService.m_bConnected && !isHand) {
                    ReceiveDeviceDataService.setBtlinkerDataListener(mContext);
                    ReceiveDeviceDataService.SendHumitureCommand();
                }
            }
        }
    }

}
