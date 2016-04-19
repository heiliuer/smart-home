package shared.heiliuer.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

	public VerticalSeekBar(Context context) {
		super(context);
		init();
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private final void init() {
		try {
			mySetProgress = ProgressBar.class.getDeclaredMethod("setProgress",
					int.class, boolean.class);
			mySetProgress.setAccessible(true);
			theProgress = getProgress();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	private int theProgress = 0;

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	private Method mySetProgress;

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());

	}

	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-getHeight(), 0);
		super.onDraw(c);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			int i = getMax() - (int) (getMax() * event.getY() / getHeight());
			if (i != theProgress) {
				theProgress = i;
				try {
					mySetProgress.invoke(this, i, true);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}
}
