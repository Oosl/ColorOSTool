package com.oosl.colorostool;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals("com.coloros.safecenter")) {
            Log.d("ColorOSTool","Hook safecenter success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class<?> clazz;
                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    try {
                        clazz = cl.loadClass("com.coloros.safecenter.startupapp.b");
                        Log.d("ColorOSTool","Hook safecenter.startupapp.b success!");
                    } catch (Exception e) {
                        return;
                    }
                    XposedHelpers.findAndHookMethod(clazz, "c", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.setStaticIntField(clazz, "b", 114514);
                            Log.d("ColorOSTool","After Hook c ! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, "b"));
                        }
                    });
                }
            });
        }

        if (lpparam.packageName.equals("com.oppo.launcher")) {
            Log.d("ColorOSTool","Hook oppoLauncher success!");
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class<?> clazz;
                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    try {
                        clazz = cl.loadClass("com.coloros.quickstep.applock.ColorLockManager");
                        Log.d("ColorOSTool","Hook launcher.quickstep.applock.ColorLockManager success!");
                    } catch (Exception e) {
                        return;
                    }
                    XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.setIntField(param.thisObject,"mLockAppLimit",114514);
                            Log.d("ColorOSTool","Hook launcher.~.ColorLockManager Constructor success!");
                        }
                    });
                }
            });

        }
    }
}
