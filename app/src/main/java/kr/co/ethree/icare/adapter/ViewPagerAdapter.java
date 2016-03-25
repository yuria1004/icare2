package kr.co.ethree.icare.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ethree.icare.R;
import kr.co.ethree.icare.data.BehaviorItem;
import kr.co.ethree.icare.utils.ELog;

/**
 * Created by lee on 2016-01-18.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mResource;
    private ArrayList<BehaviorItem> mItems;

    private ImageView mImageView;
    private TextView mTextView;

    public ViewPagerAdapter(Context context, int resource, ArrayList<BehaviorItem> items) {
        mContext = context;
        mResource = resource;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BehaviorItem item = mItems.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mResource, null);

        setUpView(view);
        setUpData(item);

        container.addView(view);

        return view;
    }

    private void setUpView(View v) {
        mImageView = (ImageView) v.findViewById(R.id.image_view);
        mTextView = (TextView) v.findViewById(R.id.text_view);
    }

    private void setUpData(BehaviorItem item) {
        mImageView.setImageResource(item.icon);
        mTextView.setText(item.text);
    }
}
