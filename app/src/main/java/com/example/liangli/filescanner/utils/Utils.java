package com.example.liangli.filescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.liangli.filescanner.R;
import com.example.liangli.filescanner.helpers.LocalFile;
import com.example.liangli.filescanner.helpers.StatisticResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangli on 2/9/16.
 *
 * Modules Working area
 */
public class Utils {

    private static final String TAG = "Utils";

    private static final String AVERAGE_FILE_SIZE_TAB = "The average file size is ";
    private static final String BIGEST_FILE_SIZE_TAB = "The bigest files are: ";
    private static final String POPULAR_FILE_EXTENSION = "The most frequent file extensions: ";
    private static final String BYTE_TAB = " bytes";
    private static final String FREQUENCY_TAB = " times";


    /*
    *  Always check if SD card is available before scanning
    */
    public static boolean isSDCardAvailable() {
        return android.os.Environment.
                getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /*
    *  Send intent to invoke all the apps in Android sharing menu
    */
    public static void onClickShareButton(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_msg));
        sendIntent.setType("*/*");
        context.startActivity(sendIntent);
    }

    /*
    * record the real time scanning status.
    */
    public static void updateScanningStatusPref(Context context, int status) {
        SharedPreferences.Editor editor = Prefs.getPreferences(context).edit();
        editor.putInt(Consts.START_BUTTON_STATUS_KEY, status).commit();
    }

    public static int getScanningStatusPref(Context context) {
        return Prefs.getPreferences(context).
                getInt(Consts.START_BUTTON_STATUS_KEY, Consts.STOP_STATUS);
    }

    /*
    *  Based on scanning result, do statistics.
    */
    public static ArrayList<StatisticResult> composeStatisticalResult(List<LocalFile> files) {
        ArrayList<StatisticResult> result = new ArrayList<>();
        if (files.size() == 0) return result;
        Map<String, Long> sizeMap = new HashMap<>();
        Map<String, Integer> extensionMap = new HashMap<>();
        long totalSize = 0;

        for (LocalFile file : files) {
            if (sizeMap.containsKey(file.getFileName())) {
                sizeMap.put(file.getFileName(),
                        sizeMap.get(file.getFileName()) > file.getFileSize() ?
                                sizeMap.get(file.getFileName()) : file.getFileSize());
            } else {
                sizeMap.put(file.getFileName(), file.getFileSize());
            }
            if (extensionMap.containsKey(file.getFileExtension())) {
                extensionMap.put(file.getFileExtension(), extensionMap.get(file.getFileExtension())+1);
            } else {
                extensionMap.put(file.getFileExtension(), 1);
            }
            totalSize += file.getFileSize();
        }

        result.add(new StatisticResult(BIGEST_FILE_SIZE_TAB, BYTE_TAB));
        sortBySize(sizeMap, result);
        addDivider(result);
        result.add(new StatisticResult(AVERAGE_FILE_SIZE_TAB, (totalSize / files.size()) + BYTE_TAB));
        addDivider(result);
        result.add(new StatisticResult(POPULAR_FILE_EXTENSION, FREQUENCY_TAB));
        sortByExtension(extensionMap, result);
        return result;
    }

    private static void sortBySize(Map<String, Long> sizeMap, final List<StatisticResult> result) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(sizeMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> lhs, Map.Entry<String, Long> rhs) {
                return (rhs.getValue()).compareTo(lhs.getValue());
            }
        });
        int length = list.size() > 10 ? 10 : list.size();
        for (int i=0; i<length; ++i) {
            result.add(new StatisticResult(list.get(i).getKey(), list.get(i).getValue()+""));
        }
    }

    private static void sortByExtension(Map<String, Integer> extensionMap, List<StatisticResult> result) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(extensionMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return (rhs.getValue()).compareTo(lhs.getValue());
            }
        });
        int length = list.size() > 5 ? 5 : list.size();
        for (int i=0; i<length; ++i) {
            result.add(new StatisticResult(list.get(i).getKey(), list.get(i).getValue()+""));
        }
    }

    private static void addDivider(List<StatisticResult> result) {
        result.add(new StatisticResult("", ""));
    }
}
