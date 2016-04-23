package com.myhome.frame;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.myhome.chip.IOHandler;
import com.myhome.chip.IOP;
import com.myhome.chip.IOReadAndWrite;
import com.myhome.prefrences.PreferencesCommoms;
import com.myhome.service.ComService;
import com.myhome.service.bluetooth.BluetoothService;

import java.util.Vector;

import shared.heiliuer.shared.Utils;

/**
 * [常驻] 主要负责命令的转发和解析，模式的控制
 * 
 * @author hao.Wang
 */
public final class ServiceMain extends Service {

	private IOHandler ioHandler;

	private static ServiceMain serviceMain;

	public static ServiceMain getServiceMain() {
		return serviceMain;
	}

	private ComService comService;

	public ComService getComService() {
		return comService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		serviceMain = this;
		registerBluetoothReceiver();
		comService = new BluetoothService(ServiceMain.this, serialMsgHandler);
		ioHandler = new IOHandler() {

			@Override
			public boolean postSetPIO(IOP pio) {
				byte p = (byte) pio.getP();
				switch (pio.getTag()) {
				case IOP.TAG_P0:
					return comService.send(new byte[] { (byte) 0xa0, p });
				case IOP.TAG_P1:
					return comService.send(new byte[] { (byte) 0xa1, p });
				case IOP.TAG_P2:
					return comService.send(new byte[] { (byte) 0xa2, p });
				case IOP.TAG_P3:
					return comService.send(new byte[] { (byte) 0xa3, p });
				}
				return false;
			}

			@Override
			public boolean postQueryPIO() {
				return comService.send(new byte[] { (byte) 0xaa, (byte) 0xff });
			}
		};
	}
	
	

	public final Handler serialMsgHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ComService.MSG_WHAT_READ:
				byte[] datas = (byte[]) msg.obj;
				msg.obj = null;
				handleReadCmd(datas);
				break;
			case ComService.MSG_WHAT_STATE_CHANGE:
				handleComServiceStateChanged(msg.arg1, msg.arg2);
				break;
			case BluetoothService.MSG_WHAT_TOAST:
				Utils.showNewToast(ServiceMain.this, msg.obj.toString());
				break;
			case BluetoothService.MSG_WAHT_NULL_BLUETOOTH_ADDRESS:
				Utils.showNewToast(ServiceMain.this, "蓝牙地址为空");
				break;
			}
		};
	};

	private static final int RECONNECT_TIME_OUT = 2000;

	private void handleComServiceStateChanged(int stateFrom, int stateTo) {
		/*
		 * String toastMsg = "连接状态变化：" + ComService.getStateName(stateFrom) +
		 * "->" + ComService.getStateName(stateTo);
		 * Utils.showNewToast(ServiceMain.this, toastMsg);
		 */
		Utils.showToast(ServiceMain.this, "蓝牙：" + comService.getCurrentStateName());
		//Utils.l("蓝牙：" + comService.getCurrentStateName());

		for (OnComserviceStateChanged l : onComserviceStateChangeds) {
			l.comServiceStateChange(stateFrom, stateTo);
		}
		switch (stateTo) {
		case ComService.STATE_CONNECTED:
			if (comService instanceof BluetoothService) {
				PreferencesCommoms.getPreferenceHelper().setAddress(
						BluetoothService.ADDRESS);
			}
			break;
		case ComService.STATE_FAILED:
			/*
			 * Utils.showToast(this, "连接失败!" + RECONNECT_TIME_OUT / 1000 +
			 * "秒后自动重连");
			 */
			serialMsgHandler.removeCallbacks(reconnectComservice);
			serialMsgHandler.postDelayed(reconnectComservice,
					RECONNECT_TIME_OUT);
		}
	}

	private Runnable reconnectComservice = new Runnable() {

		@Override
		public void run() {
			comService.startService();
		}
	};

	private boolean handleReadCmd(byte datas[]) {
		onReadData(datas);
		return true;
	}
	
	

	public void onReadData(byte[] datas) {
		for (OnDataIOListener l : onDataIOListeners) {
			l.onReadData(datas);
		}
		if (datas != null && datas.length == 4) {
			ioHandler.setPIOS(datas[0] & 0xff, datas[1] & 0xff,
					datas[2] & 0xff, datas[3] & 0xff);
		}
	}

	public void onWriteData(byte[] datas) {
		for (OnDataIOListener l : onDataIOListeners) {
			l.onWriteData(datas);
		}
	}

	public Vector<OnDataIOListener> onDataIOListeners = new Vector<ServiceMain.OnDataIOListener>();

	public void addOnDataIOListeners(OnDataIOListener onDataIOListener) {
		if (onDataIOListeners == null)
			return;
		if (!onDataIOListeners.contains(onDataIOListener)) {
			onDataIOListeners.add(onDataIOListener);
		}
	}

	public boolean removeOnDataIOListeners(OnDataIOListener onDataIOListener) {
		if (onDataIOListeners == null)
			return true;
		return onDataIOListeners.remove(onDataIOListener);
	}

	public static interface OnDataIOListener {
		void onReadData(byte[] datas);

		void onWriteData(byte[] datas);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ungisterBluetoothReceiver();
		onDataIOListeners.clear();
	}

	public ServiceMain getContext() {
		return ServiceMain.this;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new BinderServiceMain() {

			@Override
			public int getComServiceState() {
				return comService.getMState();
			}

			@Override
			public ServiceMain getServiceMain() {
				return ServiceMain.this;
			}

			@Override
			public void startComService() {
				comService.startService();
			}

			@Override
			public void stopComService() {
				comService.stopService();
			}

			@Override
			public IOReadAndWrite getIOHandler() {
				return ioHandler;
			}
		};
	}

	private final void registerBluetoothReceiver() {
		bluetoothReceiver = new BluetoothReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		registerReceiver(bluetoothReceiver, filter);
	}

	private final void ungisterBluetoothReceiver() {
		unregisterReceiver(bluetoothReceiver);
		bluetoothReceiver = null;
	}

	private BluetoothReceiver bluetoothReceiver;

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getAddress().equalsIgnoreCase(
						BluetoothService.ADDRESS)) {
					if (comService != null) {
						switch (device.getBondState()) {
						case BluetoothDevice.BOND_NONE:
							Utils.l("receiver:BOND_NONE");
							comService.setMState(ComService.STATE_NONE);
							break;
						case BluetoothDevice.BOND_BONDING:
							Utils.l("receiver:BOND_BONDING");
							comService.setMState(ComService.STATE_CONNECTING);
							break;
						case BluetoothDevice.BOND_BONDED:
							Utils.l("receiver:BOND_BONDED");
							comService.setMState(ComService.STATE_CONNECTED);
							break;
						}
						device.setPairingConfirmation(false);
					}
				}
			} else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				device.setPairingConfirmation(false);
				device.setPin("1234".getBytes());
			}
		}
	}

	private Vector<OnComserviceStateChanged> onComserviceStateChangeds = new Vector<ServiceMain.OnComserviceStateChanged>();

	public void addOnComserviceStateChanged(
			OnComserviceStateChanged onComserviceStateChanged) {
		if (onComserviceStateChanged == null
				|| onComserviceStateChangeds.contains(onComserviceStateChanged)) {
			return;
		}
		onComserviceStateChangeds.add(onComserviceStateChanged);
	}

	public void removeOnComserviceStateChanged(
			OnComserviceStateChanged onComserviceStateChanged) {
		if (onComserviceStateChanged == null
				|| !onComserviceStateChangeds
						.contains(onComserviceStateChanged)) {
			return;
		}
		onComserviceStateChangeds.remove(onComserviceStateChanged);
	}

	public static interface OnComserviceStateChanged {
		public void comServiceStateChange(int stateFrom, int stateTo);
	}

}
