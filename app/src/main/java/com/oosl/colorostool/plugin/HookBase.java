package com.oosl.colorostool.plugin;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

abstract class HookBase {

    public void hook(){}

    public void hook(XC_LoadPackage.LoadPackageParam lpparam){}
}
