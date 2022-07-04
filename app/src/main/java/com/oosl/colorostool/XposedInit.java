package com.oosl.colorostool;

import com.oosl.colorostool.plugin.HookAndroid;
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
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        switch (lpparam.packageName) {
            case "com.oplus.safecenter":
            case "com.coloros.safecenter":
                new HookSafeCenter().hook();
                break;
            case "com.oppo.launcher":
            case "com.android.launcher":
                new HookOppoLauncher().hook();
                break;
            case "com.android.packageinstaller":
                new HookPackageInstaller().hook(lpparam);
                break;
            case "com.oplus.games":
            case "com.coloros.gamespace":
                new HookGameSpace().hook(lpparam);
                break;
            case "com.android.systemui":
                new HookSystemUI().hook(lpparam);
                break;
            case "com.android.settings":
                new HookSettings().hook();
                break;
            case "android":
                new HookAndroid().hook(lpparam);
                break;
        }
    }
}