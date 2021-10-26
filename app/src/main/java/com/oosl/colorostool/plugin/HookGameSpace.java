package com.oosl.colorostool.plugin;

import android.content.Context;
import android.os.Bundle;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookGameSpace extends HookBase {

    String tag = "GameSpace";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        super.hook(lpparam);
        if(ColorToolPrefs.getPrefs("root_checker", true)){
            hookRootChecker(lpparam);
            //hookGameSpaceLog(lpparam);
        }
    }

    private void hookRootChecker(final XC_LoadPackage.LoadPackageParam lpparam){
        Log.d(tag,"Hook gamespace success!");
        Class<?> clazz;

        try {
            clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.ipc.COSAManager");
            XposedHelpers.findAndHookMethod(clazz, "u3", Context.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Bundle bundle = (Bundle) param.getResult();
                    bundle.putInt("isSafe", 0);
                }
            });
            Log.d(tag,"Hook gamespace.root.checker success!");
        }catch (Exception e){
            Log.error(tag,e);
        }
    }

    @Override
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        if (!enableLog) return;
        Log.d(tag,"Hook gamespaceLog success!");
        Class<?> clazz;
        try{
            clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.j.a");
            XposedHelpers.setStaticBooleanField(clazz,"i",true);
            Log.d(tag,"Hook gamespace LogClass success!");
        } catch (Exception e){
            Log.error(tag,e);
        }
    }
}
