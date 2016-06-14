/*******************************************************************************
 *
 *  *     Nervousnet - a distributed middleware software for social sensing. 
 *  *      It is responsible for collecting and managing data in a fully de-centralised fashion
 *  *
 *  *     Copyright (C) 2016 ETH Zürich, COSS
 *  *
 *  *     This file is part of Nervousnet Framework
 *  *
 *  *     Nervousnet is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     Nervousnet is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  * 	Contributors:
 *  * 	Dinesh Acharya - acharyad@student.ethz.ch  -  Sensor visualization view implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class DecibelMeterView extends View {

	public static final int SIZE = 300;
	public static final float TOP = 0.0f;
	public static final float LEFT = 0.0f;
	public static final float RIGHT = 1.0f;
	public static final float BOTTOM = 1.0f;
	public static final float CENTER = 0.5f;

	private Bitmap mBackground;
	private Paint mBackgroundPaint;
	private RectF mOuterCircleRect;
	private RectF mInnerCircleRect;
	private RectF mScaleRect;

	private float startAngle = 30;
	private float endAngle = 330;
	private float mDivisions = 7;
	private float mSubdivisions = 5;
	private float startValue = 0;
	private float endValue = 140;
	private float dB = 0;
	private float MAX_DB = 140;
	private int scaleColor = Color.rgb(250, 250, 250);
	private int needleColor = Color.rgb(250, 250, 250);
	private int rimColor = Color.rgb(241, 153, 24);
	private int backgroundColor = Color.rgb(241, 49, 0);

	private float mSubdivisionAngle = (endAngle - startAngle) / (mDivisions * mSubdivisions);
	private float mDivisionValue = (startValue - endValue) / mDivisions;

	public DecibelMeterView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		// readAttrs(context, attrs, defStyle);
		init();
	}

	public DecibelMeterView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DecibelMeterView(final Context context) {
		this(context, null, 0);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		drawBackground(canvas);

		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0,
				(scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

		drawNeedle(canvas);

	}

	private void drawNeedle(Canvas canvas) {
		float degrees = startAngle + (endAngle - startAngle) * dB / MAX_DB;
		canvas.save();
		canvas.rotate(180 + degrees, CENTER, CENTER);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(needleColor);
		Path path = new Path();
		path.moveTo(CENTER + 0.02f, CENTER + 0.02f);
		path.lineTo(CENTER, CENTER - 0.3f);
		path.lineTo(CENTER - 0.02f, CENTER + 0.02f);

		// path.close();
		canvas.drawPath(path, paint);
		// canvas.drawRect(new RectF(0.1f,0.1f,0.5f,0.5f),paint);
		canvas.restore();
		canvas.drawOval(new RectF(CENTER - 0.05f, CENTER - 0.05f, CENTER + 0.05f, CENTER + 0.05f), paint);
	}

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
		drawSoundBackground();
	}

	private void drawSoundBackground() {
		if (null != mBackground) {
			mBackground.recycle();
		}

		// Create a new background according to the new width and height
		mBackground = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(mBackground);
		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0,
				(scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		drawRim(canvas);
		drawScale(canvas);
	}

	private void drawRim(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(rimColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawOval(mOuterCircleRect, paint);
		paint.setColor(backgroundColor);
		canvas.drawOval(mInnerCircleRect, paint);
	}

	private float getValueForTick(final int tick) {
		return startValue + tick * (mDivisionValue / mSubdivisions);
	}

	private void drawScale(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		// On canvas, North is 0 degrees, East is 90 degrees, South is 180 etc.
		// We start the scale somewhere South-West so we need to first rotate
		// the canvas.
		float mScaleRotation = (startAngle + 180) % 360;
		canvas.rotate(mScaleRotation, 0.5f, 0.5f);

		final int totalTicks = (int) (mDivisions * mSubdivisions + 1);
		for (int i = 0; i < totalTicks; i++) {
			final float y1 = mScaleRect.top;
			final float y2 = y1 + 0.015f; // height of division
			final float y3 = y1 + 0.045f; // height of subdivision

			final float value = getValueForTick(i);
			// final Paint paint = getRangePaint(mScaleStartValue + value);
			Paint paint = new Paint();
			paint.setColor(scaleColor);
			paint.setStrokeWidth(0.005f);
			paint.setTextSize(0.05f);

			float mod = value % mDivisionValue;
			if ((Math.abs(mod - 0) < 0.001) || (Math.abs(mod - mDivisionValue) < 0.001)) {
				// Draw a division tick
				canvas.drawLine(0.5f, y1, 0.5f, y3, paint);
				// Draw the text 0.15 away from the division tick
				drawTextOnCanvasWithMagnifier(canvas, String.format("%d", (int) -value), 0.5f, y3 + 0.045f, paint);
			} else {
				// Draw a subdivision tick
				canvas.drawLine(0.5f, y1, 0.5f, y2, paint);
			}
			canvas.rotate(mSubdivisionAngle, 0.5f, 0.5f);
		}
		canvas.restore();
	}

	public static void drawTextOnCanvasWithMagnifier(Canvas canvas, String text, float x, float y, Paint paint) {
		if (android.os.Build.VERSION.SDK_INT <= 15) {
			canvas.drawText(text, x, y, paint);
		} else {
			// workaround
			float originalStrokeWidth = paint.getStrokeWidth();
			float originalTextSize = paint.getTextSize();
			final float magnifier = 100f;

			canvas.save();
			canvas.scale(1f / magnifier, 1f / magnifier);

			paint.setTextSize(originalTextSize * magnifier);
			paint.setStrokeWidth(originalStrokeWidth * magnifier);

			canvas.drawText(text, x * magnifier, y * magnifier, paint);
			canvas.restore();

			paint.setTextSize(originalTextSize);
			paint.setStrokeWidth(originalStrokeWidth);
		}
	}

	@TargetApi(11)
	public void init() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		initDrawingRects();
	}

	public void initDrawingRects() {
		mOuterCircleRect = new RectF(LEFT + .15f, TOP + .15f, RIGHT - .15f, BOTTOM - .15f);
		mInnerCircleRect = new RectF(mOuterCircleRect.left + .02f, mOuterCircleRect.top + .02f,
				mOuterCircleRect.right - .02f, mOuterCircleRect.bottom - .02f);
		mScaleRect = new RectF(mInnerCircleRect);
		mScaleRect.inset(.02f, .02f);
	}

	public void initDrawingTools() {
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setFilterBitmap(true);
	}

	private void drawBackground(final Canvas canvas) {
		if (null != mBackground) {
			canvas.drawBitmap(mBackground, 0, 0, mBackgroundPaint);
		}
	}

	@Override
	protected void onRestoreInstanceState(final Parcelable state) {
		final Bundle bundle = (Bundle) state;
		final Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);

		// mChargingState = bundle.getBoolean("mChargingState");
		// mCurrentBatteryLevel = bundle.getFloat("mCurrentBatteryLevel");
		// mTargetBatteryLevel = bundle.getFloat("mTargetBatteryLevel");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();

		final Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		// state.putBoolean("mChargingState", mChargingState);

		return state;
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final int chosenWidth = chooseDimension(widthMode, widthSize);
		final int chosenHeight = chooseDimension(heightMode, heightSize);
		setMeasuredDimension(chosenWidth, chosenHeight);
	}

	private int chooseDimension(final int mode, final int size) {
		switch (mode) {
		case View.MeasureSpec.AT_MOST:
		case View.MeasureSpec.EXACTLY:
			return size;
		case View.MeasureSpec.UNSPECIFIED:
		default:
			return getDefaultDimension();
		}
	}

	private int getDefaultDimension() {
		return SIZE;
	}

	public void setDecibleValue(float value) {
		dB = value > 140 ? 140 : value;
		invalidate();
	}
}
