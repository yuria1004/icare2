package kr.co.ethree.icare.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xrz.lib.bluetooth.BTLinkerUtils;
import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import kr.co.ethree.icare.Icare;
import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.service.NotiService;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;
import kr.co.ethree.icare.widget.PopupDialog;

public class SettingActivity extends BaseActivity implements View.OnClickListener, BtlinkerDataListener {

    private static final int PHOTO = 100;
    private static final int CROP = 101;
    private static final int IMAGE = 102;

    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private ImageView mProfileImageView;
    private TextView mNameTextView;
    private ImageButton mConnectOffBtn;
    private Button mConnectOnBtn;
    private ImageButton mPushBtn;
    private Button mHandBtn;
    private Button mAutoBtn;
    private ImageButton mHomeBtn;
    private ImageButton mMonitorBtn;
    private ImageButton mIcareBtn;

    private boolean isConnect;
    private boolean isPush;
    private boolean isHand;

    private String mPath;
    private String mPath2;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setUpView();
        setUpData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReceiveDeviceDataService.isrunconnect = true;
        isConnect = PrefUtils.isConnect(getApplicationContext());
        ReceiveDeviceDataService.setBtlinkerDataListener(this);
//        ELog.e(null, "onResume : " + isConnect);
        refreshConnectView(isConnect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private void setUpView() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image_view);
        mNameTextView = (TextView) findViewById(R.id.name_text_view);
        mConnectOffBtn = (ImageButton) findViewById(R.id.connect_off_btn);
        mConnectOnBtn = (Button) findViewById(R.id.connect_on_btn);
        mPushBtn = (ImageButton) findViewById(R.id.push_btn);
        mHandBtn = (Button) findViewById(R.id.hand_btn);
        mAutoBtn = (Button) findViewById(R.id.auto_btn);
        mHomeBtn = (ImageButton) findViewById(R.id.home_btn);
        mMonitorBtn = (ImageButton) findViewById(R.id.monitor_btn);
        mIcareBtn = (ImageButton) findViewById(R.id.icare_btn);

        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mProfileImageView.setOnClickListener(this);
        mConnectOffBtn.setOnClickListener(this);
        mConnectOnBtn.setOnClickListener(this);
        mPushBtn.setOnClickListener(this);
        mHandBtn.setOnClickListener(this);
        mAutoBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mMonitorBtn.setOnClickListener(this);
        mIcareBtn.setOnClickListener(this);
    }

    private void setUpData() {
        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        mPath2 = getFilesDir() + "/" + Icare.PROFILE;
        mPath = Environment.getExternalStorageDirectory() + "/" + Icare.PROFILE;

        isPush = PrefUtils.isPush(getApplicationContext());
        isHand = PrefUtils.isHand(getApplicationContext());

        if (isPush) {
            mPushBtn.setBackgroundResource(R.drawable.btn_switch_on);
        } else {
            mPushBtn.setBackgroundResource(R.drawable.btn_switch_off);
        }

        if (isHand) {
            mHandBtn.setBackgroundResource(R.drawable.btn_check_auto);
            mHandBtn.setTextColor(Color.parseColor("#31c3be"));
            int left = mHandBtn.getPaddingLeft();
            int right = mHandBtn.getPaddingRight();
            mHandBtn.setPadding(left, 0, right, 0);

            mAutoBtn.setBackgroundResource(R.drawable.btn_check_hand);
            mAutoBtn.setTextColor(Color.parseColor("#666666"));

            left = mAutoBtn.getPaddingLeft();
            right = mAutoBtn.getPaddingRight();
            mAutoBtn.setPadding(left, 0, right, 0);
        } else {
            mHandBtn.setBackgroundResource(R.drawable.btn_check_hand);
            mHandBtn.setTextColor(Color.parseColor("#666666"));
            int left = mHandBtn.getPaddingLeft();
            int right = mHandBtn.getPaddingRight();
            mHandBtn.setPadding(left, 0, right, 0);

            mAutoBtn.setBackgroundResource(R.drawable.btn_check_auto);
            mAutoBtn.setTextColor(Color.parseColor("#31c3be"));

            left = mAutoBtn.getPaddingLeft();
            right = mAutoBtn.getPaddingRight();
            mAutoBtn.setPadding(left, 0, right, 0);
        }

        File file = new File(mPath2);
        if (file.exists()) {
            setImage();
        } else {
            setDefaultImage();
        }
    }

    private void crop(Uri uri) {
        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 204, getResources().getDisplayMetrics());

//        ELog.e(null, "path : " + mPath);
        Uri output = Uri.fromFile(new File(mPath));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("output", output);
        startActivityForResult(intent, CROP);
    }

    private void saveImage() {
        byte[] bytes = Utils.readFile(mPath);

        File f = new File(mPath2);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream fos = openFileOutput(Icare.PROFILE, Context.MODE_APPEND);
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteImage() {
        File file = new File(mPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void setImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPath2);

        try {
            ExifInterface exif = new ExifInterface(mPath2);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int degrees = Utils.exifDegrees(orientation);
            bitmap = Utils.bitmapRotate(bitmap, degrees);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 102, getResources().getDisplayMetrics());
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, size, size, false);
        mBitmap = Utils.getCircleBitmap(bitmap2);

        mProfileImageView.setImageBitmap(mBitmap);
        bitmap.recycle();
        bitmap2.recycle();
    }

    private void setDefaultImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_signup_photos_01);

        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 102, getResources().getDisplayMetrics());
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, size, size, false);
        mBitmap = Utils.getCircleBitmap(bitmap2);

        mProfileImageView.setImageBitmap(mBitmap);
        bitmap.recycle();
        bitmap2.recycle();
    }

    private void refreshConnectView(boolean isConnect) {
        PrefUtils.setConnect(getApplicationContext(), isConnect);
        String name = PrefUtils.getName(getApplicationContext());
//        ELog.e(null, "refreshConnectView : " + isConnect);
        if (ReceiveDeviceDataService.m_bConnected) {
            mConnectOffBtn.setVisibility(View.GONE);
            mConnectOnBtn.setVisibility(View.VISIBLE);

            mNameTextView.setText(name);
            mNameTextView.setVisibility(View.VISIBLE);
        } else {
            mConnectOffBtn.setVisibility(View.VISIBLE);
            mConnectOnBtn.setVisibility(View.GONE);

            mNameTextView.setVisibility(View.GONE);
        }
    }

    private void saveDefaultImage(int position) {
        int resId = 0;
        switch (position) {
            case 1:
                resId = R.drawable.img_signup_photos_01;
                break;
            case 2:
                resId = R.drawable.img_signup_photos_02;
                break;
            case 3:
                resId = R.drawable.img_signup_photos_03;
                break;
            case 4:
                resId = R.drawable.img_signup_photos_04;
                break;
            case 5:
                resId = R.drawable.img_signup_photos_05;
                break;
            case 6:
                resId = R.drawable.img_signup_photos_06;
                break;
            default:
                break;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        File f = new File(mPath);
        if (f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(mPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int left = 0;
        int right = 0;
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.share_btn:
                Utils.sendSNS(this);
                break;
            case R.id.profile_image_view:
                PopupDialog dialog = new PopupDialog(this, new PopupDialog.OnPopupListener() {
                    @Override
                    public void onAlbum() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PHOTO);
                    }

                    @Override
                    public void onDefault() {
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        startActivityForResult(intent, IMAGE);
                    }
                });
                dialog.show();
                break;
            case R.id.connect_off_btn:
                ReceiveDeviceDataService.isrunconnect = false;
                intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.connect_on_btn:
                ReceiveDeviceDataService.isrunconnect = false;
                BTLinkerUtils.disconnect();
                isConnect = false;
                refreshConnectView(isConnect);
                break;
            case R.id.push_btn:
                if (isPush) {
                    isPush = false;
                    mPushBtn.setBackgroundResource(R.drawable.btn_switch_off);
                } else {
                    isPush = true;
                    mPushBtn.setBackgroundResource(R.drawable.btn_switch_on);
                }
                PrefUtils.setPush(getApplicationContext(), isPush);
                break;
            case R.id.hand_btn:
                if (!isHand) {
                    isHand = true;

                    mHandBtn.setBackgroundResource(R.drawable.btn_check_auto);
                    mHandBtn.setTextColor(Color.parseColor("#31c3be"));
                    left = mHandBtn.getPaddingLeft();
                    right = mHandBtn.getPaddingRight();
                    mHandBtn.setPadding(left, 0, right, 0);

                    mAutoBtn.setBackgroundResource(R.drawable.btn_check_hand);
                    mAutoBtn.setTextColor(Color.parseColor("#666666"));

                    left = mAutoBtn.getPaddingLeft();
                    right = mAutoBtn.getPaddingRight();
                    mAutoBtn.setPadding(left, 0, right, 0);

                    PrefUtils.setCheck(getApplicationContext(), isHand);
                    NotiService.stopTimer();
                }
                break;
            case R.id.auto_btn:
                if (isHand) {
                    isHand = false;

                    mHandBtn.setBackgroundResource(R.drawable.btn_check_hand);
                    mHandBtn.setTextColor(Color.parseColor("#666666"));
                    left = mHandBtn.getPaddingLeft();
                    right = mHandBtn.getPaddingRight();
                    mHandBtn.setPadding(left, 0, right, 0);

                    mAutoBtn.setBackgroundResource(R.drawable.btn_check_auto);
                    mAutoBtn.setTextColor(Color.parseColor("#31c3be"));

                    left = mAutoBtn.getPaddingLeft();
                    right = mAutoBtn.getPaddingRight();
                    mAutoBtn.setPadding(left, 0, right, 0);

                    PrefUtils.setCheck(getApplicationContext(), isHand);
                    NotiService.setTimer();
                }
                break;
            case R.id.home_btn:
                finish();
                break;
            case R.id.monitor_btn:
                intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.icare_btn:
                intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == PHOTO) {
            crop(data.getData());
        } else if (data != null && requestCode == CROP) {
            saveImage();
            setImage();
            deleteImage();
        } else if (data != null && requestCode == IMAGE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", 0);
//                ELog.e(null, "position : " + position);
                saveDefaultImage(position);
                saveImage();
                setImage();
                deleteImage();
            }
        }
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "settingActivity getBluetoothData");
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
        ELog.e(null, "SettingActivity getBluetoothConnectState : " + b);
        final boolean isConnect = b;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshConnectView(isConnect);
            }
        });

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
