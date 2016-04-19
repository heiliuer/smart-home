package com.myhome.frame;

import android.app.Application;
import android.content.Intent;

import com.myhome.prefrences.PreferencesCommoms;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		PreferencesCommoms.initPreferenceHelper(getApplicationContext());
		startService(new Intent(this, ServiceMain.class));
	}
}
