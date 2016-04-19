package com.myhome.frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.InjectFragment;
import com.lidroid.xutils.view.annotation.ContentView;

@ContentView(R.layout.fra_devices)
public class FraDevices extends InjectFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewUtils.inject(this);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fra_devices, menu);
	}
}
