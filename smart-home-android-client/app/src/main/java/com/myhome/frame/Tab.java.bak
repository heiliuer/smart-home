package com.myhome.frame;

import android.support.v4.app.Fragment;

import com.heiliuer.myhome.R;

public class Tab {

	public int resTxt;
	public int resImg;
	public Class<? extends Fragment> fraCls;
	public String tabId;

	public Tab(int resTxt, int resImg, Class<? extends Fragment> fraCls,
			String tabId) {
		this.resTxt = resTxt;
		this.resImg = resImg;
		this.fraCls = fraCls;
		this.tabId = tabId;
	}

	public static final Tab[] TABS = new Tab[] {
			new Tab(R.string.chip, R.drawable.tab_seach_selector,
					FraChip.class, "FraDbIndex"),
			new Tab(R.string.devices, R.drawable.tab_test_selector,
					FraDevices.class, "FraProductTest") };

}
