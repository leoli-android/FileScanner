package com.example.liangli.filescanner.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangli.filescanner.R;
import com.example.liangli.filescanner.helpers.LocalFile;
import com.example.liangli.filescanner.fragments.FileScanFragment;
import com.example.liangli.filescanner.helpers.StatisticResult;
import com.example.liangli.filescanner.utils.Consts;
import com.example.liangli.filescanner.utils.MyListViewAdapter;
import com.example.liangli.filescanner.utils.MyNotificationManager;
import com.example.liangli.filescanner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,
        FileScanFragment.OnScanFinishedListener {

    private static final String TAG = "MainActivity";

    private static final String RESULT_BUNDLE_KEY = "com.example.liangli.filescanner.KEY_RESULT_BUNDLE";

    private TextView mStartButton, mShareButton;
    private ListView mListView;
    private int mStartButtonStatus;
    private FileScanFragment mFragment;
    private ArrayList<StatisticResult> mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        mFragment = new FileScanFragment();
    }


    /************************************
     * Initialization
     ************************************/

    private void initializeViews() {
        mStartButton = (TextView) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(this);
        mShareButton = (TextView) findViewById(R.id.share_button);
        mShareButton.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.statistic_list_view);

        mStartButtonStatus = Utils.getScanningStatusPref(this);
        initializeStartButtonStatus();
    }

    private void initializeStartButtonStatus() {
        mStartButton.setText(getResources().getString(R.string.start));
        mStartButton.setBackgroundColor(getResources().getColor(R.color.light_green));
        mShareButton.setClickable(true);
    }


    /************************************
     * Handle onClick events
     ************************************/

    public void onClickStartButton(View view) {
        if (!Utils.isSDCardAvailable()) {
            Toast.makeText(this, getResources().
                    getString(R.string.no_sd_card_warning), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mStartButtonStatus == Consts.STOP_STATUS) {
            mListView.setAdapter(null);
            mShareButton.setClickable(false);
            MyNotificationManager.sendNotification(this);
            mStartButton.setText(getResources().getString(R.string.stop));
            mStartButton.setBackgroundColor(getResources().getColor(R.color.light_red));
            mStartButtonStatus = Consts.START_STATUS;
            Utils.updateScanningStatusPref(this, Consts.START_STATUS);
            setFragment();
        } else {
            mShareButton.setClickable(true);
            MyNotificationManager.cancelNotification(this);
            mStartButton.setText(getResources().getString(R.string.start));
            mStartButton.setBackgroundColor(getResources().getColor(R.color.light_green));
            mStartButtonStatus = Consts.STOP_STATUS;
            mFragment.cancelCurrentTaskIfNecessary();
            removeFragment();
        }
    }

    public void onClickShareButton(View view) {
        Utils.onClickShareButton(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button :
                onClickStartButton(v);
                break;
            case R.id.share_button :
                onClickShareButton(v);
                break;
        }
    }


    /************************************
     * Add/Remove fragment
     ************************************/

    private void setFragment() {
        if (mFragment == null) {
            mFragment = new FileScanFragment();
        }
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentById(R.id.progress_fragment) == null) {
            fm.beginTransaction().add(R.id.progress_fragment, mFragment).commit();
        }
    }

    private void removeFragment() {
        if (mFragment == null) {
            mFragment = new FileScanFragment();
        }
        getFragmentManager().beginTransaction().remove(mFragment).commit();
    }


    /************************************
     * Communication chanel between activity and fragment
     ************************************/

    @Override
    public void onScanFinished(List<LocalFile> localFiles) {
        mResultList = Utils.composeStatisticalResult(localFiles);
        mListView.setAdapter(new MyListViewAdapter(this, mResultList));
        mStartButtonStatus = Consts.STOP_STATUS;
        Utils.updateScanningStatusPref(this, Consts.STOP_STATUS);
        initializeStartButtonStatus();
        MyNotificationManager.cancelNotification(this);
    }


    /************************************
     * Overide activity methods
     ************************************/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultList == null) return;
        outState.putParcelableArrayList(RESULT_BUNDLE_KEY, mResultList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mResultList = savedInstanceState.getParcelableArrayList(RESULT_BUNDLE_KEY);
            if (mResultList != null) {
                mListView.setAdapter(new MyListViewAdapter(this, mResultList));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFragment.onBackPressed();
    }
}
