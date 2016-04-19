package shared.heiliuer.img;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class ImgHelper {

	/**
	 * 限定范围进行缩放
	 * 
	 * @param bitmap
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap resize(Bitmap bitmap, int maxWidth, int maxHeight) {
		if (bitmap == null) {
			Log.e(TAG, " bimap is null");
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w > maxWidth || h > maxHeight) {
			float scale = Math.min((float) maxWidth / w, (float) maxHeight / h);
			return resize(bitmap, scale, scale);
		}
		return bitmap;
	}

	/**
	 * 缩放到指定尺寸
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resizeTo(Bitmap bitmap, int width, int height) {
		if (bitmap == null) {
			Log.e(TAG, " bimap is null");
			return null;
		}
		return resize(bitmap, (float) width / bitmap.getWidth(), (float) height
				/ bitmap.getHeight());
	}

	/**
	 * 按比例缩放位图
	 * 
	 * @param bitmap
	 * @param xScale
	 * @param yScale
	 * @return
	 */
	public static Bitmap resize(Bitmap bitmap, float xScale, float yScale) {
		if (bitmap == null) {
			Log.e(TAG, "resize(bitmap,  " + xScale + "," + yScale
					+ ") bimap is null");
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(xScale, yScale);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private static final String TAG = ImgHelper.class.getName();
}
