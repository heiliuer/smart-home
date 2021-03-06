package com.myhome.service;

public abstract class ComService extends MsgPoster {

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing

	public static final int STATE_FAILED = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote

	/**
	 * arg1=fromState,arg2=toState
	 */
	public static final int MSG_WHAT_STATE_CHANGE = 34;

	public static final int MSG_WHAT_READ = 38;

	public static final int MSG_WHAT_WRITE = 39;

	/**
	 * obj=the toast text
	 */
	public static final int MSG_WHAT_TOAST = 41;

	public abstract boolean send(byte[] out);

	public abstract boolean startService();

	public abstract boolean stopService();

	private int state = STATE_NONE;

	/**
	 * Return the current connection state.
	 */
	public synchronized final int getMState() {
		return state;
	}

	public final boolean isMState(int state) {
		return this.state == state;
	}

	public static final String getStateName(int state) {
		switch (state) {
		case STATE_CONNECTED:
			return "已连接";
		case STATE_CONNECTING:
			return "连接中";
		case STATE_FAILED:
			return "连接失败";
		case STATE_NONE:
			return "无连接";
		}
		return "未知状态";
	}

	public final String getCurrentStateName() {
		return getStateName(this.state);
	}

	public final boolean isNotMState(int state) {
		return this.state != state;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	public synchronized final void setMState(int state) {
		int fromState = this.state;
		this.state = state;
		if (fromState != state) {
			sendMessage(MSG_WHAT_STATE_CHANGE, fromState, state);
		}
	}

}
