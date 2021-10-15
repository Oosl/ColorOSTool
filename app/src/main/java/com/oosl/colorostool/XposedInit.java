package com.oosl.colorostool;

import com.oosl.colorostool.plugin.HookGameSpace;
import com.oosl.colorostool.plugin.HookOppoLauncher;
import com.oosl.colorostool.plugin.HookPackageInstaller;
import com.oosl.colorostool.plugin.HookSafeCenter;
import com.oosl.colorostool.plugin.HookSettings;
import com.oosl.colorostool.plugin.HookSystemUI;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals("com.oplus.safecenter")) {
            new HookSafeCenter().hook();
        }else if (lpparam.packageName.equals("com.android.launcher")) {
            new HookOppoLauncher().hook();
        }else if (lpparam.packageName.equals("com.android.packageinstaller")) {
            new HookPackageInstaller().hook(lpparam);
        }else if(lpparam.packageName.equals("com.oplus.games")) {
            new HookGameSpace().hook(lpparam);
        }else if(lpparam.packageName.equals("com.android.systemui")) {
            new HookSystemUI().hook();
        }else if(lpparam.packageName.equals("com.android.settings")) {
            new HookSettings().hook();
        }
    }
}