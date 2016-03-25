package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class InfoActivity extends BaseActivity implements View.OnClickListener, BtlinkerDataListener {

    private ImageButton mShareBtn;
    private Button mInfoBtn;
    private ImageButton mHomeBtn;
    private ImageButton mMonitorBtn;
    private ImageButton mSettingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setUpView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        ReceiveDeviceDataService.setBtlinkerDataListener(this);
    }

    private void setUpView() {
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mInfoBtn = (Button) findViewById(R.id.info_btn);
        mHomeBtn = (ImageButton) findViewById(R.id.home_btn);
        mMonitorBtn = (ImageButton) findViewById(R.id.monitor_btn);
        mSettingBtn = (ImageButton) findViewById(R.id.setting_btn);

        mShareBtn.setOnClickListener(this);
        mInfoBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mMonitorBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.share_btn:
                Utils.sendSNS(this);
                break;
            case R.id.info_btn:
                intent = new Intent(getApplicationContext(), WebActivity.class);
                startActivity(intent);
                break;
            case R.id.home_btn:
                finish();
                break;
            case R.id.monitor_btn:
                intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.setting_btn:
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "infoActivity getBluetoothData");
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
        ELog.e(null, "infoActivity getBluetoothConnectState : " + b);
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
