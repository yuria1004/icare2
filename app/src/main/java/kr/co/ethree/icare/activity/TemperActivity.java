package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import kr.co.ethree.icare.Icare;
import kr.co.ethree.icare.R;
import kr.co.ethree.icare.adapter.ViewPagerAdapter;
import kr.co.ethree.icare.data.BehaviorItem;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class TemperActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BtlinkerDataListener {

    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private LinearLayout mBaseLayout;
    private ImageView mProfileImageView;
    private ImageView mStepImageView;
    private ImageView mIconImageView;
    private TextView mNumTextView;
    private ViewPager mViewPager;
    private LinearLayout mMarkLayout;

    private Bitmap mBitmap;

    private String mPath;
    private int mPrePosition;
    private ArrayList<BehaviorItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temper);

        setUpView();
        setUpData();
//        setPagerAdapter();
//        setPageMark();
    }

    private void setUpView() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mBaseLayout = (LinearLayout) findViewById(R.id.base_layout);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image_view);
        mStepImageView = (ImageView) findViewById(R.id.step_image_view);
        mIconImageView = (ImageView) findViewById(R.id.icon_image_view);
        mNumTextView = (TextView) findViewById(R.id.num_text_view);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mMarkLayout = (LinearLayout) findViewById(R.id.mark_layout);

        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    private void setUpData() {
        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        mPath = getFilesDir() + "/" + Icare.PROFILE;

        setImage();

        if (ReceiveDeviceDataService.m_bConnected) {
            ReceiveDeviceDataService.SendHumitureCommand();
        }

        IcareItem item = getLastIcare();

        if (item != null) {
            String temper = item.temper;

            if (temper != null) {
                setTemper(temper);
                setPagerAdapter(temper);
                setPageMark();
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

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 204, getResources().getDisplayMetrics());
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, size, size, false);
        mBitmap = Utils.getCircleBitmap(bitmap2);

        mProfileImageView.setImageBitmap(mBitmap);
        bitmap.recycle();
        bitmap2.recycle();
    }

    private void setTemper(String temper) {
        int step = Utils.getTemperStep(temper);

        switch (step) {
            case 1:
                mBaseLayout.setBackgroundResource(R.drawable.bg_temperature_low);
                mStepImageView.setBackgroundResource(R.drawable.img_photos_cover_thi_usually);
                mIconImageView.setBackgroundResource(R.drawable.ic_photo_temperature_low);
                break;
            case 2:
                mBaseLayout.setBackgroundResource(R.drawable.bg_temperature_proper);
                mStepImageView.setBackgroundResource(R.drawable.img_photos_cover_temperature_proper);
                mIconImageView.setBackgroundResource(R.drawable.ic_photo_temperature_proper);
                break;
            case 3:
                mBaseLayout.setBackgroundResource(R.drawable.bg_temperature_high);
                mStepImageView.setBackgroundResource(R.drawable.img_photos_cover_thi_bad);
                mIconImageView.setBackgroundResource(R.drawable.ic_photo_temperature_high);
                break;
            default:
                break;
        }

        double num = Double.parseDouble(temper);
        ELog.e(null, "num : " + num);
        int num2 = (int) num;
        ELog.e(null, "num2 : " + num2);
        mNumTextView.setText(String.valueOf(num2));
    }

    private void setPagerAdapter(String temper) {
        mItems = new ArrayList();

        int step = Utils.getTemperStep(temper);

        String[] text = null;
        ArrayList<Integer> resIds = new ArrayList();
        int resId;
        switch (step) {
            case 1:
                text = getResources().getStringArray(R.array.temper_list_1);

                resId = R.drawable.ic_behavior_temperature_low01;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_low02;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_low03;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_low04;
                resIds.add(resId);
                break;
            case 2:
                text = getResources().getStringArray(R.array.temper_list_2);

                resId = R.drawable.ic_behavior_temperature_proper01;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_proper02;
                resIds.add(resId);
                break;
            case 3:
                text = getResources().getStringArray(R.array.temper_list_3);

                resId = R.drawable.ic_behavior_temperature_high01;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_high02;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_high03;
                resIds.add(resId);

                resId = R.drawable.ic_behavior_temperature_high04;
                resIds.add(resId);
                break;
            default:
                break;
        }

        for (int i = 0; i < resIds.size(); i++) {
            BehaviorItem item = new BehaviorItem();
            item.icon = resIds.get(i);
            item.text = text[i];
            mItems.add(item);
        }

        mViewPager.setAdapter(new ViewPagerAdapter(this, R.layout.item_viewpager, mItems));
        mViewPager.addOnPageChangeListener(this);
    }

    private void setPageMark() {
        mMarkLayout.removeAllViews();
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.ic_dot_paging_n);
            } else {
                imageView.setBackgroundResource(R.drawable.ic_dot_paging_p);
            }
            mMarkLayout.addView(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.share_btn:
                Utils.sendSNS(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mMarkLayout.getChildAt(mPrePosition).setBackgroundResource(R.drawable.ic_dot_paging_p);
        mMarkLayout.getChildAt(position).setBackgroundResource(R.drawable.ic_dot_paging_n);
        mPrePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void getBluetoothData(Map<String, String> map) {
        ELog.e(null, "TemperActivity getBluetoothData");
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
                    setTemper(temper);
                    setPagerAdapter(temper);
                    setPageMark();
                }
            });
            mTemper = temper;
            mHum = hum;
        }

        final String uv = map.get("uv");
        if (uv != null) {
            mUv = uv;
            mDate = Utils.getDateTime();
            mIndex = Utils.getIndex(mTemper, mHum);
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
        ELog.e(null, "temperActivity getBluetoothConnectState : " + b);
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
