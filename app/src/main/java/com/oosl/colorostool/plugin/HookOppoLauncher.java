package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookOppoLauncher extends HookBase{

    private static final String tag = "OppoLauncher";

    @Override
    public void hook() {
        super.hook();
        if (ColorToolPrefs.getPrefs("app_lock", true)){
            hookMaxAppLock();
        }
        if (ColorToolPrefs.getPrefs("launcher_layout", true)){
            hookLayout();
        }
    }

    private void hookMaxAppLock(){
        // 去除多任务后台只能锁定5个的限制
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    clazz = cl.loadClass("com.oplus.quickstep.applock.OplusLockManager");
                    XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.setIntField(param.thisObject, "mLockAppLimit", 114514);
                            Log.d(tag,"Hook app_lock to 114514 successfully!");
                        }
                    });
                    Log.d(tag,"Hook launcher app_lock success!");
                } catch (Exception e) {
                    Log.error(tag,e);
                }
            }
        });
        Log.d(tag,"Hook oppoLauncher success!");
    }

    private void hookLayout(){
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    clazz = cl.loadClass("com.android.launcher.togglebar.adapter.ToggleBarLayoutAdapter");
                    XposedHelpers.setStaticObjectField(clazz,"MIN_MAX_COLUMN", new int[] {3, 7});
                    XposedHelpers.setStaticObjectField(clazz,"MIN_MAX_ROW", new int[] {5, 7});
                } catch (Exception e) {
                    Log.error(tag,e);
                }
            }
        });
        Log.d(tag,"hookLayout success!");
    }

    @Override
    public void hookLog() {
        super.hookLog();
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    clazz = cl.loadClass("com.android.common.debug.LogUtils");
                    XposedHelpers.findAndHookMethod(clazz, "updateState", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            boolean normal = XposedHelpers.getStaticBooleanField(clazz, "normal");
                            Log.d(tag, "normal is " + normal);
                            XposedHelpers.setStaticBooleanField(clazz,"normal", true);
                            Log.d(tag, "normal is " + XposedHelpers.getStaticBooleanField(clazz, "normal"));
                        }
                    });
                } catch (Exception e) {
                    Log.error(tag,e);
                }
            }
        });
    }
}
