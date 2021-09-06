package com.oosl.colorostool;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "ColorToolPrefs");

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coloros.safecenter") && prefs.getBoolean("startup", true)) {
            //去除只能开启5个应用自启动的限制
            ColorOSToolLog("Hook safecenter success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.safecenter.startupapp.b");
                    ColorOSToolLog("Hook safecenter.startupapp.b success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.findAndHookMethod(clazz, "c", Context.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        if(param == null)
                            return null;
                        XposedHelpers.setStaticIntField(clazz, "b", 114514);
                        Log.d("StartupManager", "update max allow count ? " + 114514);
                        ColorOSToolLog("After Hook c ! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, "b"));
                        return null;
                    }
                });
                }
            });
        }else if (lpparam.packageName.equals("com.oppo.launcher") && prefs.getBoolean("app_lock", true)) {
            // 去除多任务后台只能锁定5个的限制
            ColorOSToolLog("Hook oppoLauncher success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.quickstep.applock.ColorLockManager");
                    ColorOSToolLog("Hook launcher.quickstep.applock.ColorLockManager success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setIntField(param.thisObject, "mLockAppLimit", 114514);
                        ColorOSToolLog("Hook launcher.~.ColorLockManager Constructor success!");
                    }
                });
                }
            });
        }else if (lpparam.packageName.equals("com.android.packageinstaller")) {
            ColorOSToolLog("Hook packageinstaller success!");
            // 去除安装前的验证
            if(prefs.getBoolean("safe_installer", true)) {
                Class<?> clazz;
                clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
                XposedHelpers.findAndHookMethod(clazz, "continueOppoSafeInstall", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        ColorOSToolLog("replace OppoSafeInstall() OK!!");
                        XposedHelpers.callMethod(param.thisObject,"continueAppInstall");
                        return null;
                    }
                });
            }
            // 使用原生安装器而非OPPO自己写的, false暂时禁用
            if(prefs.getBoolean("aosp_installer", false)) {
                Class<?> clazz2;
                clazz2 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.FeatureOption");
                ColorOSToolLog("sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz2, "sIsClosedSuperFirewall"));
                XposedHelpers.findAndHookMethod(clazz2, "setIsClosedSuperFirewall", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setStaticBooleanField(clazz2, "sIsClosedSuperFirewall", true);
                        ColorOSToolLog("after sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz2, "sIsClosedSuperFirewall"));
                    }
                });
            }
        }else if(lpparam.packageName.equals("com.coloros.gamespace") && prefs.getBoolean("root_checker", true)) {
            hookGameSpace(lpparam);
        }
    }

    private static void ColorOSToolLog(String str){
        final String TAG = "ColorOSTool";

        XposedBridge.log(TAG + ": " + str);
        //Log.d(TAG,str);
    }

    /**
     * Hook the root checker of gamesSpace and make it return true always
     * @param lpparam
     * @throws ClassNotFoundException
     */
    private  void hookGameSpace(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        ColorOSToolLog("Hook gamespace success!");
        Class<?> clazz;
        clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.i.f");
        XposedHelpers.findAndHookMethod(clazz, "c", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });
        ColorOSToolLog("Hook gamespace.root.checker success!");
    }

    /**
     * Open dev mode for logPrinter of gamespace to print more log
     * @param lpparam
     * @throws ClassNotFoundException
     */
    private void hookGameSpaceLog(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        Class<?> clazz;
        clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.f.a");
        XposedHelpers.findAndHookMethod(clazz, "b", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.setStaticBooleanField(clazz,"e",true);
            }
        });
    }

    /**
     * print StackTrace in XposedBridge-log
     */
    private void PrStackTrace(){
        ColorOSToolLog("Dump Stack:---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        for (int i =0; i< stackElements.length; i++){
            ColorOSToolLog(
            i + ":"+ stackElements[i].getMethodName()
                +" in "+stackElements[i].getFileName()
                +":"+stackElements[i].getLineNumber()
                +" -> "+stackElements[i].getClassName());
        }
        ColorOSToolLog("Dump Stack:---------------over----------------");
    }
}