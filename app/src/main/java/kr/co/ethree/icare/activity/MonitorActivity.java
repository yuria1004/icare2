package kr.co.ethree.icare.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.ArrayList;
import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;
import kr.co.ethree.icare.widget.MyMarkerView;
import kr.co.ethree.icare.widget.SensorDialog;
import kr.co.ethree.icare.widget.TermDialog;

public class MonitorActivity extends BaseActivity implements View.OnClickListener, OnChartValueSelectedListener, BtlinkerDataListener {

    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private Button mTermBtn;
    private Button mSensorBtn;
    private TextView mSensorTextView;
    private TextView mStatusTextView;
    private TextView mNumTextView;
    private ImageView mIconImageView;
    private ImageButton mHomeBtn;
    private ImageButton mIcareBtn;
    private ImageButton mSettingBtn;
    private LineChart mLineChart;

    private ArrayList<IcareItem> mItems;

    private int mNum;
    private String mStatus;
    private int mDateType;
    private int mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        setUpView();
        setUpData();
    }

    private void setUpView() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mShareBtn = (ImageButton) findViewById(R.id.share_btn);
        mTermBtn = (Button) findViewById(R.id.term_btn);
        mSensorBtn = (Button) findViewById(R.id.sensor_btn);
        mSensorTextView = (TextView) findViewById(R.id.sensor_text_view);
        mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        mNumTextView = (TextView) findViewById(R.id.num_text_view);
        mIconImageView = (ImageView) findViewById(R.id.icon_image_view);
        mHomeBtn = (ImageButton) findViewById(R.id.home_btn);
        mIcareBtn = (ImageButton) findViewById(R.id.icare_btn);
        mSettingBtn = (ImageButton) findViewById(R.id.setting_btn);
        mLineChart = (LineChart) findViewById(R.id.graph);

        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mTermBtn.setOnClickListener(this);
        mSensorBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mIcareBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
    }

    private void setUpData() {
        ReceiveDeviceDataService.setBtlinkerDataListener(this);

        setTermText(0);
        setSensorText(0);

        mItems = getIcareDay();

        mLineChart.setViewPortOffsets(0, 54, 0, 0);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getXAxis().setEnabled(false);
        mLineChart.setDescription("");

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // set the marker to the chart
        mLineChart.setMarkerView(mv);
        mLineChart.setOnChartValueSelectedListener(this);

        mLineChart.getLegend().setEnabled(false);

//        setTemperGraphData();

        // dont forget to refresh the drawing

    }

    private void setTermText(int num) {
        mDateType = num;
        clearData();
        switch (num) {
            case 0:
                mTermBtn.setText("기간 : 일");
                break;
            case 1:
                mTermBtn.setText("기간 : 주");
                break;
            case 2:
                mTermBtn.setText("기간 : 월");
                break;
            default:
                break;
        }

        setDateDB();

        if (mSensor == 0) {
            setTemperGraphData();
        } else if (mSensor == 1) {
            setHumGraphData();
        } else if (mSensor == 2) {
            setUvGraphData();
        } else if (mSensor == 3) {
            setIndexGraphData();
        }

    }

    private void setSensorText(int num) {
        mSensor = num;
        clearData();
        switch (num) {
            case 0:
                mSensorBtn.setText("센서 : 온도");
                mSensorTextView.setText("온도");
                mIconImageView.setVisibility(View.VISIBLE);
                mIconImageView.setImageResource(R.drawable.img_text_c);

                setDateDB();
                setTemperGraphData();
                break;
            case 1:
                mSensorBtn.setText("센서 : 습도");
                mSensorTextView.setText("습도");
                mIconImageView.setVisibility(View.VISIBLE);
                mIconImageView.setImageResource(R.drawable.img_text_percent);

                setDateDB();
                setHumGraphData();
                break;
            case 2:
                mSensorBtn.setText("센서 : 자외선");
                mSensorTextView.setText("자외선");
                mIconImageView.setVisibility(View.GONE);

                setDateDB();
                setUvGraphData();
                break;
            case 3:
                mSensorBtn.setText("센서 : 내 아이 지수");
                mSensorTextView.setText("내 아이 지수");
                mIconImageView.setVisibility(View.GONE);

                setDateDB();
                setIndexGraphData();
                break;
            default:
                break;

        }
    }

    private void setData(ArrayList<String> date, ArrayList<String> temper) {

        ArrayList<Entry> vals1 = new ArrayList();

        for (int i = 0; i < temper.size(); i++) {
            float val;
            if (temper.get(i) == null) {
                val = 0;
            } else {
                val = Float.valueOf(temper.get(i));
            }
            vals1.add(new Entry(val, i, date.get(i)));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(2.0f);
        set1.setHighLightColor(Color.rgb(241, 245, 251));
        set1.setColor(Color.parseColor("#523bbf"));
        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setFillFormatter(new FillFormatter() {
//            @Override
//            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                return -10;
//            }
//        });
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_graph);
        set1.setFillDrawable(drawable);
        set1.setDrawFilled(true);

        // create a data object with the datasets
        LineData data = new LineData(date, set1);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mLineChart.setData(data);

        mLineChart.animateXY(2000, 2000);

        mLineChart.invalidate();
    }

    private void setTemperGraphData() {
        if (mItems != null && !mItems.isEmpty()) {
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> tempers = new ArrayList<>();
            for (int i = 0; i < mItems.size(); i++) {
                String date = mItems.get(i).date;
                String temper = mItems.get(i).temper;
                ELog.e(null, "date : " + date + " , temper : " + temper);
                dates.add(date);
                tempers.add(temper);
            }
            setData(dates, tempers);
        }
    }

    private void setHumGraphData() {
        if (mItems != null && !mItems.isEmpty()) {
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> hums = new ArrayList<>();
            for (int i = 0; i < mItems.size(); i++) {
                String date = mItems.get(i).date;
                String hum = mItems.get(i).hum;
                ELog.e(null, "date : " + date + " , hum : " + hum);
                dates.add(date);
                hums.add(hum);
            }
            setData(dates, hums);
        }
    }

    private void setUvGraphData() {
        if (mItems != null && !mItems.isEmpty()) {
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> uvs = new ArrayList<>();
            for (int i = 0; i < mItems.size(); i++) {
                String date = mItems.get(i).date;
                String uv = mItems.get(i).uv;
                ELog.e(null, "date : " + date + " , uv : " + uv);
                dates.add(date);
                uvs.add(uv);
            }
            setData(dates, uvs);
        }
    }

    private void setIndexGraphData() {
        if (mItems != null && !mItems.isEmpty()) {
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> indexs = new ArrayList<>();
            for (int i = 0; i < mItems.size(); i++) {
                String date = mItems.get(i).date;
                String index = mItems.get(i).care;
                ELog.e(null, "date : " + date + " , index : " + index);
                dates.add(date);
                indexs.add(index);
            }
            setData(dates, indexs);
        }
    }

    private void setDateDB() {
        if (mDateType == 0) {
            mItems = getIcareDay();
        } else if (mDateType == 1) {
            mItems = getIcareWeek();
        } else if (mDateType == 2) {
            mItems = getIcareMonth();
        }
    }

    private void clearData() {
        mStatusTextView.setText("");
        mNumTextView.setText("0");
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
            case R.id.term_btn:
                TermDialog dialog = new TermDialog(this, new TermDialog.OnNumberListener() {
                    @Override
                    public void onNumber(int num) {
                        setTermText(num);
                    }
                });
                dialog.show();
                break;
            case R.id.sensor_btn:
                SensorDialog sensorDialog = new SensorDialog(this, new SensorDialog.OnNumberListener() {
                    @Override
                    public void onNumber(int num) {
                        setSensorText(num);
                    }
                });
                sensorDialog.show();
                break;
            case R.id.home_btn:
                finish();
                break;
            case R.id.icare_btn:
                intent = new Intent(getApplicationContext(), InfoActivity.class);
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

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        ELog.e(null, "entry : " + e.getVal());
        mNum = Math.round(e.getVal());
        mNumTextView.setText(String.valueOf(mNum));
        int step;
        String stepString = null;

        switch (mSensor) {
            case 0:
                step = Utils.getTemperStep(String.valueOf(mNum));

                if (step == 1) {
                    stepString = "낮음";
                } else if (step == 2) {
                    stepString = "적정";
                } else if (step == 3) {
                    stepString = "높음";
                }
                break;

            case 1:
                step = Utils.getHumStep(String.valueOf(mNum));

                if (step == 1) {
                    stepString = "낮음";
                } else if (step == 2) {
                    stepString = "적정";
                } else if (step == 3) {
                    stepString = "높음";
                }
                break;

            case 2:
                step = Utils.getUvStep(String.valueOf(mNum));

                if (step == 1) {
                    stepString = "낮음";
                } else if (step == 2) {
                    stepString = "보통";
                } else if (step == 3) {
                    stepString = "높음";
                } else if (step == 4) {
                    stepString = "높음";
                } else if (step == 5) {
                    stepString = "위험";
                }
                break;

            case 3:
                step = Utils.getIndexStep(mNum);

                if (step == 1) {
                    stepString = "보통";
                } else if (step == 2) {
                    stepString = "나쁨";
                } else if (step == 3) {
                    stepString = "나쁨";
                }
                break;

            default:
                break;
        }

        mStatusTextView.setText(stepString);
    }

    @Override
    public void onNothingSelected() {

    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "monitorActivity getBluetoothData");
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
        ELog.e(null, "monitorActivity getBluetoothConnectState : " + b);
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
