package ch.ethz.coss.nervousnet.hub.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class GyroscopeSensorView extends View {

    public static final int SIZE = 300;
    public static final float TOP = 0.0f;
    public static final float LEFT = 0.0f;
    public static final float RIGHT = 1.0f;
    public static final float BOTTOM = 1.0f;
    public static final float CENTER = 0.5f;
    //public static final float EPSILON = 0.000000001f;
    RectF mXOuterRect;
    RectF mXInnerRect;
    RectF mYZOuterRect;
    RectF mYZInnerRect;
    RectF mYZRotatingDiscRect;

    // Create a constant to convert nanoseconds to seconds.
    //private static final float NS2S = 1.0f / 1000000000.0f;
    //private final float[] deltaRotationVector = new float[4];
    //private float[] rotationMatrixCurrent = new float[9];
    RectF mXBarRect;
    RectF mYBarRect;
    RectF mZBarRect;
    float xVal = .3f;
    float yVal = .5f;
    float zVal = -.5f;
    long timestamp;
    private Bitmap mBackground;
    private Paint mBackgroundPaint;
    private float[] angles = new float[3];
    //light Color.rgb(241,153,24)
    //standard Color.rgb(241,95,24)
    // dark Color.rgb(241,49,0)
    private int barColor = Color.rgb(241, 95, 24);
    private int barTopCircleColor = Color.rgb(241, 49, 0);

    public GyroscopeSensorView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GyroscopeSensorView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GyroscopeSensorView(final Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        drawBackground(canvas);


        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

        // reinitialize rectangles;
        initDrawingRects();
        drawMovables(canvas);

    }

    private void drawMovables(Canvas canvas) {

        drawXBar(canvas);
        drawYBar(canvas);
        drawZBar(canvas);

    }

    private void drawXBar(Canvas canvas) {

        canvas.drawRect(mXBarRect, getBarPaint());

        RectF oval = new RectF(mXBarRect.left, mXBarRect.top + .02f, mXBarRect.right, mXBarRect.top - .02f);
        canvas.drawOval(oval, getBarTopCirclePaint());

        oval = new RectF(mXBarRect.left, mXBarRect.bottom + .02f, mXBarRect.right, mXBarRect.bottom - .02f);
        canvas.drawOval(oval, getBarPaint());
    }

    private void drawYBar(Canvas canvas) {

        canvas.drawRect(mYBarRect, getBarPaint());

        RectF oval = new RectF(mYBarRect.left, mYBarRect.top + .02f, mYBarRect.right, mYBarRect.top - .02f);
        canvas.drawOval(oval, getBarTopCirclePaint());

        oval = new RectF(mYBarRect.left, mYBarRect.bottom + .02f, mYBarRect.right, mYBarRect.bottom - .02f);
        canvas.drawOval(oval, getBarPaint());
    }

    private void drawZBar(Canvas canvas) {

        canvas.drawRect(mZBarRect, getBarPaint());

        RectF oval = new RectF(mZBarRect.left, mZBarRect.top + .02f, mZBarRect.right, mZBarRect.top - .02f);
        canvas.drawOval(oval, getBarTopCirclePaint());

        oval = new RectF(mZBarRect.left, mZBarRect.bottom + .02f, mZBarRect.right, mZBarRect.bottom - .02f);
        canvas.drawOval(oval, getBarPaint());
    }

    private Paint getBarPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(barColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getBarTopCirclePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(barTopCircleColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;

    }


    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        drawGyroBackground();
    }

    private void drawGyroBackground() {
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
        drawAxisLine(canvas);
    }

    private void drawAxisLine(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // X Bar
        paint.setColor(Color.rgb(170, 170, 170));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(.03f);
        canvas.drawLine((float) (1 / 16), CENTER, (float) (1. - 1 / 16), CENTER, paint);
    }

    @TargetApi(11)
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        initDrawingRects();
    }

    public void initDrawingRects() {
        // create rectangles for drawing bars:
        /* [3./16., 5./16.], [7./16., 9./16.], [7./16., 9./16.] */
        mXBarRect = new RectF((float) (2.5 / 16.), CENTER - xVal, (float) (5.5 / 16.), CENTER);
        mYBarRect = new RectF((float) (6.5 / 16.), CENTER - yVal, (float) (9.5 / 16.), CENTER);
        mZBarRect = new RectF((float) (10.5 / 16.), CENTER - zVal, (float) (13.5 / 16.), CENTER);

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

        //mChargingState = bundle.getBoolean("mChargingState");
        xVal = bundle.getFloat("xVal");
        yVal = bundle.getFloat("yVal");
        zVal = bundle.getFloat("zVal");

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putFloat("xVal", xVal);
        state.putFloat("yVal", yVal);
        state.putFloat("zVal", zVal);
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

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void setGyroValues(float[] values) {
        xVal = Math.abs(values[0]) > 5 ? 5 * Math.signum(values[0]) : values[0];
        yVal = Math.abs(values[1]) > 5 ? 5 * Math.signum(values[1]) : values[1];
        zVal = Math.abs(values[2]) > 5 ? 5 * Math.signum(values[2]) : values[2];

        //scale to [-.5, +.5]
        xVal /= 10;
        yVal /= 10;
        zVal /= 10;
        invalidate();


    }
}