package com.oosl.colorostool.plugin;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

abstract class HookBase {

    public HookBase(){hook();}

    public HookBase(XC_LoadPackage.LoadPackageParam lpparam){
        hook(lpparam);
    }

    protected void hook(){}

    protected void hook(XC_LoadPackage.LoadPackageParam lpparam){}
}
