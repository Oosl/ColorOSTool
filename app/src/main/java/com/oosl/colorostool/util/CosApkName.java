package com.oosl.colorostool.util;

import android.os.Build;

public final class CosApkName {
    private static final String safeCenterName;
    private static final String luncherName;
    private static final String gamesToolName;
    private static final String packageInstallerName = "com.android.packageinstaller";
    private static final String systemUIName = "com.android.systemui";
    private static final String settingsName = "com.android.settings";
    private static final String androidName = "android";

    static {
        if (isCos12()){
            safeCenterName = "com.oplus.safecenter";
            luncherName = "com.android.launcher";
            gamesToolName = "com.oplus.games";
        }else {
            safeCenterName = "com.coloros.safecenter";
            luncherName = "com.oppo.launcher";
            gamesToolName = "com.coloros.gamespace";
        }
    }

    public static String getLuncherName() {
        return luncherName;
    }

    public static String getGamesToolName() {
        return gamesToolName;
    }

    public static String getSafeCenterName() {
        return safeCenterName;
    }

    public static String getPackageInstallerName() {
        return packageInstallerName;
    }

    public static String getSettingsName() {
        return settingsName;
    }

    public static String getSystemUIName() {
        return systemUIName;
    }

    public static String getAndroidName() {
        return androidName;
    }

    public static boolean isCos12(){
        return Build.VERSION.SDK_INT == 31;
    }
}
