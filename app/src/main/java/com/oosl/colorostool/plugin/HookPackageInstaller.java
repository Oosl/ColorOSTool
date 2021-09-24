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

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        hookPackageInstaller(lpparam);
    }

    @SuppressLint("PrivateApi")
    private void hookPackageInstaller(XC_LoadPackage.LoadPackageParam lpparam){
        String tag = "PackageInstaller";
        Log.d(tag,"Hook packageinstaller success!");
        // 去除安装前的验证
        Class<?> clazz0, clazz1;
        try {
            clazz0 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.AccountVerifyControl");
            clazz1 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
        }catch (Exception e){
            return;
        }
        if(ColorToolPrefs.getPrefs("safe_installer", true)){
            XposedHelpers.findAndHookMethod(clazz0, "needAccountVerify", Context.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(tag,"replace AccountVerify OK!!");
                    return false;
                }
            });
            XposedHelpers.findAndHookMethod(clazz1, "continueOppoSafeInstall", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(tag,"replace OppoSafeInstall() OK!!");
                    XposedHelpers.callMethod(param.thisObject,"continueAppInstall");
                    return null;
                }
            });
        }
        if (ColorToolPrefs.getPrefs("installer_warn", false)){
            XposedHelpers.findAndHookMethod(clazz1, "showDialogInner", int.class, new XC_MethodHook() {
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
        if(ColorToolPrefs.getPrefs("aosp_installer", false)) {
            Class<?> clazz;
            try {
                clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.FeatureOption");
            }catch (Exception e){
                return;
            }
            Log.d(tag,"sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
            XposedHelpers.findAndHookMethod(clazz, "setIsClosedSuperFirewall", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setStaticBooleanField(clazz, "sIsClosedSuperFirewall", true);
                    //ColorOSToolLog(tag,"after sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
                }
            });
        }
    }
}
