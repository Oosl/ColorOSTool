package com.oosl.colorostool;

import com.oosl.colorostool.plugin.HookAndroid;
import com.oosl.colorostool.plugin.HookGameSpace;
import com.oosl.colorostool.plugin.HookOppoLauncher;
import com.oosl.colorostool.plugin.HookPackageInstaller;
import com.oosl.colorostool.plugin.HookSafeCenter;
import com.oosl.colorostool.plugin.HookSettings;
import com.oosl.colorostool.plugin.HookSystemUI;
import com.oosl.colorostool.util.CosApkName;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(CosApkName.getSafeCenterName())) {
            new HookSafeCenter().hook();
        }else if (lpparam.packageName.equals(CosApkName.getLuncherName())) {
            new HookOppoLauncher().hook();
        }else if (lpparam.packageName.equals(CosApkName.getPackageInstallerName())) {
            new HookPackageInstaller().hook(lpparam);
        }else if(lpparam.packageName.equals(CosApkName.getGamesToolName())) {
            new HookGameSpace().hook(lpparam);
        }else if(lpparam.packageName.equals(CosApkName.getSystemUIName())) {
            new HookSystemUI().hook();
        }else if(lpparam.packageName.equals(CosApkName.getSettingsName())) {
            new HookSettings().hook();
        }else if(lpparam.packageName.equals(CosApkName.getAndroidName())) {
            new HookAndroid().hook(lpparam);
        }
    }
}