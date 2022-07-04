package com.oosl.colorostool.plugin;

import android.content.ComponentName;

import com.oosl.colorostool.plugin.base.HookBase;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAndroid extends HookBase {

    private static final String tag = "Android";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        ClassLoader classLoader = lpparam.classLoader;
        if (ColorToolPrefs.getPrefs("usb_install", false)) {
            usbInstall(classLoader);
        }
        if (ColorToolPrefs.getPrefs("remove_oppo_default_app", false)) {
            removeForceApp(classLoader);
        }
        Log.d(tag, "Hook android success!");
    }

    public void usbInstall(ClassLoader classLoader) {

        Log.d(tag, "Patch coloros usb alert START");
        XposedHelpers.findAndHookMethod("com.android.server.pm.OplusPackageInstallInterceptManager", classLoader, "allowInterceptAdbInstallInInstallStage", int.class, "android.content.pm.PackageInstaller$SessionParams", File.class, String.class, "android.content.pm.IPackageInstallObserver2", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }
        });
        Log.d(tag, "Patch coloros usb alert END");
    }

    public void removeForceApp(ClassLoader classLoader){
        Log.d(tag, "Patch coloros removeForceApp START");
        XposedHelpers.findAndHookMethod("com.android.server.pm.OplusOsPackageManagerHelper", classLoader, "isOplusForceApp", ComponentName.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return false;
            }
        });
        Log.d(tag, "Patch coloros removeForceApp END");
    }
}