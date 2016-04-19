package com.myhome.frame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.act_main)
public class ActMain extends FragmentActivity {

	@ViewInject(android.R.id.tabhost)
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		initTabs();
	}

	private final void initTabs() {
		LayoutInflater inflater = getLayoutInflater();
		for (Tab t : Tab.TABS) {
			View tabView = inflater.inflate(R.layout.tab, null);
			((ImageView) tabView.findViewById(R.id.img))
					.setImageResource(t.resImg);
			((TextView) tabView.findViewById(R.id.txt)).setText(t.resTxt);
			mTabHost.addTab(mTabHost.newTabSpec(t.tabId).setIndicator(tabView),
					t.fraCls, null);
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(Intent.ACTION_MAIN).setFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK)
				.addCategory(Intent.CATEGORY_HOME));
	}

}
