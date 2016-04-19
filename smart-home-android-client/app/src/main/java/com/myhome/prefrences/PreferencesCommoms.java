package com.myhome.prefrences;

import android.content.Context;

/**
 * @author hao.wang
 */
public class PreferencesCommoms {

	private static PreferencesCommoms data;

	private ShareHelper share;

	public static PreferencesCommoms getPreferenceHelper() {
		if (data == null)
			throw new NullPointerException("在使用GlobalData前未初始化");
		return data;
	}

	/**
	 * 
	 * @param applicationContext
	 *            强烈建议用applicationContext
	 * @return
	 */
	public static PreferencesCommoms initPreferenceHelper(
			Context applicationContext) {
		if (applicationContext == null)
			throw new NullPointerException("applicationContext is null");
		data = new PreferencesCommoms(applicationContext,
				applicationContext.getPackageName());
		return data;
	}

	/**
	 * 
	 * @param applicationContext
	 *            强烈建议用applicationContext
	 * @return
	 */
	public static PreferencesCommoms initPreferenceHelper(
			Context applicationContext, String name) {
		if (applicationContext == null || name == null
				|| name.trim().length() == 0)
			throw new NullPointerException("applicationContext or name is null");
		data = new PreferencesCommoms(applicationContext, name);
		return data;
	}

	private PreferencesCommoms(Context applicationContext, String name) {
		share = ShareHelper.getShareHelperInstance(applicationContext, name);
		initData();
	}

	public void clear() {
		share.clearAll();
	}

	// 自定义share数据
	// begin

	private void initData() {
		address = share.getString("address", null);
	}

	private String address;

	/**
	 * @return maybe null
	 */
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		share.setString("address", address);
	}
	// end

}
