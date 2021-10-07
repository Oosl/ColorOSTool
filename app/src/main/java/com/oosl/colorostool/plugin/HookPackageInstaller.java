package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.Log;

import android.annotation.SuppressLint;
import android.content.Context;

import com.oosl.colorostool.util.ColorToolPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookPackageInstaller extends HookBase{

    private static final String tag = "PackageInstaller";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        //packageInstallerLog(lpparam);
        if(ColorToolPrefs.getPrefs("safe_installer", true)){
            removeVerify(lpparam);
        }
        if(ColorToolPrefs.getPrefs("aosp_installer", false)) {
            replaceInstaller(lpparam);
        }
        if (ColorToolPrefs.getPrefs("installer_warn", false)){
            removeWarn(lpparam);
        }
        Log.d(tag,"Hook packageinstaller success!");
    }

    @SuppressLint("PrivateApi")
    private void removeVerify(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz;
        try {
            clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
            // account verify
            XposedHelpers.findAndHookMethod(clazz, "startAccountVerification", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(tag, "replace startAccountVerification() OK!!");
                    XposedHelpers.callMethod(param.thisObject, "continueAppInstall");
                    return null;
                }
            });

            //app detail
            XposedHelpers.findAndHookMethod(clazz, "preSafeInstall", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.callMethod(param.thisObject,"startSafeInstall");
                    return null;
                }
            });

            //apk scan
            XposedHelpers.findAndHookMethod(clazz, "checkToScanRisk", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(tag,"replace checkToScanRisk OK!!");
                    XposedHelpers.callMethod(param.thisObject,"initiateInstall");
                    return null;
                }
            });

        }catch (Exception e){
            Log.error(tag, e);
        }
    }

    @SuppressLint("PrivateApi")
    private void removeWarn(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz;
        try {
            clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
        }catch (Exception e){
            return;
        }
        XposedHelpers.findAndHookMethod(clazz, "showDialogInner", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = 0;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedHelpers.callMethod(param.thisObject,"continueAppInstall");
            }
        });
    }

    // 使用原生安装器而非OPPO自己写的
    @SuppressLint("PrivateApi")
    private void replaceInstaller(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz;
        try {
            clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.FeatureOption");
            Log.d(tag,"sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
            XposedHelpers.findAndHookMethod(clazz, "setIsClosedSuperFirewall", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setStaticBooleanField(clazz, "sIsClosedSuperFirewall", true);
                    //ColorOSToolLog(tag,"after sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
                }
            });
        }catch (Exception e){
            Log.error(tag, e);
        }
    }

    @Override
    @SuppressLint("PrivateApi")
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        if (!enableLog) return;
        Class<?> clazz;
        try {
            clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.OppoLog");
            XposedHelpers.setStaticBooleanField(clazz, "DEVELOP_MODE", true);
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }
}
