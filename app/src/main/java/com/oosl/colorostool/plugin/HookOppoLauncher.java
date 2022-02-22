package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookOppoLauncher extends HookBase{

    private static final String tag = "OppoLauncher";
    private String version = "Null";
    private String android = "R";

    @Override
    public void hook() {
        version = ColorToolPrefs.getVersion("launcherR", "Error");
        if(version.equals("Error") || version.equals("Null")) {
            version = ColorToolPrefs.getVersion("launcherS", "Error");
            android = "S";
        }
        Log.d(tag, "Launcher version is " + version);
        if (ColorToolPrefs.getPrefs("app_lock", true)){
            hookMaxAppLock();
        }
        if (ColorToolPrefs.getPrefs("launcher_layout", true) && android.equals("S")){
            hookLayout();
        }
        super.hook();
    }

    private void hookMaxAppLock(){
        // 去除多任务后台只能锁定5个的限制
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                String[] className = new String[1];
                String[] fieldName = new String[1];
                switch (version){
                    case "33b2b9a":
                        className[0] ="com.coloros.quickstep.applock.ColorLockManager";
                        fieldName[0] = "mLockAppLimit";
                        break;
                    default:
                        className[0] ="com.oplus.quickstep.applock.OplusLockManager";
                        fieldName[0] = "mLockAppLimit";
                }
                try {
                    clazz = cl.loadClass(className[0]);
                    XposedHelpers.findAndHookConstructor(clazz, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.setIntField(param.thisObject, fieldName[0], 114514);
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

                String[] className = new String[1];
                String[] fieldName = new String[2];
                switch (version){
                    default:
                        className[0] ="com.android.launcher.togglebar.adapter.ToggleBarLayoutAdapter";
                        fieldName[0] = "MIN_MAX_COLUMN";
                        fieldName[1] = "MIN_MAX_ROW";
                }

                try {
                    clazz = cl.loadClass(className[0]);
                    XposedHelpers.setStaticObjectField(clazz,fieldName[0], new int[] {3, 8});
                    XposedHelpers.setStaticObjectField(clazz,fieldName[1], new int[] {5, 8});
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
                String[] className = new String[1];
                String[] funName = new String[1];
                String[] fieldName = new String[2];

//              search -> private static boolean sDebug = true
                switch (version){
                    case "57cef08":
                        className[0] = "com.oplus.quickstep.utils.LogUtils";
                        funName[0] = "updateState";
                        fieldName[0] = "sDebug";
                    case "33b2b9a":
                        className[0] ="com.coloros.quickstep.utils.LogUtils";
                        funName[0] = "updateState";
                        fieldName[0] = "sDebug";
                        break;
                    default:
                        className[0] ="com.android.common.debug.LogUtils";
                        funName[0] = "updateState";
                        fieldName[0] = "normal";
                }
                try {
                    clazz = cl.loadClass(className[0]);
                    XposedHelpers.findAndHookMethod(clazz, funName[0], new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedHelpers.setStaticBooleanField(clazz,fieldName[0], true);
                        }
                    });
                } catch (Exception e) {
                    Log.error(tag,e);
                }
            }
        });
    }
}
