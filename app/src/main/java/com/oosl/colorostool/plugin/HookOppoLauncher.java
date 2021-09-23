package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.Log;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookOppoLauncher extends HookBase{

    @Override
    protected void hook() {
        super.hook();
        hookOppoLauncher();
    }

    private void hookOppoLauncher(){
        String tag = "OppoLauncher";
        // 去除多任务后台只能锁定5个的限制
        Log.d(tag,"Hook oppoLauncher success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.quickstep.applock.ColorLockManager");
                    Log.d(tag,"Hook launcher app_lock success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.setIntField(param.thisObject, "mLockAppLimit", 114514);
                        Log.d(tag,"Hook app_lock to 114514 successfully!");
                    }
                });
            }
        });
    }
}
