package com.oosl.colorostool.plugin;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class HookSettings extends HookBase{

    private final String tag = "Settings";

    @Override
    public void hook() {
        super.hook();
        if (ColorToolPrefs.getPrefs("more_dark_mode", false)) hookDarkMode();
        Log.n(tag, "Hook Settings success!");
    }

    private void hookDarkMode() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplus.settings.feature.display.darkmode.a.b");
                    XposedHelpers.findAndHookMethod(clazz, "a", Reader.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            File darkModeList = getDarkModelist();
                            Reader reader = new FileReader(darkModeList);
                            param.args[0] = reader;
                        }
                    });
                    Log.n(tag, "Hook Darkmode success!");
                } catch (Exception e) {
                    Log.error(tag, e);
                }
            }
        });
    }

    private File getDarkModelist(){
        @SuppressLint("SdCardPath") File darkModeList = new File( "/data/data/com.android.settings/files/dark_mode_list.xml");
        if (!darkModeList.exists()){
            try{
                Log.d(tag,"darkModeList dont exist");
                File dir = new File(Objects.requireNonNull(darkModeList.getParent()));
                if (!dir.exists()) dir.mkdir();
                if (!darkModeList.createNewFile()) throw new Exception();
                Log.d(tag, "darkModeList create successfully");
            } catch (Exception e){
                Log.error(tag,e);
            }
        }else {
            Log.d(tag,"darkModeList alrady exists");
        }
        updateDarkModeList(darkModeList);
        return darkModeList;
    }

    @SuppressLint("WrongConstant")
    private void updateDarkModeList(File darkModeList) {
        StringBuilder itemList = new StringBuilder();
        itemList.append("<filter-conf>\n" +
                "\t<version>202105141050</version>\n" +
                "\t<isOpen>1</isOpen>\n" +
                "\t<filter-name>sys_dark_mode_third_app_managed</filter-name>\n");
        PackageManager packageManager = AndroidAppHelper.currentApplication().getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> packageInfos = packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_USER);
        for (int i =0; i < packageInfos.size(); i++){
            ApplicationInfo packageInfo = packageInfos.get(i);
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                itemList.append("\t<p attr=\"");
                itemList.append(packageInfo.packageName);
                itemList.append("\" />\n");
            }
        }
        itemList.append("</filter-conf>");
        byte[] listBytes = itemList.toString().getBytes(StandardCharsets.UTF_8);
        try {
            FileOutputStream outputStream = new FileOutputStream(darkModeList);
            outputStream.write(listBytes);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hookLog() {
        super.hookLog();
        if (!enableLog) return;
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    clazz = cl.loadClass("com.oplus.settings.utils.am");
                } catch (Exception e) {
                    Log.error(tag, e);
                    return;
                }
                XposedHelpers.setStaticIntField(clazz,"a", 2);
                XposedHelpers.setStaticBooleanField(clazz, "b", true);
                Log.n(tag, "Enable Settings Log success!");
            }
        });
    }
}
