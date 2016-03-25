package kr.co.ethree.icare.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.ethree.icare.R;

/**
 * Created by lee on 2016-02-15.
 */
public class PopupDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private OnPopupListener mListener;

    private Button mAlbumBtn;
    private Button mDefaultBtn;
    private Button mCloseBtn;

    public PopupDialog(Context context, OnPopupListener listener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_popup);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mContext = context;
        mListener = listener;

        setUpView();
    }

    private void setUpView() {
        mAlbumBtn = (Button) findViewById(R.id.album_btn);
        mDefaultBtn = (Button) findViewById(R.id.default_btn);
        mCloseBtn = (Button) findViewById(R.id.close_btn);

        mAlbumBtn.setOnClickListener(this);
        mDefaultBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_btn:
                mListener.onAlbum();
                dismiss();
                break;
            case R.id.default_btn:
                mListener.onDefault();
                dismiss();
                break;
            case R.id.close_btn:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnPopupListener {
        void onAlbum();
        void onDefault();
    }
}
