package com.lidroid.xutils.view;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.View;

/**
 * Author: wyouflf Date: 13-9-9 Time: 下午12:29
 */
public class ViewFinder {

	private View view;
	private Activity activity;
	private PreferenceGroup preferenceGroup;
	private PreferenceActivity preferenceActivity;
	private InjectFragment fragment;
	private InjectableContext context;

	public ViewFinder(View view) {
		this.view = view;
	}

	public ViewFinder(InjectFragment fragment) {
		this.fragment = fragment;
	}

	public ViewFinder(Activity activity) {
		this.activity = activity;
	}

	public ViewFinder(PreferenceGroup preferenceGroup) {
		this.preferenceGroup = preferenceGroup;
	}

	public ViewFinder(PreferenceActivity preferenceActivity) {
		this.preferenceActivity = preferenceActivity;
		this.activity = preferenceActivity;
	}

	public ViewFinder(InjectableContext context) {
		this.context = context;
	}

	public View findViewById(int id) {
		if (activity != null) {
			return activity.findViewById(id);
		} else if (view != null) {
			return view.findViewById(id);
		} else if (fragment != null) {
			return fragment.findViewById(id);
		} else if (context != null) {
			return context.findViewById(id);
		}
		return null;
	}

	public View findViewByInfo(ViewInjectInfo info) {
		return findViewById((Integer) info.value, info.parentId);
	}

	public View findViewById(int id, int pid) {
		View pView = null;
		if (pid > 0) {
			pView = this.findViewById(pid);
		}

		View view = null;
		if (pView != null) {
			view = pView.findViewById(id);
		} else {
			view = this.findViewById(id);
		}
		return view;
	}

	@SuppressWarnings("deprecation")
	public Preference findPreference(CharSequence key) {
		return preferenceGroup == null ? preferenceActivity.findPreference(key)
				: preferenceGroup.findPreference(key);
	}

	public Context getContext() {
		if (view != null)
			return view.getContext();
		if (activity != null)
			return activity;
		if (preferenceActivity != null)
			return preferenceActivity;
		if (fragment != null)
			return fragment.getActivity();
		if (context != null)
			return context.getContext();
		return null;
	}
}
