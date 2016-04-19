package shared.heiliuer.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class HeadSetKeyReceiver extends BroadcastReceiver {
	public static boolean MEDIA_UNMOUT = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
				|| action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			//MyService.sendMes(Mes.mediaUnmount);
		}
		KeyEvent keyEvent = (KeyEvent) intent
				.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
				//MyService.sendMes(Mes.headsethook_down);
			} else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
			}
		}
	}
}
