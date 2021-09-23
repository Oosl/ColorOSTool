package com.oosl.colorostool.util;

import com.oosl.colorostool.BuildConfig;

import de.robv.android.xposed.XSharedPreferences;

public final class ColorToolPrefs {
    private static XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "ColorToolPrefs");

    private ColorToolPrefs(){

    }

    public static boolean getPrefs(String prefsName, Boolean defaultValue){
        Log.d("prefs:", prefsName + ": " + prefs.getBoolean(prefsName, defaultValue));
        return prefs.getBoolean(prefsName, defaultValue);
    }
}
