package shared.heiliuer.media;

import android.content.Context;
import android.media.AudioManager;

/**
 * 已通过
 * 
 * @author heiliuer
 * 
 */
public class AudioManagerHelper {
	private static AudioManagerHelper audioManagerHelper;

	/**
	 * 初始化
	 * 
	 * @param c
	 *            强烈建议使用application context
	 * @return
	 */
	public static AudioManagerHelper initAudioManagerHelper(Context c) {
		if (c == null)
			return null;
		audioManagerHelper = new AudioManagerHelper(c);
		return audioManagerHelper;
	}

	/**
	 * 获取AudioManagerHelper单例
	 * 
	 * @return
	 */
	public static AudioManagerHelper getAudioManagerHelper() {
		if (audioManagerHelper == null) {
			throw new RuntimeException(
					"getAudioManagerHelper之前未调用initAudioManagerHelper");
		}
		return audioManagerHelper;
	}

	private AudioManager audioManager;
	private int streamType = AudioManager.STREAM_MUSIC;

	private AudioManagerHelper(Context c) {
		audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * 设置音频流种类
	 * 
	 * @param streamType
	 * @return
	 */
	public AudioManagerHelper setStreamType(int streamType) {
		this.streamType = streamType;
		return this;
	}

	/**
	 * 获取最大音量
	 * 
	 * @return
	 */
	public int getMaxVolume() {
		return audioManager.getStreamMaxVolume(streamType);
	}

	/**
	 * 获取当前音量
	 * 
	 * @return
	 */
	public int getVolume() {
		return audioManager.getStreamVolume(streamType);
	}

	/**
	 * 设置音量
	 * 
	 * @param volume
	 * @param flag
	 * @return
	 */
	public AudioManagerHelper setVolume(int volume, int flag) {
		audioManager.setStreamVolume(streamType, volume, flag);
		return this;
	}

	/**
	 * 设置音量
	 * 
	 * @param volume
	 * @param flag
	 * @return
	 */
	public AudioManagerHelper setVolume(int volume) {
		audioManager.setStreamVolume(streamType, volume,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		return this;
	}
}
