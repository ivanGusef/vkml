package com.ifgroup.vkml.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 9:51 PM
 * May the force be with you always.
 */
public class PreferencesManager {

    private static PreferencesManager sPreferencesManager;

    public static PreferencesManager getInstance(Context mContext) {
        if (sPreferencesManager == null) {
            sPreferencesManager = new PreferencesManager(mContext);
        }
        return sPreferencesManager;
    }

    private SharedPreferences mPreferences;

    private PreferencesManager(Context mContext) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void save(String key, String value) {
        mPreferences.edit().putString(key, value).commit();
    }

    public void save(String key, int value) {
        mPreferences.edit().putInt(key, value).commit();
    }

    public void save(String key, long value) {
        mPreferences.edit().putLong(key, value).commit();
    }

    public void save(String key, float value) {
        mPreferences.edit().putFloat(key, value).commit();
    }

    public void save(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).commit();
    }

    public String get(String key, String defVal) {
        return mPreferences.getString(key, defVal);
    }

    public int get(String key, int defVal) {
        return mPreferences.getInt(key, defVal);
    }

    public long get(String key, long defVal) {
        return mPreferences.getLong(key, defVal);
    }

    public float get(String key, float defVal) {
        return mPreferences.getFloat(key, defVal);
    }

    public boolean get(String key, boolean defVal) {
        return mPreferences.getBoolean(key, defVal);
    }

    public void clear() {
        mPreferences.edit().clear().commit();
    }
}
