package com.oosl.colorostool.plugin;

import com.oosl.colorostool.BuildConfig;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

abstract class HookBase {

    static final boolean enableLog = BuildConfig.DEBUG;

    public void hook() {
        if (enableLog) hookLog();
    }

    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (enableLog) hookLog(lpparam);
    }

    public void hookLog() {
    }

    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
    }
}
