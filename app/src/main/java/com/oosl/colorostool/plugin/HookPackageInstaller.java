package com.oosl.colorostool.plugin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oosl.colorostool.plugin.base.HookBase;
import com.oosl.colorostool.util.ColorToolPrefs;
import com.oosl.colorostool.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookPackageInstaller extends HookBase {

    private static final String tag = "PackageInstaller";
    private String version = "Null";

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        version = ColorToolPrefs.getVersion("packageInstaller", "Error");
        if (version.equals("Error")) {
            Log.d(tag, "Version code is Error! pls check it!");
            return;
        }
        Log.d(tag, "Version is " + version);
        super.hook(lpparam);
        if (ColorToolPrefs.getPrefs("safe_installer", true)) {
            removeVerify(lpparam);
        }
        if (ColorToolPrefs.getPrefs("aosp_installer", false)) {
            replaceInstaller(lpparam);
        }
        if (ColorToolPrefs.getPrefs("installer_warn", false)) {
            removeWarn(lpparam);
        }
        if (ColorToolPrefs.getPrefs("installer_ads", true)) {
            makeClear(lpparam);
        }
        Log.d(tag, "Hook packageinstaller-" + version + " success!");
    }

    @SuppressLint("PrivateApi")
    private void removeVerify(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz;
        try {
            String className = "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity";
            String[] funName = new String[7];
            String[] fieldName = new String[3];
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                    funName[0] = "L";
                    funName[1] = "r";
                    funName[2] = "s";
                    funName[3] = "C";
                    funName[4] = "k";
                    funName[5] = "T";
                    funName[6] = "U";
                    fieldName[0] = "R";
                    fieldName[1] = "o";
                    fieldName[2] = "aM";
                    break;
                case "38477f0":
                    funName[0] = "M";
                    funName[1] = "r";
                    funName[2] = "s";
                    funName[3] = "D";
                    funName[4] = "k";
                    funName[5] = "U";
                    funName[6] = "V";
                    fieldName[0] = "W";
                    fieldName[1] = "o";
                    fieldName[2] = "aR";
                    break;
                case "75fe984":
                case "532ffef":
                    funName[0] = "L";
                    funName[1] = "NULL";
//                    search -> p()
                    funName[2] = "p";
                    funName[3] = "D";
                    funName[4] = "i";
                    funName[5] = "R";
                    funName[6] = "S";
                    fieldName[0] = "Q";
                    fieldName[1] = "o";
                    fieldName[2] = "aM";
                    break;
                case "a222497":
                    funName[0] = "M";
                    funName[1] = "NULL";
                    funName[2] = "p";
                    funName[3] = "E";
                    funName[4] = "j";
                    funName[5] = "S";
                    funName[6] = "T";
                    fieldName[0] = "Q";
                    fieldName[1] = "o";
                    fieldName[2] = "aN";
                    break;
                default:
//                    search -> count_canceled_by_app_detail -3
                    funName[0] = "isStartAppDetail";
//                    search -> 1500L -15
                    funName[1] = "startAccountVerification";
//                    search -> 1500L +5
                    funName[2] = "continueAppInstall";
//                    search -> "button_type", "install_old_version_button" -5
                    funName[3] = "checkToScanRisk";
//                    search -> "button_type", "install_old_version_button" -11
                    funName[4] = "initiateInstall";
//                    search -> "PackageInstaller", "startAppdetail: " -7
                    funName[5] = "checkAppSuggest";
//                    search -> "PackageInstaller", "don't recommend : -2
                    funName[6] = "checkGameSuggest";
//                    search -> private InstallFlowAnalytics
                    fieldName[0] = "mInstallFlowAnalytics";
//                    search ->  InstallFlowAnalytics line 196
                    fieldName[1] = "mLoginWindows";
//                    search -> "oppo_market"
                    fieldName[2] = "mIsOPPOMarketExists";
            }
            clazz = lpparam.classLoader.loadClass(className);

            // Skip install guide
            XposedHelpers.findAndHookMethod(clazz, funName[0], new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(false);
                }
            });

            // account verify
            if (!funName[1].equals("NULL")) {
                XposedHelpers.findAndHookMethod(clazz, funName[1], new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        XposedHelpers.setObjectField(XposedHelpers.getObjectField(param.thisObject, fieldName[0]), fieldName[1], "0");
                        XposedHelpers.callMethod(param.thisObject, funName[2]);
                        Log.d(tag, "replace startAccountVerification() OK!!");
                        return null;
                    }
                });
            }

            //apk scan
            XposedHelpers.findAndHookMethod(clazz, funName[3], new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    XposedHelpers.callMethod(param.thisObject, funName[4]);
                    Log.d(tag, "replace checkToScanRisk OK!!");
                    return null;
                }
            });

            XposedHelpers.findAndHookMethod(clazz, funName[5], new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return null;
                }
            });

            XposedHelpers.findAndHookMethod(clazz, funName[6], new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return null;
                }
            });

            XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedHelpers.setBooleanField(param.thisObject, fieldName[2], false);
                }
            });
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }

    @SuppressLint("PrivateApi")
//    search ->  ? 1 : 0;
    private void removeWarn(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz, clazz1;
        try {
            String className = "com.android.packageinstaller.oplus.OPlusPackageInstallerActivity", funName;
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                case "a222497":
                    funName = "Q";
                    break;
                case "38477f0":
                    funName = "R";
                    break;
                case "75fe984":
                case "532ffef":
                    funName = "P";
                    break;
                default:
                    funName = "isReplaceInstall";
            }
            clazz = lpparam.classLoader.loadClass(className);
            XposedHelpers.findAndHookMethod(clazz, funName, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return false;
                }
            });
        } catch (Exception e) {
            Log.error(tag, e);
        }

//      uncheck app_suggest_option as default
//      search -> CompoundButton.SavedState{
        try {
            String className, funName = "setState";
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                case "38477f0":
                    className = "com.color.support.widget.OppoCheckBox";
                    break;
                default:
                    className = "com.coui.appcompat.widget.COUICheckBox";
            }
            clazz1 = lpparam.classLoader.loadClass(className);
            XposedHelpers.findAndHookMethod(clazz1, funName, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if ((int) param.args[0] == 2) {
                        param.args[0] = 0;
                    }
                }
            });
        } catch (Exception e) {
            Log.error(tag, e);
        }

    }

    // 使用原生安装器而非OPPO自己写的
//    search -> DeleteStagedFileOnResult
    @SuppressLint("PrivateApi")
    private void replaceInstaller(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz, clazz1;
        try {
            String[] className = new String[2];
            String[] fieldName = new String[1];
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                case "75fe984":
                case "532ffef":
                case "38477f0":
                case "a222497":
                    className[0] = "com.android.packageinstaller.oplus.common.j";
                    className[1] = "com.android.packageinstaller.DeleteStagedFileOnResult";
                    fieldName[0] = "f";
                    break;
                default:
                    className[0] = "com.android.packageinstaller.oplus.common.FeatureOption";
                    className[1] = "com.android.packageinstaller.DeleteStagedFileOnResult";
                    fieldName[0] = "sIsClosedSuperFirewall";
            }
            clazz = lpparam.classLoader.loadClass(className[0]);
            clazz1 = lpparam.classLoader.loadClass(className[1]);
            XposedHelpers.findAndHookMethod(clazz1, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedHelpers.setStaticBooleanField(clazz, fieldName[0], true);
                }
            });
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }

    //hide the suggest layout when install successfully
    @SuppressLint("PrivateApi")
    private void makeClear(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz1, clazz0;
        final LinearLayout[] installDoneSuggestLayout = new LinearLayout[3];
        final RelativeLayout[] relativeLayout = new RelativeLayout[1];
        try {
            String[] className = new String[2];
            className[0] = "com.android.packageinstaller.oplus.InstallAppProgress";
            String[] funName = new String[2];
            String[] fieldName = new String[4];
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                case "38477f0":
                    className[1] = "com.android.packageinstaller.oplus.b";
                    funName[0] = "a";
                    funName[1] = "handleMessage";
                    fieldName[0] = "S";
                    fieldName[1] = "U";
                    fieldName[2] = "V";
                    fieldName[3] = "T";
                    break;
                case "75fe984":
                case "532ffef":
                    className[1] = "com.android.packageinstaller.oplus.b";
                    funName[0] = "a";
                    funName[1] = "handleMessage";
                    fieldName[0] = "V";
                    fieldName[1] = "X";
                    fieldName[2] = "Y";
                    fieldName[3] = "W";
                    break;
                case "a222497":
                    className[1] = "com.android.packageinstaller.oplus.b";
                    funName[0] = "a";
                    funName[1] = "handleMessage";
                    fieldName[0] = "W";
                    fieldName[1] = "Y";
                    fieldName[2] = "Z";
                    fieldName[3] = "X";
                    break;
                default:
                    className[1] = "com.android.packageinstaller.oplus.InstallAppProgress$1";
//                  search -> "unexpected scheme " -3
                    funName[0] = "initView";
                    funName[1] = "handleMessage";
//                    private LinearLayout mSuggestLayoutA;
//                    ***************
//                    private RelativeLayout mSuggestLayoutATitle;
//                    private LinearLayout mSuggestLayoutB;
//                    private LinearLayout mSuggestLayoutC;
                    fieldName[0] = "mSuggestLayoutA";
                    fieldName[1] = "mSuggestLayoutB";
                    fieldName[2] = "mSuggestLayoutC";
                    fieldName[3] = "mSuggestLayoutATitle";
            }
            clazz0 = lpparam.classLoader.loadClass(className[0]);
            clazz1 = lpparam.classLoader.loadClass(className[1]);
            XposedHelpers.findAndHookMethod(clazz0, funName[0], new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    installDoneSuggestLayout[0] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, fieldName[0]);
                    installDoneSuggestLayout[1] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, fieldName[1]);
                    installDoneSuggestLayout[2] = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, fieldName[2]);
                    relativeLayout[0] = (RelativeLayout) XposedHelpers.getObjectField(param.thisObject, fieldName[3]);
                }
            });

            XposedHelpers.findAndHookMethod(clazz1, funName[1], Message.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    installDoneSuggestLayout[0].setVisibility(View.GONE);
                    installDoneSuggestLayout[1].setVisibility(View.GONE);
                    installDoneSuggestLayout[2].setVisibility(View.GONE);
                    relativeLayout[0].setVisibility(View.GONE);
                }
            });
            Log.d(tag, "Hide installed suggest layout successfully");
        } catch (Exception e) {
            Log.error(tag, e);
        }
    }

    @Override
    @SuppressLint("PrivateApi")
//    search -> OppoLog, isQELogOn =
    public void hookLog(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hookLog(lpparam);
        Class<?> clazz;
        try {
            String[] className = new String[1];
            String[] fieldName = new String[1];
            switch (version) {
                case "7bc7db7":
                case "e1a2c58":
                case "38477f0":
                case "a222497":
                    className[0] = "com.android.packageinstaller.oplus.common.k";
                    fieldName[0] = "d";
                    break;
                case "75fe984":
                case "532ffef":
                    className[0] = "com.android.packageinstaller.oplus.common.k";
                    fieldName[0] = "f2658d";
                    break;
                default:
                    className[0] = "com.android.packageinstaller.oplus.common.OppoLog";
                    fieldName[0] = "DEVELOP_MODE";
            }
            clazz = lpparam.classLoader.loadClass(className[0]);
            XposedHelpers.setStaticBooleanField(clazz, fieldName[0], true);
        } catch (Exception e) {
            Log.error(tag, e);
        }
        Log.d(tag, "Hook log success!");
    }
}
