package com.oosl.colorostool.plugin;

import android.annotation.SuppressLint;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.CosApkName;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAndroid extends HookBase{
    private static final String tag = "Android";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        if (ColorToolPrefs.getPrefs("usb_install", false)) {
            usbInstall(lpparam);
        }


        Log.d(tag, "Hook android success!");
    }

    public void usbInstall(XC_LoadPackage.LoadPackageParam lpparam){
        ClassLoader classLoader = lpparam.classLoader;
        Log.d(tag,"Patch coloros usb alert START");
        String className;
        if (CosApkName.isCos12()){
            className = "com.android.server.pm.OplusPackageInstallInterceptManager";
        }else {
            className = "com.android.server.pm.ColorPackageInstallInterceptManager";
        }
        XposedHelpers.findAndHookMethod(className, classLoader, "allowInterceptAdbInstallInInstallStage", int.class, "android.content.pm.PackageInstaller$SessionParams", "java.io.File", "java.lang.String", "android.content.pm.IPackageInstallObserver2", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }
        });
        Log.d(tag,"Patch coloros usb alert END");

    }


}
