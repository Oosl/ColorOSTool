package com.oosl.colorostool.plugin;

import android.app.Application;
import android.content.Context;

import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSettings {
    private void hookSettings() {
        String tag = "Settings";
        Log.d(tag, "Hook Settings success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplus.settings.utils.aj");
                    Log.d(tag, "Hook setting log success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.setStaticIntField(clazz,"a", 2);
                XposedHelpers.setStaticBooleanField(clazz, "b", true);

                XposedHelpers.findAndHookMethod(clazz, "b", String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //ColorOSToolLog(tag, "tag is " + param.args[0]);
                    }
                });
            }
        });
    }
}
