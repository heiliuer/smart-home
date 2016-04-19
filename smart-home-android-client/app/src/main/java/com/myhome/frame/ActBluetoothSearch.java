package com.myhome.frame;

import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

@ContentView(R.layout.act_device_list)
public class ActBluetoothSearch extends Activity {

	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	@ViewInject(R.id.paired_devices)
	private ListView pairedListView;

	@ViewInject(R.id.new_devices)
	private ListView newDevicesListView;

	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	@OnClick(R.id.button_scan)
	public void onBtnScanClick(View v) {
		doDiscovery();
		v.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ViewUtils.inject(this);
		setResult(Activity.RESULT_CANCELED);
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		registerBluetoothReceiver();
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(mReceiver);
	}

	private final void registerBluetoothReceiver() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		this.registerReceiver(mReceiver, filter);
	}

	private final void doDiscovery() {
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		mBtAdapter.startDiscovery();
	}

	public static final UUID MY_UUID_SERIAL = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	@OnItemClick({ R.id.new_devices, R.id.paired_devices })
	public void onDeviceListItemClick(AdapterView<?> av, View v, int arg2,
			long arg3) {
		mBtAdapter.cancelDiscovery();
		String info = ((TextView) v).getText().toString();
		String address = info.substring(info.length() - 17);
		finishOK(address);
	}

	public final void finishOK(String address) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(
							R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}
