package kr.co.ethree.icare.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.ethree.icare.utils.ELog;
import kr.co.ethree.icare.utils.PrefUtils;

public class NotiService extends Service {

    private final static String ACTION_NOTI = "kr.co.ethree.icare.ACTION_NOTI";
    private static Context mContext;
    private static Timer timer;

    public NotiService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ELog.e(null, "service onStartCommand()");
        mContext = this;
        setTimer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ELog.e(null, "service onDestroy()");
        stopTimer();
    }

    public static void setTimer() {
        boolean isHand = PrefUtils.isHand(mContext);
        if (!isHand) {
            ELog.e(null, "service setTimer()");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    ELog.e(null, "TimerTask");
                    Intent intent = new Intent();
                    intent.setAction(ACTION_NOTI);
                    mContext.sendBroadcast(intent);
                }
            };
            timer = new Timer();
            timer.schedule(task, 300000, 300000);
        }
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
