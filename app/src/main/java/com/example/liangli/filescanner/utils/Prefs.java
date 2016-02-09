package com.example.liangli.filescanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liangli on 2/9/16.
 *
 * Gerenal preferences
 */

public class Prefs {
    private static final String UI_PREFS_NAME = "com.example.liangli.filescanner.prefs";

    private Prefs() {};

    public static final SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(UI_PREFS_NAME, Context.MODE_PRIVATE);
    }
}
