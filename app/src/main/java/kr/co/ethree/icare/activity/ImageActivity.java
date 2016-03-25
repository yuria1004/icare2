package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.EToast;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class ImageActivity extends BaseActivity implements View.OnClickListener, BtlinkerDataListener {

    private ImageButton mCloseBtn;
    private RelativeLayout mSelectLayout1;
    private RelativeLayout mSelectLayout2;
    private RelativeLayout mSelectLayout3;
    private RelativeLayout mSelectLayout4;
    private RelativeLayout mSelectLayout5;
    private RelativeLayout mSelectLayout6;
    private ImageView mSelectImageView1;
    private ImageView mSelectImageView2;
    private ImageView mSelectImageView3;
    private ImageView mSelectImageView4;
    private ImageView mSelectImageView5;
    private ImageView mSelectImageView6;
    private Button mOkBtn;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setUpView();

        ReceiveDeviceDataService.setBtlinkerDataListener(this);
    }

    private void setUpView() {
        mCloseBtn = (ImageButton) findViewById(R.id.close_btn);
        mSelectLayout1 = (RelativeLayout) findViewById(R.id.select_layout1);
        mSelectLayout2 = (RelativeLayout) findViewById(R.id.select_layout2);
        mSelectLayout3 = (RelativeLayout) findViewById(R.id.select_layout3);
        mSelectLayout4 = (RelativeLayout) findViewById(R.id.select_layout4);
        mSelectLayout5 = (RelativeLayout) findViewById(R.id.select_layout5);
        mSelectLayout6 = (RelativeLayout) findViewById(R.id.select_layout6);
        mSelectImageView1 = (ImageView) findViewById(R.id.select_image_view1);
        mSelectImageView2 = (ImageView) findViewById(R.id.select_image_view2);
        mSelectImageView3 = (ImageView) findViewById(R.id.select_image_view3);
        mSelectImageView4 = (ImageView) findViewById(R.id.select_image_view4);
        mSelectImageView5 = (ImageView) findViewById(R.id.select_image_view5);
        mSelectImageView6 = (ImageView) findViewById(R.id.select_image_view6);
        mOkBtn = (Button) findViewById(R.id.ok_btn);

        mCloseBtn.setOnClickListener(this);
        mSelectLayout1.setOnClickListener(this);
        mSelectLayout2.setOnClickListener(this);
        mSelectLayout3.setOnClickListener(this);
        mSelectLayout4.setOnClickListener(this);
        mSelectLayout5.setOnClickListener(this);
        mSelectLayout6.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
    }

    private void setSelectImage(int position) {
        switch (position) {
            case 1:
                if (mSelectImageView1.getVisibility() == View.VISIBLE) {
                    mSelectImageView1.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView1.setVisibility(View.VISIBLE);
                }
                mSelectImageView2.setVisibility(View.GONE);
                mSelectImageView3.setVisibility(View.GONE);
                mSelectImageView4.setVisibility(View.GONE);
                mSelectImageView5.setVisibility(View.GONE);
                mSelectImageView6.setVisibility(View.GONE);
                break;
            case 2:
                if (mSelectImageView2.getVisibility() == View.VISIBLE) {
                    mSelectImageView2.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView2.setVisibility(View.VISIBLE);
                }
                mSelectImageView1.setVisibility(View.GONE);
                mSelectImageView3.setVisibility(View.GONE);
                mSelectImageView4.setVisibility(View.GONE);
                mSelectImageView5.setVisibility(View.GONE);
                mSelectImageView6.setVisibility(View.GONE);
                break;
            case 3:
                if (mSelectImageView3.getVisibility() == View.VISIBLE) {
                    mSelectImageView3.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView3.setVisibility(View.VISIBLE);
                }
                mSelectImageView1.setVisibility(View.GONE);
                mSelectImageView2.setVisibility(View.GONE);
                mSelectImageView4.setVisibility(View.GONE);
                mSelectImageView5.setVisibility(View.GONE);
                mSelectImageView6.setVisibility(View.GONE);
                break;
            case 4:
                if (mSelectImageView4.getVisibility() == View.VISIBLE) {
                    mSelectImageView4.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView4.setVisibility(View.VISIBLE);
                }
                mSelectImageView1.setVisibility(View.GONE);
                mSelectImageView2.setVisibility(View.GONE);
                mSelectImageView3.setVisibility(View.GONE);
                mSelectImageView5.setVisibility(View.GONE);
                mSelectImageView6.setVisibility(View.GONE);
                break;
            case 5:
                if (mSelectImageView5.getVisibility() == View.VISIBLE) {
                    mSelectImageView5.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView5.setVisibility(View.VISIBLE);
                }
                mSelectImageView1.setVisibility(View.GONE);
                mSelectImageView2.setVisibility(View.GONE);
                mSelectImageView3.setVisibility(View.GONE);
                mSelectImageView4.setVisibility(View.GONE);
                mSelectImageView6.setVisibility(View.GONE);
                break;
            case 6:
                if (mSelectImageView6.getVisibility() == View.VISIBLE) {
                    mSelectImageView6.setVisibility(View.GONE);
                    mPosition = 0;
                } else {
                    mSelectImageView6.setVisibility(View.VISIBLE);
                }
                mSelectImageView1.setVisibility(View.GONE);
                mSelectImageView2.setVisibility(View.GONE);
                mSelectImageView3.setVisibility(View.GONE);
                mSelectImageView4.setVisibility(View.GONE);
                mSelectImageView5.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                finish();
                break;
            case R.id.select_layout1:
                mPosition = 1;
                setSelectImage(mPosition);
                break;
            case R.id.select_layout2:
                mPosition = 2;
                setSelectImage(mPosition);
                break;
            case R.id.select_layout3:
                mPosition = 3;
                setSelectImage(mPosition);
                break;
            case R.id.select_layout4:
                mPosition = 4;
                setSelectImage(mPosition);
                break;
            case R.id.select_layout5:
                mPosition = 5;
                setSelectImage(mPosition);
                break;
            case R.id.select_layout6:
                mPosition = 6;
                setSelectImage(mPosition);
                break;
            case R.id.ok_btn:
                if (mPosition == 0) {
                    EToast.show(getApplicationContext(), "이미지를 선택해 주세요.", EToast.LENGTH_SHORT);
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("position", mPosition);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "imageActivity getBluetoothData");
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
//    }

    @Override
    public void getBluetoothConnectState(boolean b) {
        ELog.e(null, "imageActivity getBluetoothConnectState : " + b);
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
