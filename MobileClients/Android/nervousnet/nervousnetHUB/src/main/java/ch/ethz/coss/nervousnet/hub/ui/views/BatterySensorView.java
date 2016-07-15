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

public class BatterySensorView extends View {

    public static final int SIZE = 300;
    public static final float TOP = 0.0f;
    public static final float LEFT = 0.0f;
    public static final float RIGHT = 1.0f;
    public static final float BOTTOM = 1.0f;
    public static final float CENTER = 0.5f;

    /*
    public static final int[] BACKGROUND_GRADIENT_COLORS = {Color.rgb(150,150,150), Color.rgb(50,50,50),Color.rgb(50,50,50),
        Color.rgb(150,150,150),Color.rgb(50,50,50),Color.rgb(150,150,150)};
    public static final float[] BACKGROUND_GRADIENT_POSITIONS ={.1f,.2f,.3f,.5f,.7f,.9f};
    */
    private float mCurrentBatteryLevel;
    private float mTargetBatteryLevel;
    private boolean mChargingState = true;
    private boolean mAC = false;
    private boolean mUSB = false;


    private RectF mBatteryBackgroundRectInner;
    private RectF mBatteryBackgroundRectOuter;
    private RectF mBatteryLevelRect;
    private RectF mChargingZRect;
    private RectF mBatteryCapRect;
    private RectF mCableRect;

    private int cableColor = Color.rgb(241, 49, 0);
    private int batteryBackgroundColor = Color.rgb(241, 49, 0);
    private int batteryRimColor = Color.rgb(241, 153, 24);
    private int batteryLevelColor = Color.rgb(250, 250, 250);

    private Bitmap mBackground;
    private Paint mBackgroundPaint;
    //private Paint mBatteryBackgroundPaint;
    //private Paint mbatteryLevelPaint;

    public BatterySensorView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        //readAttrs(context, attrs, defStyle);
        init();
    }

    public BatterySensorView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatterySensorView(final Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        drawBackground(canvas);


        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

        mBatteryLevelRect = new RectF(LEFT + .36f, TOP + .31f, LEFT + .36f + .53f * mTargetBatteryLevel / 100, .69f);
        drawBatteryLevel(canvas);

        if (mChargingState) {
            drawCharger(canvas);
        }
        if (mUSB) {
            drawUSB(canvas);
        }
        if (mAC) {
            drawAC(canvas);
        }
    }

    private void drawAC(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // center coordinates
        mCableRect.offset(.1f, -.1f);
        float cX = (mCableRect.left + mCableRect.right) / 2;
        float cY = (mCableRect.top + mCableRect.bottom) / 2;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(cableColor);
        canvas.drawRoundRect(new RectF(cX - .05f, cY - 0.15f, cX - .02f, cY - 0.06f), .02f, .02f, paint);
        canvas.drawRoundRect(new RectF(cX + .02f, cY - 0.15f, cX + .05f, cY - 0.06f), .02f, .02f, paint);
        canvas.drawRect(new RectF(cX - .05f, cY - 0.1f, cX - .02f, cY - 0.06f), paint);
        canvas.drawRect(new RectF(cX + .02f, cY - 0.1f, cX + .05f, cY - 0.06f), paint);

        //canvas.drawRect(new RectF(cX-.05f, cY-0.15f, cX+.05f, cY-0.06f), paint);
        canvas.drawRoundRect(new RectF(cX - 0.09f, cY - 0.05f, cX + .09f, cY - .01f), .02f, .02f, paint);

        //canvas.drawRoundRect(new RectF(cX-0.08f, cY-0.04f, cX+.08f, cY+.1f), .02f,.02f,paint);
        canvas.drawRoundRect(new RectF(cX - .065f, cY - 0.05f, cX + .065f, cY + .09f), 0.04f, 0.04f, paint);
        canvas.drawRect(new RectF(cX - .015f, cY + 0.09f, cX + .015f, cY + .25f), paint);
        mCableRect.offset(-.1f, .1f);
    }

    private void drawUSB(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // center coordinates
        mCableRect.offset(.1f, -.1f);
        float cX = (mCableRect.left + mCableRect.right) / 2;
        float cY = (mCableRect.top + mCableRect.bottom) / 2;

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(cableColor);
        canvas.drawRect(new RectF(cX - .05f, cY - 0.15f, cX + .05f, cY - 0.06f), paint);
        canvas.drawRect(new RectF(cX - 0.08f, cY - 0.05f, cX + .08f, cY + .04f), paint);
        canvas.drawRoundRect(new RectF(cX - 0.08f, cY - 0.04f, cX + .08f, cY + .1f), .02f, .02f, paint);
        canvas.drawRect(new RectF(cX - .05f, cY + 0.09f, cX + .05f, cY + .13f), paint);
        canvas.drawRect(new RectF(cX - .02f, cY + 0.13f, cX + .02f, cY + .25f), paint);

        paint.setColor(Color.rgb(50, 50, 50));
        canvas.drawRect(new RectF(cX - 0.03f, cY - 0.13f, cX - 0.01f, cY - 0.1f), paint);
        canvas.drawRect(new RectF(cX + 0.01f, cY - 0.13f, cX + 0.03f, cY - 0.1f), paint);
        mCableRect.offset(-.1f, .1f);
    }

    private void drawBatteryLevel(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawRoundRect(mBatteryLevelRect, .03f, .03f, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(batteryLevelColor);
        canvas.drawRoundRect(mBatteryLevelRect, .02f, .02f, paint);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        drawBatteryBackground();
    }

    private void drawBatteryBackground() {
        if (null != mBackground) {
            mBackground.recycle();
        }
        // Create a new background according to the new width and height
        mBackground = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(mBackground);
        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(batteryRimColor);
        //paint.setShader(new LinearGradient(CENTER,CENTER-.3f, CENTER, CENTER+.3f, BACKGROUND_GRADIENT_COLORS, BACKGROUND_GRADIENT_POSITIONS, TileMode.MIRROR));

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(.01f);
        paint.setColor(batteryRimColor);
        canvas.drawRoundRect(mBatteryBackgroundRectOuter, .03f, .03f, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(batteryBackgroundColor);
        canvas.drawRoundRect(mBatteryBackgroundRectInner, .03f, .03f, paint);
        paint.setColor(batteryRimColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mBatteryCapRect, .01f, .01f, paint);
    }

    @TargetApi(11)
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        initDrawingRects();
    }

    //////////////////////////////////////////////
    public void initDrawingRects() {
        mCableRect = new RectF(LEFT, TOP + .4f, LEFT + .3f, TOP + .7f);
        mBatteryBackgroundRectInner = new RectF(LEFT + .35f, TOP + .3f, .9f, .7f);
        mBatteryBackgroundRectOuter = new RectF(LEFT + .34f, TOP + .29f, .91f, .71f);
        mBatteryCapRect = new RectF(mBatteryBackgroundRectInner.right, CENTER - .1f, mBatteryBackgroundRectInner.right + .05f, CENTER + .1f);
        float cX = (mBatteryBackgroundRectOuter.left + mBatteryBackgroundRectOuter.right) / 2;
        float cY = (mBatteryBackgroundRectOuter.top + mBatteryBackgroundRectOuter.bottom) / 2;
        mChargingZRect = new RectF(cX - .1f, cY - .05f, cX + .1f, cY + .05f);
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

    //
    public void drawCharger(Canvas canvas) {

        canvas.save();
        canvas.rotate(45, mChargingZRect.centerX(), mChargingZRect.centerY());
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(100, 100, 200));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = new Path();
        path.moveTo(mChargingZRect.left + .05f, mChargingZRect.top);
        float cX = (mChargingZRect.left + mChargingZRect.right) / 2;
        float cY = (mChargingZRect.top + mChargingZRect.bottom) / 2;
        path.lineTo(cX - .01f, cY + .02f);
        path.lineTo(mChargingZRect.left - .1f, mChargingZRect.bottom);
        path.lineTo(mChargingZRect.right - 0.05f, mChargingZRect.bottom);
        path.lineTo(cX + .01f, cY - .02f);
        path.lineTo(mChargingZRect.right + .1f, mChargingZRect.top);
        path.close();
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        final Bundle bundle = (Bundle) state;
        final Parcelable superState = bundle.getParcelable("superState");
        super.onRestoreInstanceState(superState);

        mChargingState = bundle.getBoolean("mChargingState");
        mCurrentBatteryLevel = bundle.getFloat("mCurrentBatteryLevel");
        mTargetBatteryLevel = bundle.getFloat("mTargetBatteryLevel");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        final Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putBoolean("mChargingState", mChargingState);
        state.putFloat("mTargetBatteryLevel", mTargetBatteryLevel);
        state.putFloat("mCurrentBatteryLevel", mCurrentBatteryLevel);
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

    public void setBatteryLevel(final float value) {
        mTargetBatteryLevel = value;
        invalidate();
    }

    public void setACCharging(final boolean value) {
        mAC = value;
    }

    public void setUSBCharging(final boolean value) {
        mUSB = value;
    }

    public void setChargingState(final boolean value) {
        mChargingState = value;
        invalidate();
    }
}
