package com.myhome.service.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import shared.heiliuer.shared.Utils;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.myhome.service.ComService;

public class BluetoothService extends ComService {

	// Debugging
	private static final String TAG = "BluetoothChatService";

	// Name for the SDP record when creating server socket
	private static final String NAME = "BluetoothChat";

	public static final UUID MY_UUID_SERIAL = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final int MSG_WAHT_NULL_BLUETOOTH_ADDRESS = 61;

	private final BluetoothAdapter mAdapter;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	public static String ADDRESS;

	@Override
	public boolean startService() {
		if (getMState() != BluetoothService.STATE_CONNECTED
				&& getMState() != BluetoothService.STATE_CONNECTING) {
			connnet(ADDRESS);
			return true;
		}
		return false;
	}

	/**
	 * Stop all threads
	 */
	@Override
	public synchronized boolean stopService() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		setMState(STATE_NONE);
		return true;
	}

	@Override
	public boolean send(byte[] out) {
		return write(out);
	}

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		setMState(STATE_NONE);
		setHandler(handler);
	}

	// ########## below methods are all private

	private final boolean connnet(String address) {
		if (address == null || address.trim().length() == 0) {
			sendMessage(MSG_WAHT_NULL_BLUETOOTH_ADDRESS);
			return false;
		}
		// Get the BLuetoothDevice object
		BluetoothDevice device = mAdapter.getRemoteDevice(address);

		// Attempt to connect to the device
		connect(device);
		return true;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	private final synchronized void start() {

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setMState(STATE_FAILED);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	private final synchronized void connect(BluetoothDevice device) {
		// Cancel any thread attempting to make a connection
		if (isMState(STATE_CONNECTING)) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setMState(STATE_CONNECTING);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	private final boolean write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (isNotMState(STATE_CONNECTED) || mConnectedThread == null)
				return false;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		return r.write(out);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	private final synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		// sendMessage(MSG_WHAT_TOAST, "设备名:" + device.getName());
		// 此处可获取连接的设备名
		setMState(STATE_CONNECTED);
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {

		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
						MY_UUID_SERIAL);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			setName("AcceptThread");
			BluetoothSocket socket = null;
			// Listen to the server socket if we're not connected
			while (isNotMState(STATE_CONNECTED)) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothService.this) {
						int state = getMState();
						switch (state) {
						case STATE_FAILED:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate
							// new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
		}

		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		final BluetoothSocket mmSocket;
		final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SERIAL);
			} catch (Exception e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");
			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();
			// Make sure mmSocket has been created
			if (mmSocket == null) {
				// Start the service over to restart listening mode
				BluetoothService.this.start();
				return;
			}
			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				setMState(STATE_FAILED);
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close() socket during connection failure",
							e2);
				}
				// Start the service over to restart listening mode
				BluetoothService.this.start();
				return;
			}
			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}
			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		boolean cancel() {
			if (mmSocket != null) {
				try {
					mmSocket.close();
					return true;
				} catch (IOException e) {
					Log.e(TAG, "close() of connect socket failed", e);
				}
			}
			return false;
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {

		private static final int BUFFER_SIZE = 1024;

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[BUFFER_SIZE], dataSend;
			int bytes, i = 0;
			// Keep listening to the InputStream while connected
			try {
				while (true) {
					bytes = mmInStream.read(buffer, i, 100);
					bytes += i;
					if (bytes < 4) {
						i = bytes;
						continue;
					}
					i = 0;
					dataSend = Arrays.copyOfRange(buffer, 0, bytes);
					sendMessage(MSG_WHAT_READ, dataSend);
					Utils.l("read data");
				}
			} catch (IOException e) {
				setMState(STATE_FAILED);
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public boolean write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				// Share the sent message back to the UI Activity
				sendMessage(MSG_WHAT_WRITE, buffer);
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
				setMState(STATE_FAILED);
			}
			return false;
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

}
