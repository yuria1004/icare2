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
public class TermDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private OnNumberListener mListener;

    private Button mDayBtn;
    private Button mWeekBtn;
    private Button mMonthBtn;
    private Button mCloseBtn;

    public TermDialog(Context context, OnNumberListener listener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_term);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mContext = context;
        mListener = listener;

        setUpView();
    }

    private void setUpView() {
        mDayBtn = (Button) findViewById(R.id.day_btn);
        mWeekBtn = (Button) findViewById(R.id.week_btn);
        mMonthBtn = (Button) findViewById(R.id.month_btn);
        mCloseBtn = (Button) findViewById(R.id.close_btn);

        mDayBtn.setOnClickListener(this);
        mWeekBtn.setOnClickListener(this);
        mMonthBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int number;
        switch (v.getId()) {
            case R.id.day_btn:
                number = 0;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.week_btn:
                number = 1;
                mListener.onNumber(number);
                dismiss();
                break;
            case R.id.month_btn:
                number = 2;
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
