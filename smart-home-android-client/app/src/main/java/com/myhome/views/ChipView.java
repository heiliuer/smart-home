package com.myhome.views;

import shared.heiliuer.shared.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChipView extends View {

	public ChipView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ChipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChipView(Context context) {
		super(context);
		init();
	}

	private final void init() {
		paintPin = new Paint();
		paintPin.setColor(Color.rgb(188, 188, 188));
		paintPin.setStyle(Style.FILL);
		paintChip = new Paint();
		paintChip.setColor(Color.rgb(75, 74, 68));
		paintChip.setStyle(Style.FILL);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		paddingLeft = getPaddingLeft();
		width = getWidth() - paddingLeft - getPaddingRight();
		paddingTop = getPaddingTop();
		height = getHeight() - paddingTop - getPaddingBottom();
		Utils.l("height:" + height + ",width:" + width);
		heightPin = height
				/ (PIN_NUMS_ONE_SIDE + TIMES_HEIGHT_GAP_TO_PIN
						* (PIN_NUMS_ONE_SIDE - 1));
		heightPinAndGap = heightPin * (1 + TIMES_HEIGHT_GAP_TO_PIN);
		widthPin = width / (2 + TIMES_WIDTH_CHIP_TO_PIN);
		widthChip = width - widthPin - widthPin;
	}

	private static final int PIN_NUMS_ONE_SIDE = 16;
	private static final float TIMES_HEIGHT_GAP_TO_PIN = 0.4f;
	private static final float TIMES_WIDTH_CHIP_TO_PIN = 4;
	private float widthChip;
	private float widthPin;
	private float heightPin;
	private float heightPinAndGap;
	private float height;
	private float width;
	private float PADDING_CHIP_SECOND_LAYOUT = 10f;
	private int paddingLeft, paddingTop;
	private RectF rect;
	private Paint paintPin, paintChip;

	@Override
	protected void onDraw(Canvas canvas) {
		if (rect == null) {
			rect = new RectF();
		}
		float top;
		float left = paddingLeft + width - widthPin;
		for (int i = 0; i < PIN_NUMS_ONE_SIDE; i++) {
			top = paddingTop + heightPinAndGap * i;
			Utils.l(i + ":draw" + top);
			rect.set(paddingLeft, top, paddingLeft + widthPin, top + heightPin);
			canvas.drawRect(rect, paintPin);
			rect.set(left, top, left + widthPin, top + heightPin);
			canvas.drawRect(rect, paintPin);
		}
		rect.set(paddingLeft + widthPin, paddingTop, paddingLeft + widthPin
				+ widthChip, paddingTop + height);
		paintChip.setColor(Color.argb(200, 45, 45, 45));
		canvas.drawRect(rect, paintChip);
		rect.set(paddingLeft + widthPin + PADDING_CHIP_SECOND_LAYOUT,
				paddingTop + PADDING_CHIP_SECOND_LAYOUT, paddingLeft + widthPin
						+ widthChip - PADDING_CHIP_SECOND_LAYOUT, paddingTop
						+ height - PADDING_CHIP_SECOND_LAYOUT);
		paintChip.setColor(Color.rgb(40, 40, 40));
		canvas.drawRoundRect(rect, 10, 10, paintChip);
	}
}
