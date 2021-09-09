package com.oosl.colorostool;

import android.annotation.SuppressLint;
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
            hookSafeCenter();
        }else if (lpparam.packageName.equals("com.oppo.launcher") && prefs.getBoolean("app_lock", true)) {
            hookOppoLuncher();
        }else if (lpparam.packageName.equals("com.android.packageinstaller")) {
            hookPackageInstaller(lpparam);
        }else if(lpparam.packageName.equals("com.coloros.gamespace") && prefs.getBoolean("root_checker", true)) {
            hookGameSpace(lpparam);
        }else if(lpparam.packageName.equals("com.android.systemui") && prefs.getBoolean("lock_red_one", false)) {
            hookSystemUI();
        }
    }

    /**
     * auto add TAG to logMesg which will be printed by XposedBridge.log()
     * @param logMesg
     * @param tag tag for hook app
     */
    private static void ColorOSToolLog(String tag, String logMesg){
        final String TAG = "ColorOSTool";
        XposedBridge.log("[" + TAG + "-"+ tag + "]: " + logMesg);
    }

    /**
     * Hook the root checker of gamesSpace and make it return true always
     * @param lpparam
     * @throws ClassNotFoundException
     */
    private void hookGameSpace(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        String tag = "GameSpace";
        ColorOSToolLog(tag,"Hook gamespace success!");
        Class<?> clazz;
        clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.i.f");
        XposedHelpers.findAndHookMethod(clazz, "c", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });
        ColorOSToolLog(tag,"Hook gamespace.root.checker success!");
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
        String tag = "ST";
        ColorOSToolLog(tag,"Dump Stack:---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        for (int i =0; i< stackElements.length; i++){
            ColorOSToolLog(tag,
            i + ":"+ stackElements[i].getMethodName()
                +" in "+stackElements[i].getFileName()
                +":"+stackElements[i].getLineNumber()
                +" -> "+stackElements[i].getClassName());
        }
        ColorOSToolLog(tag,"Dump Stack:---------------over----------------");
    }

    private void hookSafeCenter() {
        String tag = "SafeCenter";
        //去除只能开启5个应用自启动的限制
        ColorOSToolLog(tag,"Hook safecenter success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.safecenter.startupapp.b");
                    ColorOSToolLog(tag,"Hook safecenter startupapp success!");
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
                        ColorOSToolLog(tag,"After Hook! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, "b"));
                        return null;
                    }
                });
            }
        });
    }

    private void hookOppoLuncher() {
        String tag = "OppoLuncher";
        // 去除多任务后台只能锁定5个的限制
        ColorOSToolLog(tag,"Hook oppoLauncher success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.quickstep.applock.ColorLockManager");
                    ColorOSToolLog(tag,"Hook launcher app_lock success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setIntField(param.thisObject, "mLockAppLimit", 114514);
                        ColorOSToolLog(tag,"Hook app_lock to 114514 successfully!");
                    }
                });
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookPackageInstaller(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        String tag = "PackageInstaller";
        ColorOSToolLog(tag,"Hook packageinstaller success!");
        // 去除安装前的验证
        Class<?> clazz0, clazz1;
        clazz0 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.AccountVerifyControl");
        clazz1 = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
        if(prefs.getBoolean("safe_installer", true)){
            XposedHelpers.findAndHookMethod(clazz0, "needAccountVerify", Context.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    ColorOSToolLog(tag,"replace AccountVerify OK!!");
                    return false;
                }
            });
            XposedHelpers.findAndHookMethod(clazz1, "continueOppoSafeInstall", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    ColorOSToolLog(tag,"replace OppoSafeInstall() OK!!");
                    XposedHelpers.callMethod(param.thisObject,"continueAppInstall");
                    return null;
                }
            });
        }
        if (prefs.getBoolean("installer_warn", false)){
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
        if(prefs.getBoolean("aosp_installer", false)) {
            Class<?> clazz;
            clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.FeatureOption");
            ColorOSToolLog(tag,"sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
            XposedHelpers.findAndHookMethod(clazz, "setIsClosedSuperFirewall", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setStaticBooleanField(clazz, "sIsClosedSuperFirewall", true);
                    //ColorOSToolLog(tag,"after sIsClosedSuperFirewall is " + XposedHelpers.getStaticBooleanField(clazz, "sIsClosedSuperFirewall"));
                }
            });
        }
    }

    private void hookSystemUI() {
        String tag = "SystemUI";
        ColorOSToolLog(tag, "Hook SystemUI success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.systemui.keyguard.clock.RedTextClock");
                    ColorOSToolLog(tag, "Hook RedClock success!");
                } catch (Exception e) {
                    return;
                }
                // the read one in lock screen
                XposedHelpers.setStaticObjectField(clazz,"NUMBER_ONE","");
            }
        });
    }
}