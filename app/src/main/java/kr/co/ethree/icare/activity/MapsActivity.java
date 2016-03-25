package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, BtlinkerDataListener {

    private GoogleMap mMap;

    private double mLat;
    private double mLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        mLat = intent.getDoubleExtra("lat", 0);
        mLong = intent.getDoubleExtra("long", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ReceiveDeviceDataService.setBtlinkerDataListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng coords = new LatLng(mLat, mLong);
        mMap.addMarker(new MarkerOptions().position(coords));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 16));
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "mapsActivity getBluetoothData");
//        ELog.e(null, "data : " + map);
//
//        if (PrefUtils.isHand(getApplicationContext()) && map.containsValue("8111B0")) {
//            ReceiveDeviceDataService.SendHumitureCommand();
//        }
//
//        final String temper = map.get("Tem");
//        final String hum = map.get("hum");
//        if (temper != null && hum != null) {
//            mTemper = temper;
//            mHum = hum;
//        }
//
//        if (map.containsValue("8111B6")) {
//            ReceiveDeviceDataService.sendUltravioletCommand();
//        }
//
//        final String uv = map.get("uv");
//        if (uv != null) {
//            mUv = uv;
//            mDate = Utils.getDateTime();
//            mIndex = Utils.getIndex(mTemper, mHum);
//            IcareItem item = new IcareItem();
//
//            item.date = mDate;
//            item.care = mIndex;
//            item.temper = mTemper;
//            item.hum = mHum;
//            item.uv = mUv;
//
//            insertIcare(item);
//            ELog.e(null, "isPush : " + PrefUtils.isPush(getApplicationContext()) + " , isPushEnabled : " + isPushEnabled());
//            if (PrefUtils.isPush(getApplicationContext()) && isPushEnabled()) {
//                int step = Utils.getIndexStep(mIndex);
//                ELog.e(null, "step : " + step);
//                if (step == 3) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Utils.heatIndexBad(getApplicationContext());
//                        }
//                    });
//                }
//            }
//        }
//
//    }

    @Override
    public void getBluetoothConnectState(boolean b) {
        ELog.e(null, "mapsActivity getBluetoothConnectState : " + b);
        if (!b) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.disConnectToast(getApplicationContext());
                }
            });

            insertLocation();
        }
    }
}
