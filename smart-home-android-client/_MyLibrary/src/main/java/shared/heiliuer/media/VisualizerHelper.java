package shared.heiliuer.media;

import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;

public class VisualizerHelper {

	private static VisualizerHelper visualizerHelper;

	public static VisualizerHelper initVisualizerHelper(
			OnDataCaptureListener onDataCaptureListener, int audioSessionId) {
		visualizerHelper = new VisualizerHelper(onDataCaptureListener,
				audioSessionId);
		return visualizerHelper;
	}

	public static VisualizerHelper initVisualizerHelper(
			OnDataCaptureListener onDataCaptureListener) {
		visualizerHelper = new VisualizerHelper(onDataCaptureListener);
		return visualizerHelper;
	}

	public static VisualizerHelper getVisualizerHelper() {
		if (visualizerHelper == null) {
			throw new RuntimeException(
					"visualizerHelper has not been initialed");
		}
		return visualizerHelper;
	}

	private Visualizer visualizer;

	public VisualizerHelper(OnDataCaptureListener onDataCaptureListener) {
		this(onDataCaptureListener, 0);
	}

	public VisualizerHelper(OnDataCaptureListener onDataCaptureListener,
			int audioSessionId) {
		visualizer = new Visualizer(audioSessionId);
		visualizer.setEnabled(false);
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		visualizer.setDataCaptureListener(onDataCaptureListener,
				Visualizer.getMaxCaptureRate() / 16, true, false);
		visualizer.setEnabled(true);

	}

	public VisualizerHelper setEnalbed(boolean enabled) {
		visualizer.setEnabled(enabled);
		return this;
	}

	/**
	 * 设置捕获速率衰减倍数（2,4,8,16...）
	 * 
	 * @param dampTimes
	 *            衰减倍数
	 */
	public void setCaptureSizeDampTimes(int dampTimes) {
		visualizer.setCaptureSize(Visualizer.getMaxCaptureRate() / dampTimes);
	}

	/**
	 * 释放visualizer资源，再次使用需要重新调用方法initVisualizerHelper
	 */
	public void release() {
		visualizer.release();
		visualizer = null;
		visualizerHelper = null;
	}

}
