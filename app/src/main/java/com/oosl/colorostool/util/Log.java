package com.oosl.colorostool.util;

import android.os.Build;

import de.robv.android.xposed.XposedBridge;

public class Log {

    private final static String TAG = "ColorOSTool";
    private static int debug = 1;

    //debug
    public static void d(String tag, String logMesg){
        if(debug == 1)
        XposedBridge.log("[" + TAG + "-"+ tag + "]: " + logMesg);
    }

    //normal
    public static void n(String tag, String logMesg){
        XposedBridge.log("[" + TAG + "-"+ tag + "]: " + logMesg);
    }
}
