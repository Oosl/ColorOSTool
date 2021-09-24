package com.oosl.colorostool.plugin;

import android.app.Application;
import android.content.Context;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSystemUI extends HookBase{

    @Override
    public void hook() {
        super.hook();
        if (ColorToolPrefs.getPrefs("lock_red_one", false)){
            hookSystemUI();
        }
    }

    private void hookSystemUI() {
        String tag = "SystemUI";
        Log.d(tag, "Hook SystemUI success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz, clazz1;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.coloros.systemui.keyguard.clock.RedTextClock");
                    Log.d(tag, "Hook Class success!");

                    // the read one in lock screen
                    XposedHelpers.setStaticObjectField(clazz,"NUMBER_ONE","");
                    Log.d(tag, "Hook RedClock success!");

                } catch (Exception e) {
                    return;
                }

            }
        });
    }

    private void hookSystemUILog() {
        String tag = "SystemUILog";
        Log.d(tag, "Hook SystemUILog success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplusos.common.util.LogUtil");
                    Log.d(tag, "Hook Class success!");

                    // hook SystemUI_LogUtil
                    if(false) {
                        XposedHelpers.findAndHookMethod(clazz, "isTagEnable", String.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Log.d(tag, "br log tag is " + param.getResult());
                                param.setResult(true);
                                Log.d(tag, "af log tag is " + param.getResult());
                            }
                        });
                        XposedHelpers.findAndHookMethod(clazz, "updateLevel", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Log.d(tag, "af updateLevel is " + XposedHelpers.getStaticBooleanField(clazz, "sNormal"));
                                XposedHelpers.setStaticBooleanField(clazz, "sNormal", true);
                                Log.d(tag, "af updateLevel is " + XposedHelpers.getStaticBooleanField(clazz, "sNormal"));
                            }
                        });
                    }
                } catch (Exception e) {
                    return;
                }
            }
        });
    }
}
