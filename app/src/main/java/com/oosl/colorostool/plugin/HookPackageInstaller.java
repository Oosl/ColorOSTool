package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.Log;
import com.oosl.colorostool.util.ColorToolPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookPackageInstaller extends HookBase{

    private static final String tag = "PackageInstaller";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        if (ColorToolPrefs.getPrefs("safe_installer", true)) {
            removeVerify(lpparam);
        }
        if (ColorToolPrefs.getPrefs("aosp_installer", false)) {
            replaceInstaller(lpparam);
        }
        if (ColorToolPrefs.getPrefs("installer_warn", false)) {
            removeWarn(lpparam);
        }
        if (ColorToolPrefs.getPrefs("installer_ads", true)) {
            makeClear(lpparam);
        }
        Log.d(tag, "Hook packageinstaller success!");
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
        }catch (Exception e){
            Log.error(tag, e);
        }
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

    //hide the suggest layout when install successfully
    @SuppressLint("PrivateApi")
    private void makeClear(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz1 = null, clazz0 = null;
        final LinearLayout[] installDoneSuggestLayout = new LinearLayout[3];
        final RelativeLayout[] relativeLayout = new RelativeLayout[1];
        try {
            clazz0 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.InstallAppProgress");
            clazz1 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.InstallAppProgress$1");
        } catch (Exception e) {
            Log.error(tag, e);
        }

        try {
            XposedHelpers.findAndHookMethod(clazz0, "initView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    installDoneSuggestLayout[0] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mSuggestLayoutA");
                    installDoneSuggestLayout[1] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mSuggestLayoutB");
                    installDoneSuggestLayout[2] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mSuggestLayoutC");
                    relativeLayout[0] = (RelativeLayout) XposedHelpers.getObjectField(param.thisObject, "mSuggestLayoutATitle");
                }
            });

            XposedHelpers.findAndHookMethod(clazz1, "handleMessage", Message.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    installDoneSuggestLayout[0].setVisibility(View.GONE);
                    installDoneSuggestLayout[1].setVisibility(View.GONE);
                    installDoneSuggestLayout[2].setVisibility(View.GONE);
                    relativeLayout[0].setVisibility(View.GONE);
                }
            });
            Log.d(tag, "Hide installed suggest layout successfully");
        }catch (Exception e){
            Log.error(tag,e);
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
