package kr.co.ethree.icare.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.ethree.icare.R;

/**
 * Created by lee on 2016-02-15.
 */
public class SensorDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private OnNumberListener mListener;

    private Button mTermBtn;
    private Button mHumBtn;
    private Button mUvBtn;
    private Button mIndexBtn;
    private Button mCloseBtn;

    public SensorDialog(Context context, OnNumberListener listener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sensor);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mContext = context;
        mListener = listener;

        setUpView();
    }

    private void setUpView() {
        mTermBtn = (Button) findViewById(R.id.term_btn);
        mHumBtn = (Button) findViewById(R.id.hum_btn);
        mUvBtn = (Button) findViewById(R.id.uv_btn);
        mIndexBtn = (Button) findViewById(R.id.index_btn);
        mCloseBtn = (Button) findViewById(R.id.close_btn);

        mTermBtn.setOnClickListener(this);
        mHumBtn.setOnClickListener(this);
        mUvBtn.setOnClickListener(this);
        mIndexBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int number;
        switch (v.getId()) {
            case R.id.term_btn:
                number = 0;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.hum_btn:
                number = 1;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.uv_btn:
                number = 2;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.index_btn:
                number = 3;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.close_btn:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnNumberListener {
        void onNumber(int num);
    }
}
