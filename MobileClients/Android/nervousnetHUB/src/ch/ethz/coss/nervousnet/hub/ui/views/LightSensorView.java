package ch.ethz.coss.nervousnet.hub.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
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

public class LightSensorView extends View {
	
	public static final int SIZE = 300;
	public static final float TOP = 0.0f;
	public static final float LEFT = 0.0f;
	public static final float RIGHT = 1.0f;
	public static final float BOTTOM = 1.0f;
	public static final float CENTER = 0.5f;
	public static final float EPSILON = 0.000000001f;
	/*
	public static final int[] OUTER_SHADOW_COLORS = {Color.rgb(150,150,150), Color.rgb(50,50,50),Color.rgb(50,50,50), 
		Color.rgb(150,150,150),Color.rgb(50,50,50),Color.rgb(150,150,150)};
	public static final float[] OUTER_SHADOW_POS ={.1f,.2f,.3f,.5f,.7f,.9f};
	*/
	public static final int[] OUTER_GLOW_COLORS = { Color.argb(255,255, 255, 0), Color.argb(150,255, 255, 0),
		Color.argb(0, 255, 255, 0) };
	public static final float[] OUTER_GLOW_POS = { 0.1f, 0.5f, 0.99f };
	public static final int[] CANDLE_GRADIENT_COLORS = { Color.argb(255,255, 255, 255), Color.argb(255,255, 255, 255),
		Color.argb(0, 255, 255, 255) };
	public static final float[] CANDLE_GRADIENT_POS = { 0.1f, 0.5f, 0.99f };
	
	private int candleColor = Color.rgb(241,49,0);
	
	private Bitmap mBackground;
	private Paint mBackgroundPaint;
	
	private RectF candleRect;
	
	private float mLux ;
	
	public LightSensorView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		//readAttrs(context, attrs, defStyle);
		init();
	}
	
	public LightSensorView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LightSensorView(final Context context) {
		this(context, null, 0);
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		drawBackground(canvas);
		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
				, (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);
		
		drawFlame(canvas);

	}
	
	private void drawFlame(Canvas canvas){
		// intensity factor
		float iF = mLux;
		iF = iF*1.5f;
		float iF2 = iF*.5f;
		// outer glow;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setShader(new RadialGradient( CENTER,CENTER+.37f-.2f*iF, .2f*iF+EPSILON, OUTER_GLOW_COLORS, OUTER_GLOW_POS, TileMode.MIRROR));
		
		//paint.setColor(Color.rgb(0,250,250));
		canvas.drawOval(new RectF(CENTER-.2f*iF,CENTER+.37f-.2f*2*iF,CENTER+.2f*iF, CENTER+.35f), paint);
		
		// outer flame
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Path path = new Path();
		path.moveTo(CENTER, CENTER+.35f);
		paint.setColor(Color.rgb(250,250,0));
		path.rCubicTo(-.2f*iF,-.15f*iF,.0f,-.25f*iF,0.f,-.4f*iF) ;
		path.moveTo(CENTER,CENTER+.35f);
		path.rCubicTo(.2f*iF,-.15f*iF,.0f,-.25f*iF,0.f,-.4f*iF) ;
		path.close();
		canvas.drawPath(path,paint);
		
		// inner
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		path = new Path();
		path.moveTo(CENTER, CENTER+.35f);
		paint.setColor(Color.rgb(200,200,0));
		path.rCubicTo(-.2f*iF2,-.15f*iF2,.0f,-.25f*iF2,0.f,-.4f*iF2) ;
		path.moveTo(CENTER,CENTER+.35f);
		path.rCubicTo(.2f*iF2,-.15f*iF2,.0f,-.25f*iF2,0.f,-.4f*iF2) ;
		path.close();
		canvas.drawPath(path,paint);
		
	}
	
	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
			drawCandleBackground();
	}
	
	private void drawCandleBackground() {
		if (null != mBackground) {
			mBackground.recycle();
		}
		
		mBackground = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(mBackground);
		final float scale = Math.min(getWidth(), getHeight());
		canvas.scale(scale, scale);
		canvas.translate((scale == getHeight()) ? ((getWidth()-scale) /2)/scale : 0 
				,(scale == getWidth()) ? ((getHeight()-scale) /2 )/scale: 0);
		
		// candle rectangle
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(candleColor);
		//paint.setShader(new LinearGradient(CENTER-0.1f, CENTER, CENTER-0.1f, CENTER+0.25f, CANDLE_GRADIENT_COLORS, CANDLE_GRADIENT_POS, TileMode.REPEAT));
		canvas.drawRect(CENTER-0.1f,CENTER+.35f,CENTER+0.1f, CENTER+.45f, paint);
		canvas.drawOval(new RectF(CENTER-0.1f,CENTER+.4f,CENTER+0.1f, CENTER+0.49f), paint);
		
		// candle upper side
		/*
		paint.setStyle(Paint.Style.FILL);
		paint.setShader(null);
		paint.setColor(Color.rgb(250, 250, 250));
		//canvas.drawOval(new RectF(CENTER-0.1f,CENTER-.045f,CENTER+0.1f, CENTER+0.04f), paint);
		
		// candle upper side rim
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.rgb(150, 150, 150));
		//canvas.drawOval(new RectF(CENTER-0.1f,CENTER-.045f,CENTER+0.1f, CENTER+0.04f), paint);
		*/
		
	}

	@TargetApi(11)
	public void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		initDrawingRects();
	}
	
	public void initDrawingRects() {
		candleRect = new RectF(CENTER-.1f,CENTER,CENTER+.1f,CENTER+.3f);
		
	}
	
	public void initDrawingTools(){
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setFilterBitmap(true);
	}
	
	private void drawBackground(final Canvas canvas) {
        if (null != mBackground) {
				canvas.drawBitmap(mBackground, 0, 0, mBackgroundPaint);
			}
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setStyle(Paint.Style.FILL);
        RectF candle = new RectF(candleRect.left, candleRect.top, candleRect.right, candleRect.bottom);
        canvas.drawRect(candle, paint);
	}
	
	
	
	@Override
	protected void onRestoreInstanceState(final Parcelable state) {
		final Bundle bundle = (Bundle) state;
		final Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);
		//mLux = bundle.getFloat("mLux");
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();

		final Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		//state.putFloat("mLux", mLux);
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
	
	public void setLux(float lux){
		this.mLux = lux/300 >= 1.? 1.f: lux/300;
		invalidate();
	}
	
}