package com.myhome.frame;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhome.recorder.RecognizeService;
import com.myhome.prefrences.PreferencesCommoms;
import com.myhome.service.ComService;
import com.myhome.service.bluetooth.BluetoothService;

import shared.heiliuer.shared.Utils;

@ContentView(R.layout.act_welcome)
public class ActWelcome extends Activity implements ServiceConnection {

	private static final int TIME_RETRY = 60;

	/**
	 * 蓝牙适配器
	 */
	private BluetoothAdapter mBluetoothAdapter;

	private PreferencesCommoms preferencesCommoms;

	@ViewInject(value = R.id.status)
	private TextView statusTextView;

	@ViewInject(R.id.clear)
	private View btnClear;

	private View handlerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		ViewUtils.inject(this);
		handlerView = btnClear;
		// 启动蓝牙
		if (!mBluetoothAdapter.isEnabled()) {
			if (mBluetoothAdapter.enable()) {
				statusTextView.setText(R.string.open_bt_success);
			} else {
				statusTextView.setText(R.string.open_bt_failed);
			}
		}
		preferencesCommoms = PreferencesCommoms.getPreferenceHelper();
		String address = preferencesCommoms.getAddress();
		if (address == null) {
			Intent serverIntent = new Intent(this, ActBluetoothSearch.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		} else {
			BluetoothService.ADDRESS = address;
			startService();
		}

		startService(new Intent(this, RecognizeService.class));
	}

	@OnClick(R.id.clear)
	public void clearClick(View view) {
		preferencesCommoms.clear();
		Process.killProcess(Process.myPid());
	}

	private void startService() {
		handlerView.post(toStartService);
	}

	private final Runnable toStartService = new Runnable() {
		@Override
		public void run() {
			statusTextView.setText(R.string.starting_services);
			if (mBluetoothAdapter.isEnabled()) {
				Utils.l("正在绑定服务");
				bindService(new Intent(ActWelcome.this, ServiceMain.class),
						ActWelcome.this, Service.BIND_AUTO_CREATE);
			} else {
				handlerView.postDelayed(this, TIME_RETRY);
				statusTextView.setText(R.string.retry_connect);
			}
		}
	};

	private BinderServiceMain binderServiceMain;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		binderServiceMain = (BinderServiceMain) service;
		if (binderServiceMain != null) {
			Utils.l("onServiceConnected binderServiceMain != null ");
			binderServiceMain.startComService();
			handlerView.postDelayed(toActMain, TIME_RETRY);
		} else {
			Utils.l("onServiceConnected binderServiceMain = null ");
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		binderServiceMain = null;
	}

	private final Runnable toActMain = new Runnable() {
		@Override
		public void run() {
			Utils.l("toActMain binderServiceMain.getComServiceState()="
					+ binderServiceMain.getComServiceState());
			if (binderServiceMain.getComServiceState() == ComService.STATE_CONNECTED) {
				startActivity(new Intent(ActWelcome.this, ActMain.class));
				unbindService(ActWelcome.this);
				ActWelcome.this.finish();
			} else {
				handlerView.postDelayed(toActMain, TIME_RETRY);
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		handlerView.removeCallbacks(toStartService);
	};

	private static final int REQUEST_CONNECT_DEVICE = 1;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						ActBluetoothSearch.EXTRA_DEVICE_ADDRESS);
				BluetoothService.ADDRESS = address;
				startService();
			}
		}
	}

}
