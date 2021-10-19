package com.oosl.colorostool.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.oosl.colorostool.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Context mContext = null;
    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
        if (key.equals("all_120hz")){
            if (sharedPreferences.getBoolean(key,false)){
                try {
                    Runtime.getRuntime().exec("su -c service call SurfaceFlinger 1035 i32 13");
                    Toast.makeText(mContext,"2k 全局 120HZ 设置成功",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(mContext,"全局 120HZ 设置失败",Toast.LENGTH_SHORT).show();
                    Log.e("ColorOSTool", e.getMessage());
                }
            }else {
                try {
                    Runtime.getRuntime().exec("su -c service call SurfaceFlinger 1035 i32 5");
                    Toast.makeText(mContext,"1080p 全局 120HZ 设置成功",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(mContext,"全局 120HZ 设置失败",Toast.LENGTH_SHORT).show();
                    Log.e("ColorOSTool", e.getMessage());
                }
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
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        mContext = getApplicationContext();
        sharedPreferences = getSharedPreferences("ColorToolPrefs",MODE_PRIVATE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) finishAndRemoveTask();
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("WorldReadableFiles")
    private void checkEdXposed() {
        try {
            // getSharedPreferences will hooked by LSPosed and change xml file path to /data/misc/lsp**
            // will not throw SecurityException
            // noinspection deprecation
            // From CorePatch https://github.com/coderstory/CorePatch
            SharedPreferences sharedPreferences = getSharedPreferences("ColorToolPrefs", Context.MODE_WORLD_READABLE);
        } catch (SecurityException exception) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.not_supported))
                    .setPositiveButton(android.R.string.ok, (dialog12, which) -> exit())
                    .setNegativeButton(R.string.ignore, null)
                    .show();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("ColorToolPrefs");
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
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

    private void exit(){
        this.finishAndRemoveTask();
        System.exit(0);
    }
}