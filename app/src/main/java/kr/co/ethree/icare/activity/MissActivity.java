package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import kr.co.ethree.icare.Icare;
import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.data.LocationItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class MissActivity extends BaseActivity implements View.OnClickListener, BtlinkerDataListener {

    private static final int SETTING = 100;

    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private LinearLayout mBaseLayout;
    private ImageView mProfileImageView;
    private ImageView mCircleImageView;
    private ImageView mIconImageView;
    private TextView mStateTextView;
    private ImageView mMapImageView;
    private TextView mLocateTextView;

    private Bitmap mBitmap;

    private String mPath;

    private double mLat;
    private double mLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss);

        setUpView();
        setUpData();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void setUpView() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mBaseLayout = (LinearLayout) findViewById(R.id.base_layout);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image_view);
        mCircleImageView = (ImageView) findViewById(R.id.circle_image_view);
        mIconImageView = (ImageView) findViewById(R.id.icon_image_view);
        mStateTextView = (TextView) findViewById(R.id.state_text_view);
        mMapImageView = (ImageView) findViewById(R.id.map_image_view);
        mLocateTextView = (TextView) findViewById(R.id.locate_text_view);

        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mMapImageView.setOnClickListener(this);
    }

    private void setUpData() {
        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        mPath = getFilesDir() + "/" + Icare.PROFILE;

        if (ReceiveDeviceDataService.m_bConnected) {
            mLocateTextView.setText(myLocation());
            mLat = getLat();
            mLong = getLong();
        } else {
            LocationItem item = getLastLocation();
            if (item != null) {
                mLat = Double.parseDouble(item.lat);
                mLong = Double.parseDouble(item.lon);
                mLocateTextView.setText(lastAdreess(mLat, mLong));
            } else {
                mLocateTextView.setText(myLocation());
                mLat = getLat();
                mLong = getLong();
            }
        }

        setImage();
        setStateVIew();
    }

    private void setImage() {

        File file = new File(mPath);
        Bitmap bitmap = null;

        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(mPath);

            try {
                ExifInterface exif = new ExifInterface(mPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int degrees = Utils.exifDegrees(orientation);
                bitmap = Utils.bitmapRotate(bitmap, degrees);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_signup_photos_01);
        }

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 204, getResources().getDisplayMetrics());
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, size, size, false);
        mBitmap = Utils.getCircleBitmap(bitmap2);

        mProfileImageView.setImageBitmap(mBitmap);
        bitmap.recycle();
        bitmap2.recycle();

    }

    private void setStateVIew() {
        if (ReceiveDeviceDataService.m_bConnected) {
            mBaseLayout.setBackgroundResource(R.drawable.bg_temperature_proper);
            mCircleImageView.setBackgroundResource(R.drawable.img_photo_missingchild_safety_circle);
            mIconImageView.setImageResource(R.drawable.ic_photo_missingchild_safety);
            mStateTextView.setText("안전");
        } else {
            mBaseLayout.setBackgroundResource(R.drawable.bg_temperature_high);
            mCircleImageView.setBackgroundResource(R.drawable.img_photo_missingchild_unsafe_circle);
            mIconImageView.setImageResource(R.drawable.ic_photo_missingchild_unsafe);
            mStateTextView.setText("불안전");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.share_btn:
                Utils.sendSNS(this);
                break;
            case R.id.map_image_view:
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("lat", mLat);
                intent.putExtra("long", mLong);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        super.onLocationUpdated(location);
        if (ReceiveDeviceDataService.m_bConnected) {
            mLocateTextView.setText(myLocation());
            mLat = location.getLatitude();
            mLong = location.getLongitude();
        }
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "missActivity getBluetoothData");
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
        ELog.e(null, "missActivity getBluetoothConnectState : " + b);
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
