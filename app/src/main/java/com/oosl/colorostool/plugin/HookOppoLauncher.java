package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.CosApkName;
import com.oosl.colorostool.util.Log;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookOppoLauncher extends HookBase{

    @Override
    public void hook() {
        super.hook();
        if (ColorToolPrefs.getPrefs("app_lock", true)){
            hookOppoLauncher();
        }
    }

    private void hookOppoLauncher(){
        String tag = "OppoLauncher";
        String lockManagerClass;

        if (CosApkName.getSystemVersion() == 31)
            lockManagerClass = "com.oplus.quickstep.applock.OplusLockManager";
        else
            lockManagerClass = "com.coloros.quickstep.applock.ColorLockManager";

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
