package com.myhome.utils;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class ClsUtils {
	public static BluetoothDevice remoteDevice = null;

	static public boolean createBond(BluetoothDevice btDevice) throws Exception {
		Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */

	static public boolean removeBond(BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	static public boolean setPin(BluetoothDevice btDevice, String str)
			throws Exception {
		try {
			Method removeBondMethod = BluetoothDevice.class.getDeclaredMethod(
					"setPin", new Class[] { byte[].class });
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
					new Object[] { str.getBytes() });
			Log.d("returnValue", "setPin is success " + btDevice.getAddress()
					+ returnValue.booleanValue());
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	// 取消用户输入
	static public boolean cancelPairingUserInput(BluetoothDevice device)
			throws Exception {
		Method createBondMethod = BluetoothDevice.class
				.getMethod("cancelPairingUserInput");
		// cancelBondProcess()
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		Log.d("returnValue",
				"cancelPairingUserInput is success "
						+ returnValue.booleanValue());
		return returnValue.booleanValue();
	}

	// 取消配对
	static public boolean cancelBondProcess(BluetoothDevice device)
			throws Exception {
		Method createBondMethod = BluetoothDevice.class
				.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

}