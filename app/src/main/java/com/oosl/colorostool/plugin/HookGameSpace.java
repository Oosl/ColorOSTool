package com.oosl.colorostool.plugin;

import android.content.Context;
import android.os.Bundle;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookGameSpace extends HookBase {

    String tag = "GameSpace";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        super.hook(lpparam);
        if(ColorToolPrefs.getPrefs("root_checker", false)){
            hookRootChecker(lpparam);
        }
        if (ColorToolPrefs.getPrefs("gs_view_cleaner",false)){
            hookView(lpparam);
        }
        Log.d(tag,"Hook gamespace success!");
    }

    private void hookRootChecker(final XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz;

        try {
            clazz = lpparam.classLoader.loadClass("com.gamespace.ipc.COSAManager");
            XposedHelpers.findAndHookMethod(clazz, "B3", Context.class, String.class, new XC_MethodHook() {
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

    private void hookView(XC_LoadPackage.LoadPackageParam loadPackageParam){
        Class<?> clazz, clazz1;

        try {
            clazz = loadPackageParam.classLoader.loadClass("com.coloros.gamespaceui.n.k.a$a");
            XposedHelpers.findAndHookMethod(clazz, "a", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ArrayList arrayList = (ArrayList) param.getResult();
                    ArrayList arrayList1 = new ArrayList<>();
                    arrayList1.add(0,arrayList.get(2));
                    param.setResult(arrayList1);
                }
            });
            clazz1 = loadPackageParam.classLoader.loadClass("com.coloros.gamespaceui.module.floatwindow.view.GameOptimizedNewView");
            XposedHelpers.findAndHookMethod(clazz1, "c", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
            Log.d(tag,"gamespace viewer cleaner success!");
        }catch (Exception e){
            Log.error(tag,e);
        }
    }

    @Override
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        Class<?> clazz;
        try{
            clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.s.a");
            XposedHelpers.setStaticBooleanField(clazz,"i",true);
            Log.d(tag,"Hook gamespace LogClass success!");
        } catch (Exception e){
            Log.error(tag,e);
        }
    }
}
