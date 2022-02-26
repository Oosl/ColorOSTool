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

    private String tag = "GameSpace";
    private String version = "Null";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam){
        version = ColorToolPrefs.getVersion("gameSpace", "Error");
        if (version.equals("Error") || version.equals("Null")){
            Log.d(tag, "Version code is Error! pls check it!");
            return;
        }
        Log.d(tag, "version is " + version);
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
            //tag:isSafe
            clazz = lpparam.classLoader.loadClass("com.oplus.f.a");
            XposedHelpers.findAndHookMethod(clazz, "h", Context.class, String.class, new XC_MethodHook() {
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
            // tag:常见问题
            clazz = loadPackageParam.classLoader.loadClass("b.b.a.n.a$a");
            XposedHelpers.findAndHookMethod(clazz, "a", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ArrayList arrayList = (ArrayList) param.getResult();
                    ArrayList arrayList1 = new ArrayList<>();
//                    arrayList1.add(0,arrayList.get(2));
                    param.setResult(arrayList1);
                }
            });
            // tag:"startAnimationIn"
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
            // tag:= "hlog"
//            clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.u.a");
            clazz = lpparam.classLoader.loadClass("com.coloros.gamespaceui.v.a");
            XposedHelpers.setStaticBooleanField(clazz,"i",true);
            Log.d(tag,"Hook gamespace LogClass success!");
        } catch (Exception e){
            Log.error(tag,e);
        }
    }
}
