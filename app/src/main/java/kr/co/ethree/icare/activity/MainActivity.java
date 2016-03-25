package kr.co.ethree.icare.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import kr.co.ethree.icare.Icare;
import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.service.NotiService;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.EToast;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class MainActivity extends BaseActivity implements View.OnClickListener, BtlinkerDataListener {

    private final static int REQUEST_ENABLE_BT = 100;

    private ImageButton mShareBtn;
    private RelativeLayout mIndexLayout;
    private LinearLayout mTemperLayout;
    private LinearLayout mHumLayout;
    private LinearLayout mUvLayout;
    private LinearLayout mMissLayout;
    private ImageView mProfileImageView;
    private ImageView mBgImageView;
    private TextView mIndexTextView;
    private TextView mStatusTextView;
    private ImageButton mMonitorBtn;
    private ImageButton mIcareBtn;
    private ImageButton mSettingBtn;

    private Bitmap mBitmap;

    private String mPath;

    private BluetoothAdapter mBtAdapter;

    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpView();
        setUpData();

        String address = myLocation();
        ELog.e(null, "location : " + address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImage();
        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        if (isFirst && ReceiveDeviceDataService.m_bConnected) {
            ReceiveDeviceDataService.SendHumitureCommand();
            isFirst = false;
        }
        ELog.e(null, "ReceiveDeviceDataService.m_bConnected : " + ReceiveDeviceDataService.m_bConnected);
    }

    @Override
    protected void onDestroy() {
        closeDb();

        Intent noti = new Intent(getApplicationContext(), NotiService.class);
        stopService(noti);

        super.onDestroy();
    }

    private void setUpView() {
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mIndexLayout = (RelativeLayout) findViewById(R.id.index_layout);
        mTemperLayout = (LinearLayout) findViewById(R.id.temper_layout);
        mHumLayout = (LinearLayout) findViewById(R.id.hum_layout);
        mUvLayout = (LinearLayout) findViewById(R.id.uv_layout);
        mMissLayout = (LinearLayout) findViewById(R.id.miss_layout);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image_view);
        mBgImageView = (ImageView) findViewById(R.id.bg_image_view);
        mIndexTextView = (TextView) findViewById(R.id.index_text_view);
        mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        mMonitorBtn = (ImageButton) findViewById(R.id.monitor_btn);
        mIcareBtn = (ImageButton) findViewById(R.id.icare_btn);
        mSettingBtn = (ImageButton) findViewById(R.id.setting_btn);

        mShareBtn.setOnClickListener(this);
        mIndexLayout.setOnClickListener(this);
        mTemperLayout.setOnClickListener(this);
        mHumLayout.setOnClickListener(this);
        mUvLayout.setOnClickListener(this);
        mMissLayout.setOnClickListener(this);
        mMonitorBtn.setOnClickListener(this);
        mIcareBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
    }

    private void setUpData() {
        Intent noti = new Intent(getApplicationContext(), NotiService.class);
        startService(noti);

        mPath = getFilesDir() + "/" + Icare.PROFILE;

        Intent service = new Intent(getApplicationContext(), ReceiveDeviceDataService.class);
        startService(service);

        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {

        }

        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        isFirst = true;

        if (ReceiveDeviceDataService.m_bConnected) {
            ReceiveDeviceDataService.SendHumitureCommand();
            isFirst = false;
        }

        IcareItem item = getLastIcare();

        if (item != null) {
            String temper = item.temper;
            String hum = item.hum;

            if (temper != null && hum != null) {
                setIndex(temper, hum);
            }
        }

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

        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 204, getResources().getDisplayMetrics());
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, size, size, false);
        mBitmap = Utils.getCircleBitmap(bitmap2);

        mProfileImageView.setImageBitmap(mBitmap);
        bitmap.recycle();
        bitmap2.recycle();
    }

    private void setIndex(String temper, String hum) {
        int index = Utils.getHeatIndex(temper, hum);
        int step = Utils.getIndexStep(index);
        mIndex = String.valueOf(index);

        switch (step) {
            case 1:
                mBgImageView.setBackgroundResource(R.drawable.img_photos_cover_thi_usually);
                mStatusTextView.setText("보통");
                break;
            case 2:
                mBgImageView.setBackgroundResource(R.drawable.img_photos_cover_thi_baddish);
                mStatusTextView.setText("약간나쁨");
                break;
            case 3:
                mBgImageView.setBackgroundResource(R.drawable.img_photos_cover_thi_bad);
                mStatusTextView.setText("나쁨");
                break;
            default:
                break;
        }

        mIndexTextView.setText(String.valueOf(index));
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.share_btn:
                Utils.sendSNS(this);
//                sampleDB();
                break;
            case R.id.index_layout:
                if (!mBtAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    intent = new Intent(getApplicationContext(), IndexActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.temper_layout:
                if (!mBtAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    intent = new Intent(getApplicationContext(), TemperActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.hum_layout:
                if (!mBtAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    intent = new Intent(getApplicationContext(), HumActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.uv_layout:
                if (!mBtAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    intent = new Intent(getApplicationContext(), UvActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.miss_layout:
                intent = new Intent(getApplicationContext(), MissActivity.class);
                startActivity(intent);
                break;
            case R.id.monitor_btn:
                intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
                break;
            case R.id.icare_btn:
                intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_btn:
                if (!mBtAdapter.isEnabled()) {
                    intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void getBluetoothData(Map<String, String> map) {
        ELog.e(null, "mainActivity getBluetoothData");
        ELog.e(null, "data : " + map);
        if (map.containsValue("8111B6")) {
            ReceiveDeviceDataService.sendUltravioletCommand();
        }

        final String temper = map.get("Tem");
        final String hum = map.get("hum");
        if (temper != null && hum != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setIndex(temper, hum);
                }
            });
            mTemper = temper;
            mHum = hum;
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

            insertIcare(item);
            ELog.e(null, "isPush : " + PrefUtils.isPush(getApplicationContext()) + " , isPushEnabled : " + isPushEnabled());
            if (PrefUtils.isPush(getApplicationContext()) && isPushEnabled()) {
                int step = Utils.getIndexStep(mIndex);
                ELog.e(null, "step : " + step);
                if (step == 3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.heatIndexBad(getApplicationContext());
                        }
                    });
                }
            }
        }

        if (PrefUtils.isHand(getApplicationContext()) && map.containsValue("8111B0")) {
            ReceiveDeviceDataService.SendHumitureCommand();
        }
    }

    @Override
    public void getBluetoothConnectState(boolean b) {
        ELog.e(null, "mainActivity getBluetoothConnectState : " + b);
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

    private void sampleDB() {
        ArrayList<IcareItem> items = new ArrayList<>();
        IcareItem item = new IcareItem();
        item.date = "2015-02-23 17:36:39";
        item.care = "80";
        item.temper = "19";
        item.hum = "30";
        item.uv = "0";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-01-10 17:36:39";
        item.care = "100";
        item.temper = "19";
        item.hum = "30";
        item.uv = "0";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-01-23 17:36:39";
        item.care = "90";
        item.temper = "15";
        item.hum = "50";
        item.uv = "5";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-01-30 17:36:39";
        item.care = "102";
        item.temper = "22";
        item.hum = "40";
        item.uv = "5";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-15 17:36:39";
        item.care = "85";
        item.temper = "21";
        item.hum = "50";
        item.uv = "3";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-16 17:36:39";
        item.care = "103";
        item.temper = "27";
        item.hum = "60";
        item.uv = "8";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 14:36:39";
        item.care = "90";
        item.temper = "20";
        item.hum = "39";
        item.uv = "3";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 15:36:39";
        item.care = "102";
        item.temper = "21";
        item.hum = "40";
        item.uv = "5";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 16:36:39";
        item.care = "103";
        item.temper = "24";
        item.hum = "50";
        item.uv = "6";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 16:46:39";
        item.care = "103";
        item.temper = "25";
        item.hum = "51";
        item.uv = "7";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 16:56:39";
        item.care = "103";
        item.temper = "25";
        item.hum = "51";
        item.uv = "8";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 17:06:39";
        item.care = "103";
        item.temper = "25";
        item.hum = "51";
        item.uv = "10";
        items.add(item);

        item = new IcareItem();
        item.date = "2016-02-23 17:16:39";
        item.care = "103";
        item.temper = "25";
        item.hum = "51";
        item.uv = "11";
        items.add(item);

        for (int i = 0; i < items.size(); i++) {
            insertIcare(items.get(i));
        }
    }

}
