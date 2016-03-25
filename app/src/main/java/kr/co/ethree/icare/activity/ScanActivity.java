package kr.co.ethree.icare.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xrz.lib.bluetooth.BTLinkerUtils;
import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.ArrayList;
import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.adapter.BtAdapter;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.EToast;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;
import kr.co.ethree.icare.widget.MaxHeightListView;

public class ScanActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, BtlinkerDataListener {

    private ImageButton mBackBtn;
    private TextView mFailTextView;
    private TextView mDiscTextView;
    private MaxHeightListView mListView;
    private Button mScanBtn;
    private Button mCancelBtn;

    private BluetoothAdapter mBtAdapter;
    private BtAdapter mAdapter;

    private ArrayList<BluetoothDevice> mItems;
    private String mName;
    private BluetoothDevice mDevice;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setUpView();
        setUpData();
    }

    @Override
    protected void onDestroy() {
        if (mBtAdapter != null) {
            mBtAdapter.stopLeScan(scanCallback);
        }

        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        super.onDestroy();
    }

    private void setUpView() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mFailTextView = (TextView) findViewById(R.id.fail_text_view);
        mDiscTextView = (TextView) findViewById(R.id.disc_text_view);
        mListView = (MaxHeightListView) findViewById(R.id.list_view);
        mScanBtn = (Button) findViewById(R.id.scan_btn);
        mCancelBtn = (Button) findViewById(R.id.cancel_btn);

        mBackBtn.setOnClickListener(this);
        mScanBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        mListView.setOnItemClickListener(this);
    }

    private void setUpData() {
        ReceiveDeviceDataService.setBtlinkerDataListener(this);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("아이케어 기기를 연결 하는 중 입니다.");

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = manager.getAdapter();

        setAdapter();
    }

    private void setAdapter() {
        mItems = new ArrayList();
        mAdapter = new BtAdapter(getApplicationContext(), R.layout.item_scan, mItems);
        mListView.setAdapter(mAdapter);
    }

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            ELog.e(null, "onLeScan");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = device.getName();
                    if (name != null && name.contains("BTL")) {
                        mAdapter.addDevice(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.scan_btn:
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                if (mBtAdapter != null) {
                    mBtAdapter.startLeScan(scanCallback);
                }
                mFailTextView.setVisibility(View.GONE);
                mDiscTextView.setText(R.string.scan_text2);
                mScanBtn.setVisibility(View.GONE);
                mCancelBtn.setVisibility(View.VISIBLE);

                mHandler.sendEmptyMessageDelayed(0, 30000);
                break;

            case R.id.cancel_btn:
                if (mBtAdapter != null) {
                    mBtAdapter.stopLeScan(scanCallback);
                }
                mScanBtn.setText(R.string.scan_btn2);
                mScanBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
                mHandler.removeMessages(0);
                break;

            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (mAdapter.getSize() == 0) {
                    mFailTextView.setVisibility(View.VISIBLE);
                    mDiscTextView.setText(R.string.scan_text3);
                }

                if (mBtAdapter != null) {
                    mBtAdapter.stopLeScan(scanCallback);
                }
                mScanBtn.setText(R.string.scan_btn2);
                mScanBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                BTLinkerUtils.connect(mDevice.getAddress());
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mAdapter.getDevice(position);
        if (device == null) {
            return;
        }

        mName = device.getName();
        mDevice = device;
        if (mBtAdapter != null) {
            mBtAdapter.stopLeScan(scanCallback);
        }

        try {
            mProgress.show();
            mHandler.sendEmptyMessageDelayed(1, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getBluetoothData(Map<String, String> map) {
        super.getBluetoothData(map);
        ELog.e(null, "scanActivity getBluetoothData");
        ELog.e(null, "data : " + map);

        if (PrefUtils.isHand(getApplicationContext()) && map.containsValue("8111B0")) {
            ReceiveDeviceDataService.SendHumitureCommand();
        }

        final String temper = map.get("Tem");
        final String hum = map.get("hum");
        if (temper != null && hum != null) {
            mTemper = temper;
            mHum = hum;
        }

        if (map.containsValue("8111B6")) {
            ReceiveDeviceDataService.sendUltravioletCommand();
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

    }

    @Override
    public void getBluetoothConnectState(boolean b) {
        ELog.e(null, "ScanActivity getBluetoothConnectState : " + b);
        if (b) {
            mProgress.dismiss();
            finish();
            PrefUtils.setName(getApplicationContext(), mName);
        } else {
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
            insertLocation();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EToast.show(ScanActivity.this, "연결에 실패하였습니다. 다시 시도해 주세요.", EToast.LENGTH_SHORT);
                }
            });
        }
    }
}
