package com.oosl.colorostool.plugin;

import android.content.Context;
import android.os.Bundle;

import com.oosl.colorostool.plugin.base.HookBase;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookGameSpace extends HookBase {

    private final String tag = "GameSpace";
    private String version = "Null";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        version = ColorToolPrefs.getVersion("gameSpace", "Error");
        if (version.equals("Error") || version.equals("Null")) {
            Log.d(tag, "Version code is Error! pls check it!");
            return;
        }
        Log.d(tag, "version is " + version);
        super.hook(lpparam);
        if (ColorToolPrefs.getPrefs("root_checker", false)) {
            hookRootChecker(lpparam);
        }
        if (ColorToolPrefs.getPrefs("gs_view_cleaner", false)) {
            hookView(lpparam);
        }
        Log.d(tag, "Hook gamespace success!");
    }

    private void hookRootChecker(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz;
        String[] className = new String[1];
        String[] funName = new String[1];
        String flag = "";

        try {
//            search ->
//            "dynamic_feature_cool_ex");
//            ("isSafe")) : null;
            switch (version) {
                case "6d29cc7":
                    className[0] = "com.coloros.gamespaceui.g.b";
                    funName[0] = "d";
                    flag = "onlyString";
                    break;
                case "5e17a18":
                    className[0] = "com.coloros.gamespaceui.h.d";
                    funName[0] = "c";
                    break;
                case "79df39a":
                    className[0] = "com.coloros.gamespace.cosa.a";
                    funName[0] = "a";
                    flag = "onlyString";
                    break;
                case "620aca8":
                    className[0] = "com.oplus.f.a";
                    funName[0] = "h";
                    break;
                case "8ca6c06":
                case "73422a6":
                case "ae05e0f":
                default:
                    className[0] = "com.oplus.f.a";
                    funName[0] = "j";
            }
            clazz = lpparam.classLoader.loadClass(className[0]);
            if (flag.equals("onlyString")) {
                XposedHelpers.findAndHookMethod(clazz, funName[0], String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Bundle bundle = (Bundle) param.getResult();
                        bundle.putInt("isSafe", 0);
                    }
                });
            } else {
                XposedHelpers.findAndHookMethod(clazz, funName[0], Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Bundle bundle = (Bundle) param.getResult();
                        bundle.putInt("isSafe", 0);
                    }
                });
            }
            Log.d(tag, "Hook gamespace.root.checker success!");
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }

    private void hookView(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> clazz, clazz1;

        try {
            String[] className = {"b.b.a.n.a$a", "com.coloros.gamespaceui.module.floatwindow.view.GameOptimizedNewView"};
            String[] funName = {"a", "c"};
            switch (version) {
                case "8ca6c06":
                case "73422a6":
                    className[0] = "d.b.a.n.a$a";
                    break;
//                default:
////                    search -> addGameSdk(RouterConstants.PATH_OPERATION_HOME_CUSTOMER)
//                    className[0] = "b.b.a.n.a$a";
//                    funName[0] = "a";
////                    search -> "GameOptimizedNewView", "startAnimationIn"
//                    className[1] = "com.coloros.gamespaceui.module.floatwindow.view.GameOptimizedNewView";
//                    funName[1] = "c";
            }
            // tag:常见问题
            clazz = loadPackageParam.classLoader.loadClass(className[0]);
            XposedHelpers.findAndHookMethod(clazz, funName[0], new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ArrayList arrayList = (ArrayList) param.getResult();
                    ArrayList arrayList1 = new ArrayList<>();
                    param.setResult(arrayList1);
                }
            });
            // tag:"startAnimationIn"
            clazz1 = loadPackageParam.classLoader.loadClass(className[1]);
            XposedHelpers.findAndHookMethod(clazz1, funName[1], new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
            Log.d(tag, "gamespace viewer cleaner success!");
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }

    @Override
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        Class<?> clazz;
        try {
            String[] className = new String[1];
            String[] fieldName = new String[1];
//          search -> "GameSpaceUI", "OppoLog, sIsQELogOn = "
            switch (version) {
                case "6d29cc7":
                    className[0] = "com.coloros.gamespaceui.i.a";
                    fieldName[0] = "e";
                    break;
                default:
                    className[0] = "com.coloros.gamespaceui.v.a";
                    fieldName[0] = "i";
            }

            clazz = lpparam.classLoader.loadClass(className[0]);
            XposedHelpers.setStaticBooleanField(clazz, fieldName[0], true);
            Log.d(tag, "Hook gamespace LogClass success!");
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }
}
