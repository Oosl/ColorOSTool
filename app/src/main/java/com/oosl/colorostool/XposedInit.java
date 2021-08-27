package com.oosl.colorostool;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    private static final String TAG = "ColorOSTool";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coloros.safecenter")) {
            Log.d(TAG,"Hook safecenter success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class<?> clazz;
                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    try {
                        clazz = cl.loadClass("com.coloros.safecenter.startupapp.b");
                        Log.d(TAG,"Hook safecenter.startupapp.b success!");
                        XposedHelpers.findAndHookMethod(clazz, "c", Context.class, new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                if(param == null)
                                    return null;
                                XposedHelpers.setStaticIntField(clazz, "b", 114514);
                                Log.d("StartupManager", "update max allow count ? " + 114514);
                                Log.d(TAG, "After Hook c ! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, "b"));
                                return null;
                            }
                        });
                    } catch (Exception e) {
                        return;
                    }
                }
            });
        }else if (lpparam.packageName.equals("com.oppo.launcher")) {
            Log.d(TAG, "Hook oppoLauncher success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class<?> clazz;
                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    try {
                        clazz = cl.loadClass("com.coloros.quickstep.applock.ColorLockManager");
                        Log.d(TAG, "Hook launcher.quickstep.applock.ColorLockManager success!");
                    } catch (Exception e) {
                        return;
                    }
                    XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.setIntField(param.thisObject, "mLockAppLimit", 114514);
                            Log.d(TAG, "Hook launcher.~.ColorLockManager Constructor success!");
                        }
                    });
                }
            });
        }else if (lpparam.packageName.equals("com.android.packageinstaller")) {
            Log.d(TAG, "Hook packageinstaller success!");
            Class<?> clazz = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.OPlusPackageInstallerActivity");
            XposedHelpers.findAndHookMethod(clazz, "continueOppoSafeInstall", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(TAG, "replace OppoSafeInstall OK!!");
                    XposedHelpers.callMethod(param.thisObject,"continueAppInstall");
                    return null;
                }
            });
        }
    }

    //用于打印堆栈
    public void PrStackTrace(){
        Log.d("ColorOSTool","Dump Stack:---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null){
            for (int i =0; i< stackElements.length; i++){
                Log.i("ColorOSTool", i + ":"+ stackElements[i].getMethodName()
                        +" in "+stackElements[i].getFileName()
                        +":"+stackElements[i].getLineNumber()
                        +" -> "+stackElements[i].getClassName());
            }
        }
        Log.i("ColorOSTool","Dump Stack:---------------over----------------");
    }
}
