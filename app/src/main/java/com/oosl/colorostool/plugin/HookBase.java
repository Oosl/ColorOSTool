package com.oosl.colorostool.plugin;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

abstract class HookBase {

    static final boolean enableLog = true;

    public void hook(){
        hookLog();
    }

    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        hookLog(lpparam);
    }

    public void hookLog(){}

    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam){}
}
