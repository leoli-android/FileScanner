package com.example.liangli.filescanner.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.liangli.filescanner.R;
import com.example.liangli.filescanner.helpers.LocalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileScanFragment extends Fragment {

    private static final String TAG = "FileScanFgmt";

    private static final String KEY_CURRENT_PROGRESS = "com.example.liangli.KEY_CURRENT_PROGRESS";

    private ProgressBar mProgreeBar;

    private boolean mIsFragmentVisible = false;
    private boolean mIsTaskRunning = false;
    private Activity mActivity;
    private FileScannerAsync mAsync;

    public FileScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsFragmentVisible = true;
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mProgreeBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "fragment onActivityCreated");
        if (mIsTaskRunning && savedInstanceState != null) {
            int progress = savedInstanceState.getInt(KEY_CURRENT_PROGRESS, 0);
            Log.d(TAG, "current progress: " + progress);
            mProgreeBar.setProgress(progress);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "fragment onSaveInstanceState, progress: " + mProgreeBar.getProgress());
        outState.putInt(KEY_CURRENT_PROGRESS, mProgreeBar.getProgress());
    }

    @Override
    public void onStart() {
        Log.d(TAG, "fragment onstart");
        super.onStart();
        if (!mIsTaskRunning) {
            Log.d(TAG, "recreate async");
            mAsync = new FileScannerAsync(getActivity());
            Log.d(TAG, "start to execute async");
            mAsync.execute();
        }
        mIsTaskRunning = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsFragmentVisible = false;
        Log.d(TAG, "fragment onDestroyView");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "fragment onStop");
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "fragment onAttach");
        super.onAttach(activity);
        mActivity = activity;
    }


    /************************************
     * Asynctask to scan SD files in background
     ************************************/

    private class FileScannerAsync extends AsyncTask<Void, Integer, Void> {

        private Context mContext;
        File mSDPath;
        List<File> mFList;
        private List<LocalFile> mFileList;

        public FileScannerAsync(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgreeBar.setVisibility(View.VISIBLE);
            mProgreeBar.setMax(100);
            mSDPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            mFList = new ArrayList<>();
            mFileList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            listFiles(mSDPath, mFList);
            int length = mFList.size();
            for (int i=0; i<mFList.size(); ++i) {
                if (isCancelled()) {
                    break;
                }
                String fileName = mFList.get(i).getName();
                publishProgress(i*100/length);
                mFileList.add(new LocalFile(fileName,
                        getFileExtension(fileName), mFList.get(i).length()));
            }

            /************************************
             * Some test code to make the background thread running longer
             *
             * Notice: while doing this test, some code in other files need also be modified.
             * Otherwise, some unexpected exceptions may cause crush.
             ************************************/
            /*
            for (int i=0; i<10; ++i) {
                try {
                    if (isCancelled()) {
                        break;
                    }
                    Thread.sleep(1000);
                    publishProgress((i+1)*10);
                } catch (InterruptedException e) {
                    Log.e(TAG, "exception: " + e);
                }
            }
            */
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mIsTaskRunning = false;
            removeSelfIfNecessary();
            try {
                ((OnScanFinishedListener) mActivity).onScanFinished(mFileList);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate: " + values[values.length - 1]);
            mProgreeBar.setProgress(values[values.length-1]);
            super.onProgressUpdate(values);
        }
    }


    /*
    *  Remove the fragment if view has been destroyed
    * */
    private void removeSelfIfNecessary() {
        if (mIsFragmentVisible) {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        } else {
            Log.w(TAG, "Fragment already stopped.");
        }
    }

    /*
    *  Recursively get all the nested files
    * */
    public void listFiles(File directoryName, List<File> files) {
        File[] fList = directoryName.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                File subFolder = new File(file.getAbsolutePath());
                File[] subFiles = subFolder.listFiles();
                if (subFiles != null && subFiles.length > 0) {
                    listFiles(subFolder, files);
                }
            }
        }
    }

    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    public interface OnScanFinishedListener {
        public void onScanFinished(List<LocalFile> localFiles);
    }

    /*
    *  Cancel the Asynctask ASAP
    * */
    public void cancelCurrentTaskIfNecessary() {
        if (mIsTaskRunning) {
            mAsync.cancel(true);
        }
    }

    /*
    *  On back pressed event. Passed from attached activity
    * */
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed, cancel current task");
        cancelCurrentTaskIfNecessary();
    }
}
