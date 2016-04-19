package com.myhome.service;

import android.os.Handler;

public abstract class MsgPoster {

	private Handler handler;

	public final void setHandler(Handler handler) {
		this.handler = handler;
	}

	protected final boolean sendMessage(int what) {
		return sendMessage(what, 0, 0, null);
	}

	protected final boolean sendMessage(int what, Object obj) {
		return sendMessage(what, 0, 0, obj);
	}

	protected final boolean sendMessage(int what, int arg1, int arg2) {
		return sendMessage(what, arg1, arg2, null);
	}

	protected final boolean sendMessage(int what, int arg1, Object obj) {
		return sendMessage(what, arg1, 0, obj);
	}

	protected final boolean sendMessage(int what, int arg1, int arg2, Object obj) {
		if (handler == null)
			return false;
		handler.obtainMessage(what, arg1, arg2, obj).sendToTarget();
		return true;
	}

	public boolean sendEmptyMessageDelayed(int what, long delayMillis) {
		if (handler == null) {
			return false;
		}
		handler.sendEmptyMessageDelayed(what, delayMillis);
		return true;
	}
}
