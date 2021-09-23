package com.oosl.colorostool;

import com.oosl.colorostool.plugin.HookGameSpace;
import com.oosl.colorostool.plugin.HookOppoLauncher;
import com.oosl.colorostool.plugin.HookPackageInstaller;
import com.oosl.colorostool.plugin.HookSafeCenter;
import com.oosl.colorostool.plugin.HookSettings;
import com.oosl.colorostool.plugin.HookSystemUI;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.CosApkName;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(CosApkName.getSafeCenterName())
                && ColorToolPrefs.getPrefs("startup", true)) {
            new HookSafeCenter();
        }else if (lpparam.packageName.equals(CosApkName.getLuncherName())
                && ColorToolPrefs.getPrefs("app_lock", true)) {
            new HookOppoLauncher();
        }else if (lpparam.packageName.equals(CosApkName.getPackageInstallerName())) {
            new HookPackageInstaller(lpparam);
        }else if(lpparam.packageName.equals(CosApkName.getGamesToolName())
                && ColorToolPrefs.getPrefs("root_checker", true)) {
            new HookGameSpace(lpparam);
        }else if(lpparam.packageName.equals(CosApkName.getSystemUIName())
                && ColorToolPrefs.getPrefs("lock_red_one", false)) {
            new HookSystemUI();
        }else if(lpparam.packageName.equals(CosApkName.getSettingsName())) {
            new HookSettings();
        }
    }
}