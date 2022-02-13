package com.oosl.colorostool.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.oosl.colorostool.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    public Context mContext = null;
    private String tag = "COSTOOL_ACT";
    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
        if ("hide_icon".equals(key)) {
            ComponentName aliasName = ComponentName.unflattenFromString("com.oosl.colorostool/com.oosl.colorostool.activity.SettingsActivityAlias");
            int status;
            if (sharedPreferences.getBoolean(key, false))
                status = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            else status = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            if (getPackageManager().getComponentEnabledSetting(aliasName) != status) {
                getPackageManager().setComponentEnabledSetting(
                        aliasName,
                        status,
                        PackageManager.DONT_KILL_APP
                );
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        checkEdXposed();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new CostoolSettingsFragment())
                    .commit();
        }
        mContext = getApplicationContext();
        saveAllVersion();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            PreferenceFragmentCompat current = (PreferenceFragmentCompat) getSupportFragmentManager().findFragmentById(R.id.settings);
            if (current != null && current instanceof CostoolSettingsFragment) finishAndRemoveTask();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("WorldReadableFiles")
    private void checkEdXposed() {
        try {
            // getSharedPreferences will hooked by LSPosed and change xml file path to /data/misc/lsp**
            // will not throw SecurityException
            // noinspection deprecation
            // From CorePatch https://github.com/coderstory/CorePatch
            sharedPreferences = getSharedPreferences("ColorToolPrefs", Context.MODE_WORLD_READABLE);
        } catch (SecurityException exception) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.not_supported))
                    .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                        finishAndRemoveTask();
                        System.exit(0);
                    })
                    .setNegativeButton(R.string.ignore, null)
                    .show();
        }
    }

    public static class CostoolSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Keep
    public static class SafeCenterFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.safe_center_preferences, rootKey);
        }
    }

    @Keep
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.android_settings_preferences, rootKey);
        }
    }

    @Keep
    public static class AndroidFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.android_system_preferences, rootKey);
        }
    }

    @Keep
    public static class SystemUiFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.system_ui_preferences, rootKey);
        }
    }

    @Keep
    public static class LauncherFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.launcher_preferences, rootKey);
        }
    }

    @Keep
    public static class PackageInstallerFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.package_installer_preferences, rootKey);
        }
    }

    @Keep
    public static class GameSpaceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.game_space_preferences, rootKey);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    protected void saveAllVersion(){
        try {
            String settingsVersion = getVersion("com.android.settings");
            String systemuiVersion = getVersion("com.android.systemui");
            String packageInstallerVersion = getVersion("com.android.packageinstaller");
            String launcher12Version = getVersion("com.android.launcher");
            String safeCenter12Version = getVersion("com.oplus.safecenter");
            String gameSpace12Version = getVersion("com.oplus.games");
            String gameSpace11Version = getVersion("com.coloros.gamespace");
            String launcher11Version = getVersion("com.oppo.launcher");
            String safeCenter11Version = getVersion("com.coloros.safecenter");
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("ColorToolPrefs", MODE_PRIVATE);
            SharedPreferences.Editor sharePrefsEditor = sharedPreferences.edit();
            sharePrefsEditor.putString("settings", settingsVersion);
            sharePrefsEditor.putString("systemui", systemuiVersion);
            sharePrefsEditor.putString("packageInstaller", packageInstallerVersion);
            sharePrefsEditor.putString("launcherS", launcher12Version);
            sharePrefsEditor.putString("safeCenterS", safeCenter12Version);
            sharePrefsEditor.putString("gameSpaceS", gameSpace12Version);
            sharePrefsEditor.putString("launcherR", launcher11Version);
            sharePrefsEditor.putString("safeCenterR", safeCenter11Version);
            sharePrefsEditor.putString("gameSpaceR", gameSpace11Version);
            sharePrefsEditor.apply();
        }catch (Exception exception){
            android.util.Log.e(tag,"saveVersion ERROR", exception);
        }
    }

    protected String getVersion(String packageName){
        try {
            PackageManager packageManager = mContext.getPackageManager();
            return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData.getString("versionCommit");
        }catch (Exception exception){
            android.util.Log.e(tag,"getVersion ERROR", exception);
            return "Error";
        }
    }


}