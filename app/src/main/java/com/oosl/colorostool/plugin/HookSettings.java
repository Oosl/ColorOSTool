package com.oosl.colorostool.plugin;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.oosl.colorostool.plugin.base.HookBase;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSettings extends HookBase {

    private final String tag = "Settings";
    private String version = "Null";

    @Override
    public void hook() {
        super.hook();
        version = ColorToolPrefs.getVersion("settings", "Error");
        if (version.equals("Error") || version.equals("Null")) {
            Log.d(tag, "Version code is Error! pls check it!");
            return;
        }
        Log.d(tag, "Version is " + version);
        if (ColorToolPrefs.getPrefs("more_dark_mode", false)) hookDarkMode();
        else darkListBackup("restore");
        if (ColorToolPrefs.getPrefs("all_120hz", false)) enableAll120hz();
        Log.n(tag, "Hook Settings success!");
    }

    //    search -> newPullParser.setInput(reader);
    private void hookDarkMode() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    String className = "";
                    String funName = "";
                    switch (version) {
                        case "2f580de":
                            className = "ic.b";
                            funName = "k";
                            break;
                        case "3e74f9f":
                            className = "jc.b";
                            funName = "k";
                            break;
                        case "8cf83fb":
                        case "3736907":
                            className = "com.oplus.settings.feature.display.darkmode.a.b";
                            funName = "a";
                            break;
                        case "393408f":
                        case "108cf63":
                            className = "oc.b";
                            funName = "k";
                            break;
                        case "b857e6f":
                        case "1801866":
                        default:
                            className = "pc.b";
                            funName = "k";

                    }
                    clazz = cl.loadClass(className);
//                    clazz = cl.loadClass("com.oplus.settings.feature.display.darkmode.utils.DarkModeFileUtils");
                    XposedHelpers.findAndHookMethod(clazz, funName, Reader.class, new XC_MethodHook() {
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
                    darkListBackup("restore");
                    Log.error(tag, e);
                }
            }
        });
    }

    private File getDarkModelist() {
        String filePath = "/data/oplus/os/darkmode/sys_dark_mode_third_app_managed.xml";
        if (Build.VERSION.SDK_INT == 30) filePath = "/data/oppo/coloros/darkmode/sys_dark_mode_third_app_managed.xml";
        File darkModeList = new File(filePath);
        darkListBackup("backup");
        try {
            updateDarkModeList(darkModeList);
        } catch (Exception e) {
            Log.error(tag, e);
        }
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
        for (int i = 0; i < packageInfos.size(); i++) {
            ApplicationInfo packageInfo = packageInfos.get(i);
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
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
            Log.error(tag, e);
        }
    }

    private void darkListBackup(String action) {
        File darkModeListBak = new File("/data/oplus/os/darkmode/sys_dark_mode_third_app_managed.xml.bak");
        File darkModeList = new File("/data/oplus/os/darkmode/sys_dark_mode_third_app_managed.xml");
        switch (action) {
            case "backup":
                if (!darkModeListBak.exists()) {
                    Log.d(tag, "darkModeList dont backup");
                    copyFile(darkModeList, darkModeListBak);
                    Log.d(tag, "darkModeList backup successfully");
                }
                break;
            case "restore":
                if (darkModeList.exists() && darkModeListBak.exists()) {
                    copyFile(darkModeListBak, darkModeList);
                    darkModeListBak.delete();
                    Log.d(tag, "darkmode list restore success");
                }
                break;
        }
    }

    private void copyFile(File sourceFile, File targetFile) {

        FileInputStream input = null;
        BufferedInputStream inbuff = null;
        FileOutputStream out = null;
        BufferedOutputStream outbuff = null;

        try {
            input = new FileInputStream(sourceFile);
            inbuff = new BufferedInputStream(input);

            out = new FileOutputStream(targetFile);
            outbuff = new BufferedOutputStream(out);

            byte[] b = new byte[1024 * 5];
            int len = 0;
            while ((len = inbuff.read(b)) != -1) outbuff.write(b, 0, len);
            outbuff.flush();
        } catch (Exception e) {
            Log.error(tag, e);
        } finally {
            try {
                if (inbuff != null) inbuff.close();
                if (outbuff != null) outbuff.close();
                if (out != null) out.close();
                if (input != null) input.close();
            } catch (Exception e) {
                Log.error(tag, e);
            }
        }
    }

    //    search ("LogUtils", "LogMsg: qeOff: ") to locate follow code
    @Override
    public void hookLog() {
        super.hookLog();
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz;
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                String[] className = new String[1];
                String[] fieldName = new String[2];
                switch (version) {
                    case "2f580de":
                        className[0] = "ic.b";
                        fieldName[0] = "k";
                        fieldName[1] = "k";
                        break;
                    case "3e74f9f":
                        className[0] = "kf.o0";
                        fieldName[0] = "a";
                        fieldName[1] = "b";
                        break;
                    case "0c1bf83":
                        className[0] = "com.oplus.settings.utils.ak";
                        fieldName[0] = "a";
                        fieldName[1] = "b";
                        break;
                    case "8cf83fb":
                    default:
                        className[0] = "com.oplus.settings.utils.an";
                        fieldName[0] = "a";
                        fieldName[1] = "b";
                }
                try {
                    clazz = cl.loadClass(className[0]);
//                    clazz = cl.loadClass("com.oplus.settings.utils.am");
//                    clazz = cl.loadClass("com.oplus.settings.utils.LogUtils");
                    XposedHelpers.setStaticIntField(clazz, fieldName[0], 2);
                    XposedHelpers.setStaticBooleanField(clazz, fieldName[1], true);
                    Log.n(tag, "Enable Settings Log success!");
                } catch (Exception e) {
                    Log.error(tag, e);
                    return;
                }
            }
        });
    }

    //    class: ScreenRefreshRateFragment funName: "== 1"
    private void enableAll120hz() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    String className = "com.oplus.settings.feature.display.ScreenRefreshRateFragment";
                    String funName = "";
                    switch (version) {
                        case "2f580de":
                            funName = "r1";
                            break;
                        case "3e74f9f":
                        case "108cf63":
                        case "b857e6f":
                        default:
                            funName = "q1";
                            break;
//                            funName = "e"; //for unknown version
                    }
                    XposedHelpers.findAndHookMethod(className, cl, funName, int.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            setTo96(AndroidAppHelper.currentApplication().getApplicationContext());
                            setAll120(AndroidAppHelper.currentApplication().getApplicationContext());
                            Log.n(tag, "Hook All 120HZ success!");
                        }
                    });
                } catch (Exception e) {
                    Log.error(tag, e);
                }
            }
        });
    }

    private void setAll120(Context context) {
        setFrameRate(context, "min_fresh_rate", "59.0");
        setFrameRate(context, "peak_refresh_rate", "59.0");
        Toast.makeText(context, "120Hz OK", Toast.LENGTH_SHORT).show();
    }

    private void setDefault60(Context context) {
        setFrameRate(context, "min_fresh_rate", "120.0");
        setFrameRate(context, "peak_refresh_rate", "120.0");
        Toast.makeText(context, "60Hz OK", Toast.LENGTH_SHORT).show();
    }

    private void setTo96(Context context) {
        setFrameRate(context, "min_fresh_rate", "96.0");
        setFrameRate(context, "peak_refresh_rate", "96.0");
        //Toast.makeText(context, "还原96成功", Toast.LENGTH_SHORT).show();
    }

    private void setFrameRate(Context context, String rateSettingName, String value) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", rateSettingName);
            contentValues.put("value", value);
            contentResolver.insert(Uri.parse("content://settings/system"), contentValues);
        } catch (Exception exception) {
            Toast.makeText(context, "set rate failed", Toast.LENGTH_SHORT).show();
            Log.error(tag, exception);
        }
    }
}
