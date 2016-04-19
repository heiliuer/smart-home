package shared.heiliuer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	private boolean isFocused;

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isFocused() {
		return isFocused;
	}

	public MarqueeTextView setIsFocused(boolean isFocused) {
		this.isFocused = isFocused;
		return this;
	}
}