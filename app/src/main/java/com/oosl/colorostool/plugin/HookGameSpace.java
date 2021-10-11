package com.oosl.colorostool.plugin;

import android.content.Context;
import android.os.Bundle;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.CosApkName;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookGameSpace extends HookBase {

    String tag = "GameSpace";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        super.hook();
        if(ColorToolPrefs.getPrefs("root_checker", true)){
            hookRootChecker(lpparam);
            //hookGameSpaceLog(lpparam);
        }
    }

    private void hookRootChecker(final XC_LoadPackage.LoadPackageParam lpparam){
        Log.d(tag,"Hook gamespace success!");
        Class<?> clazz;
        if (CosApkName.isCos12()){
            try {
                clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.h.d");
                XposedHelpers.findAndHookMethod(clazz, "c", Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Bundle bundle = (Bundle) param.getResult();
                        bundle.putInt("isSafe", 0);
                    }
                });
            }catch (Exception e){
                return;
            }
        } else {
            try {
                clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.i.f");
                XposedHelpers.findAndHookMethod(clazz, "c", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return true;
                    }
                });
            } catch (Exception e) {
                return;
            }
        }
        Log.d(tag,"Hook gamespace.root.checker success!");
    }

    @Override
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        if (!enableLog) return;
        Log.d(tag,"Hook gamespaceLog success!");
        Class<?> clazz;
        if (CosApkName.isCos12()){
            try{
                clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.j.a");
                XposedHelpers.setStaticBooleanField(clazz,"i",true);
                Log.d(tag,"Hook gamespace LogClass success!");
            } catch (Exception e){
                return;
            }
        }else {
            try{
                clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.f.a");
                XposedHelpers.findAndHookMethod(clazz, "b", String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedHelpers.setStaticBooleanField(clazz,"e",true);
                    }
                });
            } catch (Exception e){
                return;
            }
        }
    }
}
