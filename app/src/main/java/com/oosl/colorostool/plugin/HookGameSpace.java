package com.oosl.colorostool.plugin;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookGameSpace extends HookBase {

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        super.hook();
        if(ColorToolPrefs.getPrefs("root_checker", true)){
            hookGameSpace(lpparam);
        }
    }

    private void hookGameSpace(final XC_LoadPackage.LoadPackageParam lpparam){
        String tag = "GameSpace";
        Log.d(tag,"Hook gamespace success!");
        Class<?> clazz;
        try {
            clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.i.f");
        }catch (Exception e){
            return;
        }
        XposedHelpers.findAndHookMethod(clazz, "c", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });
        Log.d(tag,"Hook gamespace.root.checker success!");
    }

    private void hookGameSpaceLog(final XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz;
        try{
            clazz = lpparam.classLoader.loadClass("com.oplus.cosa.c.f.a");
        } catch (Exception e){
            return;
        }
        XposedHelpers.findAndHookMethod(clazz, "b", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.setStaticBooleanField(clazz,"e",true);
            }
        });
    }
}
