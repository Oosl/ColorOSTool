package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.Log;
import com.oosl.colorostool.util.ColorToolPrefs;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class HookSafeCenter extends HookBase {

    private static final String tag = "SafeCenter";
    private String version = "Null";

    @Override
    public void hook() {
        version = ColorToolPrefs.getVersion("safeCenter", "Error");
        if (version.equals("Error") || version.equals("Null")){
            Log.d(tag, "Version code is Error! pls check it!");
            return;
        }
        Log.d(tag,"Version is " + version);
        if(ColorToolPrefs.getPrefs("startup", true)) {
            hookMaxStartup();
        }
        super.hook();
    }

    private void hookMaxStartup() {
        //去除只能开启5个应用自启动的限制
        Log.d(tag, "Hook safecenter success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                String[] className = new String[1];
                String[] funName = new String[1];
                String[] fieldName = new String[1];
                switch (version){
                    case "c6dd4dd":
                        className[0] ="com.oplus.safecenter.startupapp.b";
                        funName[0] = "c";
                        fieldName[0] = "b";
                        break;
                    default:
                        className[0] = "com.oplus.safecenter.startupapp.a";
                        funName[0] = "b";
                        fieldName[0] = "d";
                }
                try {
                    clazz = cl.loadClass(className[0]);
                    Log.d(tag, "Hook safecenter startupapp success!");

                    XposedHelpers.findAndHookMethod(clazz, funName[0], Context.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        if (param == null)
                            return null;
                        XposedHelpers.setStaticIntField(clazz, fieldName[0], 114514);
                        android.util.Log.d("StartupManager", "update max allow count ? " + 114514);
                        Log.d(tag, "After Hook! the max startup allowed app is " + XposedHelpers.getStaticIntField(clazz, "d"));
                        return null;
                        }
                    });
                } catch (Exception e) {
                    return;
                }
            }
        });
    }
}
