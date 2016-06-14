package ch.ethz.coss.nervousnet.hub.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;

public class AccelerometerSensorView extends View {
	
	public static final int SIZE = 300;
	public static final float TOP = 0.0f;
	public static final float LEFT = 0.0f;
	public static final float RIGHT = 1.0f;
	public static final float BOTTOM = 1.0f;
	public static final float CENTER = 0.5f;
	public static final float G = 9.80665f;
	public static final float MAXREADING = 10.0f;
	
	private Bitmap mBackground;
	private Paint mBackgroundPaint;
	
	private RectF mInnerCircleRect;
	private RectF mOuterCircleRect;
	
	private float xVal;
	private float yVal;
	private float zVal;
	
	private int rimColor = Color.rgb(241,49,0);
	private int bubbleColor = Color.rgb(241,153,24);
	
	float zScale;
	float xPos;
	float yPos;
	
	public AccelerometerSensorView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		//readAttrs(context, attrs, defStyle);
		init();
	}
	
	public AccelerometerSensorView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccelerometerSensorView(final Context context) {
		this(context, null, 0);
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		drawBackground(canvas);
		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
				, (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);
		
		drawBubble(canvas);	
	}
	
	private void drawBubble(Canvas canvas){
		RectF mBubbleRect = new RectF(xPos-.05f,yPos-.05f,xPos+.05f,yPos+.05f);
		canvas.drawOval(mBubbleRect,getBubblePaint());
	}
	
	private Paint getBubblePaint(){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(bubbleColor);
		return paint;
	}
	
	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
			drawAccelerometerBackground();
	}
	
	private void drawAccelerometerBackground() {
		if (null != mBackground) {
			mBackground.recycle();
		}
		
		// Create a new background according to the new width and height
		mBackground = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(mBackground);
		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth()-scale) /2)/scale : 0 
				,(scale == getWidth()) ? ((getHeight()-scale) /2 )/scale: 0);
		
		
		canvas.drawOval(mOuterCircleRect,getOuterCirclePaint());
		canvas.drawOval(mInnerCircleRect,getInnerCirclePaint());
		
	}
	
	private Paint getOuterCirclePaint(){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(rimColor);
		paint.setStyle(Paint.Style.FILL);
		return paint;
	}
	
	private Paint getInnerCirclePaint(){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(100,50,50));
		paint.setStyle(Paint.Style.FILL);
		return paint;
	}

	@TargetApi(11)
	public void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		initDrawingRects();
	}
	
	public void initDrawingRects() {
		mOuterCircleRect = new RectF(LEFT+.15f,TOP+.15f,RIGHT-.15f,BOTTOM-.15f);
		mInnerCircleRect = new RectF(mOuterCircleRect.left + .02f, mOuterCircleRect.top + .02f, mOuterCircleRect.right - .02f, mOuterCircleRect.bottom - .02f);
		
	}
	
	public void initDrawingTools(){
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
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		final Bundle state = new Bundle();
		state.putParcelable("superState", superState);
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
	
	public void updatePosition(){
		// Values -1 to 1
		zScale = (Math.abs(zVal-G)<MAXREADING) ? (zVal-G)/MAXREADING:(int)Math.signum(zVal);
		xPos = (Math.abs(xVal)<MAXREADING) ? (xVal)/MAXREADING:(int)Math.signum(xVal);
		yPos = (Math.abs(yVal)<MAXREADING) ? (yVal)/MAXREADING:(int)Math.signum(yVal);
		// scale from [-1 ,1] to [-0.4, 0.4]
		xPos = 0.5f+xPos*0.35f;
		yPos = 0.5f+yPos*0.35f;
		// normalize position, if greater than inner radius
		
		if(((xPos-0.5f)*(xPos-0.5f)+(yPos-0.5f)*(yPos-0.5f))>0.16f){
			xPos /=Math.sqrt(((xPos-0.5f)*(xPos-0.5f)+(yPos-0.5f)*(yPos-0.5f)));
			yPos /=((xPos-0.5f)*(xPos-0.5f)+(yPos-0.5f)*(yPos-0.5f));
					
		}
		
		//
		zScale = 2;
	}
	
	public void setAccelerometerValues(float [] values){
		/*
		if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			xVal = -values[1];
			yVal = -values[0];
		} else{
			xVal = values[0];
			yVal = -values[1];
		}
		*/
		xVal = values[0];
		yVal = -values[1];
		zVal = values[2];
		updatePosition();
		invalidate();
	}
}