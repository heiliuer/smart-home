package com.lidroid.xutils.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InjectFragment extends Fragment {

	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		return rootView;
	}

	public void setContentView(int layoutResID) {
		rootView = mInflater.inflate(layoutResID, null);
	}

	protected View rootView;

	public View findViewById(int id) {
		return rootView.findViewById(id);
	}

}
