package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.CosApkName;
import com.oosl.colorostool.util.Log;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookOppoLauncher extends HookBase{

    private static final String tag = "OppoLauncher";
    private static final String lockManagerClass;
    static {
        if (CosApkName.isCos12())
            lockManagerClass = "com.oplus.quickstep.applock.OplusLockManager";
        else
            lockManagerClass = "com.coloros.quickstep.applock.ColorLockManager";
    }

    @Override
    public void hook() {
        super.hook();
        if (ColorToolPrefs.getPrefs("app_lock", true)){
            hookMaxAppLock();
        }
    }

    private void hookMaxAppLock(){
        // 去除多任务后台只能锁定5个的限制
        Log.d(tag,"Hook oppoLauncher success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass(lockManagerClass);
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
