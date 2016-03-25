package kr.co.ethree.icare.activity;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xrz.lib.bluetooth.BtlinkerDataListener;
import com.xrz.lib.bluetooth.ReceiveDeviceDataService;

import java.util.Map;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.IcareItem;
import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;
import kr.co.ethree.icare.utils.Utils;

public class WebActivity extends BaseActivity implements BtlinkerDataListener {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setUpView();

        ReceiveDeviceDataService.setBtlinkerDataListener(this);
    }

    private void setUpView() {
        mWebView = (WebView) findViewById(R.id.web_view);

        mWebView.setWebViewClient(new WebClient());
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setSupportMultipleWindows(true);
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.loadUrl("http://www.icaremom.co.kr");
    }
    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(WebActivity.this);

            WebView.WebViewTransport transport = (WebView.WebViewTransport)resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void getBluetoothData(Map<String, String> map) {
//        super.getBluetoothData(map);
//        ELog.e(null, "webActivity getBluetoothData");
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
        ELog.e(null, "webActivity getBluetoothConnectState : " + b);
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
