package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.CosApkName;
import com.oosl.colorostool.util.Log;
import com.oosl.colorostool.util.ColorToolPrefs;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class HookSafeCenter extends HookBase {

    private static final String tag = "SafeCenter";
    private static final String className;
    private static final String methodName;
    private static final String maxStartup;
    static {
        if(CosApkName.isCos12()) {
            className = "com.oplus.safecenter.startupapp.a";
            methodName = "b";
            maxStartup = "d";
        }else{
            className = "com.coloros.safecenter.startupapp.b";
            methodName = "c";
            maxStartup = "b";
        }
    }


    @Override
    public void hook() {
        super.hook();
        if(ColorToolPrefs.getPrefs("startup", true)) {
            hookSafeCenter();
        }
    }

    private void hookSafeCenter() {
        //去除只能开启5个应用自启动的限制
        Log.d(tag, "Hook safecenter success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass(className);
                    Log.d(tag, "Hook safecenter startupapp success!");
                } catch (Exception e) {
                    return;
                }
                XposedHelpers.findAndHookMethod(clazz, methodName, Context.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    if (param == null)
                        return null;
                    XposedHelpers.setStaticIntField(clazz, maxStartup, 114514);
                    android.util.Log.d("StartupManager", "update max allow count ? " + 114514);
                    Log.d(tag, "After Hook! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, maxStartup));
                    return null;
                    }
                });
            }

        });
    }
}
