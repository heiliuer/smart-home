package com.myhome.prefrences;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareHelper {

	public static ShareHelper getShareHelperInstance(Context c, String name,
			int mode) {
		if (c == null) {
			throw new IllegalArgumentException("context is null");
		}
		return new ShareHelper(c, name, mode);
	}

	public static ShareHelper getShareHelperInstance(Context c, String name) {
		if (c == null) {
			throw new IllegalArgumentException("context is null");
		}
		return new ShareHelper(c, name, Context.MODE_PRIVATE);
	}

	public static ShareHelper getShareHelperInstance(Context c) {
		if (c == null) {
			throw new IllegalArgumentException("context is null");
		}
		return new ShareHelper(c, c.getClass().getName(), Context.MODE_PRIVATE);
	}

	private SharedPreferences shared;

	private ShareHelper(Context c, String name, int mode) {
		shared = c.getSharedPreferences(name, mode);
	}

	public void clearAll() {
		shared.edit().clear().commit();
	}

	public Set<String> getStrings(String key, Set<String> defValues) {
		return shared.getStringSet(key, defValues);
	}

	public void setStrings(String key, Set<String> values) {
		shared.edit().putStringSet(key, values).commit();
	}

	public int getInt(String key, int defValue) {
		return shared.getInt(key, defValue);
	}

	public void setInt(String key, int value) {
		shared.edit().putInt(key, value).commit();
	}

	public String getString(String key, String defValue) {
		return shared.getString(key, defValue);
	}

	public void setString(String key, String value) {
		shared.edit().putString(key, value).commit();
	}

	public float getFloat(String key, float defValue) {
		return shared.getFloat(key, defValue);
	}

	public void setFloat(String key, float value) {
		shared.edit().putFloat(key, value).commit();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return shared.getBoolean(key, defValue);
	}

	public void setBoolean(String key, boolean value) {
		shared.edit().putBoolean(key, value).commit();
	}

	public long getLong(String key, long defValue) {
		return shared.getLong(key, defValue);
	}

	public void setLong(String key, long value) {
		shared.edit().putLong(key, value).commit();
	}
}
