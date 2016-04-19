package shared.heiliuer.media;

import java.util.ArrayList;
import java.util.List;

import shared.heiliuer.shared.Utils;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MediaManager implements OnCompletionListener {

	private List<MediaPlayer> access;
	private List<MediaPlayer> busy;

	public MediaManager() {
		access = new ArrayList<MediaPlayer>();
		busy = new ArrayList<MediaPlayer>();
	}

	public int getTotalNum() {
		return access.size() + busy.size();
	}

	public int getAccessNum() {
		return access.size();
	}

	public int getBusyNum() {
		return busy.size();
	}

	synchronized public void forceToAccess(MediaPlayer m) {
		if (m != null ) {
			m.stop();
			m.reset();
			m.setLooping(false);
			busy.remove(m);
			access.add(m);
		}
	}

	synchronized public MediaPlayer getOneAccess() {
		MediaPlayer m;
		if (access.size() == 0) {
			m = new MediaPlayer();
			m.setOnCompletionListener(this);
		} else {
			m = access.remove(0);
			m.reset();
		}
		busy.add(m);
		return m;
	}

	@Override
	synchronized public void onCompletion(MediaPlayer mp) {
		if (mp.isLooping()) {
			return;
		}
		if (busy.contains(mp)) {
			busy.remove(mp);
		}
		access.add(mp);
		if (getAccessNum() > 6) {
			access.remove(0).release();
			access.remove(0).release();
			access.remove(0).release();
		}
		Utils.l("all:" + getTotalNum() + ",access:" + getAccessNum() + ",busy:"
				+ getBusyNum());
	}
}
