package com.example.liangli.filescanner.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liangli.filescanner.R;
import com.example.liangli.filescanner.helpers.StatisticResult;

import java.util.List;

/**
 * Created by liangli on 2/9/16.
 *
 * Adapter to let user list the result into the listview in main activity
 */
public class MyListViewAdapter extends BaseAdapter {

    private static final String TAG = "MyLstAdapter";

    private Context mContext;
    private List<StatisticResult> mResultList;
    private LayoutInflater mInflator;
    private TextView mFileName, mFileSize;

    public MyListViewAdapter(Context context, List<StatisticResult> fileList) {
        mContext = context;
        mResultList = fileList;
        Log.d(TAG, "file list size: " + fileList.size());
        mInflator = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "position: " + position);
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.list_entry, null);
        }
        mFileName = (TextView) convertView.findViewById(R.id.file_name);
        mFileSize = (TextView) convertView.findViewById(R.id.file_size);
        mFileName.setText(mResultList.get(position).getColume_1());
        mFileSize.setText(mResultList.get(position).getColume_2());
        return convertView;
    }
}
