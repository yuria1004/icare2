package kr.co.ethree.icare.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ethree.icare.R;

/**
 * Created by lee on 2016-01-12.
 */
public class BtAdapter extends ArrayAdapter<BluetoothDevice> {

    private Context mContext;
    private int mResource;
    private ArrayList<BluetoothDevice> mItems;

    public BtAdapter(Context context, int resource, ArrayList<BluetoothDevice> items) {
        super(context, resource, items);

        mContext = context;
        mResource = resource;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice item = mItems.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, null);
            setUpView(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        setUpData(item, holder);
        return convertView;
    }

    private void setUpView(View v) {
        ViewHolder holder = new ViewHolder();
        holder.titleTextView = (TextView) v.findViewById(R.id.title_text_view);
        v.setTag(holder);
    }

    private void setUpData(BluetoothDevice item, ViewHolder holder) {
        if (item != null) {
            String name;
            if (item.getName() == null || item.getName().isEmpty()) {
                name = "unknown device";
            } else {
                name = item.getName();
            }
            holder.titleTextView.setText(name);
        }
    }

    public void addDevice(BluetoothDevice device) {
        if(!mItems.contains(device)) {
            mItems.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mItems.get(position);
    }

    public void clear() {
        mItems.clear();
    }

    public int getSize() {
        return mItems.size();
    }

    private final class ViewHolder {
        public TextView titleTextView;
    }
}
