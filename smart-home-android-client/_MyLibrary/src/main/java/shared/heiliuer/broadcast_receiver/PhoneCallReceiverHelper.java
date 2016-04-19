package shared.heiliuer.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PhoneCallReceiverHelper extends BroadcastReceiver {

	private static boolean isRegistered;
	private static Context context;
	private static PhoneCallReceiverHelper receiver;

	public static boolean register(Context c) {
		return register(c, null);
	}

	public static boolean register(Context c,
			OnHeadSetStateChange onHeadSetStateChange) {
		if (c == null) {
			return false;
		}
		context = c;
		if (receiver == null)
			receiver = new PhoneCallReceiverHelper(onHeadSetStateChange);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.HEADSET_PLUG");
		filter.setPriority(2147483647);
		// AudioManager audioManager = (AudioManager) c
		// .getSystemService(Service.AUDIO_SERVICE);
		// ComponentName mbCN = new ComponentName(c.getPackageName(),
		// HeadSetKeyReceiver.class.getName());
		// audioManager.unregisterMediaButtonEventReceiver(mbCN);
		// audioManager.registerMediaButtonEventReceiver(mbCN);
		c.registerReceiver(receiver, filter);
		isRegistered = true;
		return true;
	}

	public static boolean unRegister() {
		if (isRegistered) {
			receiver.setOnHeadSetStateChange(null);
			context.unregisterReceiver(receiver);
			return true;
		}
		return false;
	}

	private PhoneCallReceiverHelper() {
	}

	private PhoneCallReceiverHelper(OnHeadSetStateChange onHeadSetStateChange) {
		this.onHeadSetStateChange = onHeadSetStateChange;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("state")) {
			if (onHeadSetStateChange != null) {
				onHeadSetStateChange
						.headSet(intent.getIntExtra("state", 0) == 1);
			}
		}
	}

	private OnHeadSetStateChange onHeadSetStateChange;

	public void setOnHeadSetStateChange(
			OnHeadSetStateChange onHeadSetStateChange) {
		this.onHeadSetStateChange = onHeadSetStateChange;
	}

	public static interface OnHeadSetStateChange {
		public void headSet(boolean isInOrOut);
	}
}
