package com.oosl.colorostool.plugin;

import android.app.Application;
import android.content.Context;

import com.oosl.colorostool.plugin.base.HookBase;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookSystemUI extends HookBase {

    private final String tag = "SystemUI";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        if (ColorToolPrefs.getPrefs("lock_red_one", false)) {
            hookRedOne();
        }
        if (ColorToolPrefs.getPrefs("charging_ripple", false)) {
            hookChargeWipe();
        }
        if (ColorToolPrefs.getPrefs("developer_notification", false)) {
            hookDeveloperModeNotification(lpparam);
        }
        Log.d(tag, "Hook SystemUI success!");
    }

    private void hookRedOne() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplusos.systemui.keyguard.clock.RedTextClock");
                    Log.d(tag, "Hook Class success!");

                    // the read one in lock screen
                    XposedHelpers.setStaticObjectField(clazz, "NUMBER_ONE", "");
                    Log.d(tag, "Hook RedClock success!");

                } catch (Exception e) {
                    Log.error(tag, e);
                }
            }
        });
    }

    private void hookChargeWipe() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.android.systemui.statusbar.FeatureFlags");
                    Log.d(tag, "Hook FeatureFlags success!");

                    XposedHelpers.findAndHookMethod(clazz, "isChargingRippleEnabled", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return true;
                        }
                    });
                    Log.d(tag, "Hook ChargingRipple success!");

                } catch (Exception e) {
                    Log.error(tag, e);
                }
            }
        });
    }

    @Override
    public void hookLog() {
        super.hookLog();
        // disable SystemUI Log if unnecessary
        if (true) return;
        String tag = "SystemUILog";
        Log.d(tag, "Hook SystemUILog success!");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplusos.systemui.common.util.LogUtil");
                    Log.d(tag, "Hook Class success!");

                    // hook SystemUI_LogUtil
                    XposedHelpers.findAndHookMethod(clazz, "isTagEnable", String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            param.setResult(true);
                        }
                    });
                    XposedHelpers.findAndHookMethod(clazz, "updateLevel", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedHelpers.setStaticBooleanField(clazz, "sNormal", true);
                        }
                    });
                } catch (Exception e) {
                    Log.error(tag, e);
                }
            }
        });
    }

    private void hookDeveloperModeNotification(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> clazz = lpparam.classLoader.loadClass("com.oplusos.systemui.common.feature.FeatureOption");
            XposedHelpers.findAndHookMethod(clazz, "isSendDeveloperModeNotification", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(false);
                    Log.d(tag, "hook DeveloperModeNotification success!");
                }
            });
        } catch (Throwable t) {
            Log.error(tag, t);
        }

    }
}
