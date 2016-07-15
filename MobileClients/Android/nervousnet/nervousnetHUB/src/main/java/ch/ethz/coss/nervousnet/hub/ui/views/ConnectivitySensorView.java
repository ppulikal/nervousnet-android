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

public class ConnectivitySensorView extends View {

    public static final int SIZE = 300;
    public static final float TOP = 0.0f;
    public static final float LEFT = 0.0f;
    public static final float RIGHT = 1.0f;
    public static final float BOTTOM = 1.0f;
    public static final float CENTER = 0.5f;

    private Bitmap mBackground;
    private Paint mBackgroundPaint;

    private RectF mRoamingRect;
    private RectF mDataRect;
    private RectF mWifiRect;

    private boolean wifiConnected;
    private boolean dataConnected;
    private boolean roaming;

    public ConnectivitySensorView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        //readAttrs(context, attrs, defStyle);
        init();
    }

    public ConnectivitySensorView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnectivitySensorView(final Context context) {
        this(context, null, 0);
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        drawBackground(canvas);

        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);

        drawWifi(canvas);

        drawData(canvas);

        drawRoaming(canvas);
    }

    private void drawWifi(Canvas canvas) {

        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(1 / scale, 1 / scale);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(scale * 0.025f);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Path path = new Path();
        path.moveTo(CENTER, CENTER);
        RectF nR = new RectF(mWifiRect.left * scale, mWifiRect.top * scale, mWifiRect.right * scale, mWifiRect.bottom * scale);

        if (wifiConnected) {
            paint.setColor(Color.rgb(255, 255, 255));
        } else {
            paint.setColor(Color.rgb(100, 100, 100));
        }

        for (int i = 1; i <= 3; i++) {
            nR = new RectF(mWifiRect.left * scale + i * .05f * scale, mWifiRect.top * scale + i * .05f * scale, mWifiRect.right * scale - i * .05f * scale, mWifiRect.bottom * scale - i * .05f * scale);
            //nR = new RectF(mWifiRect.left*scale+i*.05f*scale, mWifiRect.top*scale+i*.1f*scale, mWifiRect.right*scale-i*.05f*scale, mWifiRect.bottom*scale);
            path.addArc(nR, -45.f, -90.f);
        }
        //path.close();
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        nR.inset(.14f * scale, .14f * scale);
        nR.offset(0.f, -0.02f * scale);
        canvas.drawOval(nR, paint);
        canvas.scale(scale, scale);
    }

    private void drawData(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        if (dataConnected) {
            paint.setColor(Color.rgb(250, 250, 250));
        } else {
            paint.setColor(Color.rgb(100, 100, 100));
        }


        float s = 1000.f;
        // rescaling had to be done due to scaling error
        canvas.scale(1.f / s, 1.f / s);
        RectF nR = new RectF(mDataRect.left * s, mDataRect.top * s, mDataRect.right * s, mDataRect.bottom * s);
        nR.inset(110, 110);
        nR.offset(-100, -90);
        canvas.drawOval(nR, paint);
        canvas.scale(s, s);

        // arrows:
        Path path = new Path();
        path.moveTo(mDataRect.left, mDataRect.bottom);
        path.rMoveTo(.1f, -.08f);
        path.rLineTo(.02f, .0f);
        path.rLineTo(.0f, -.1f);
        path.rLineTo(.04f, .0f);
        path.rLineTo(-.05f, -.04f);
        path.rLineTo(-.05f, .04f);
        path.rLineTo(.04f, .0f);
        path.offset(.0f, -.2f);
        //path.rLineTo(.05f,.0f);
        path.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawPath(path, paint);

        canvas.rotate(180, mDataRect.centerX(), mDataRect.centerY());
        canvas.translate(.2f, 0.2f);
        canvas.drawPath(path, paint);
        canvas.translate(-.2f, -0.2f);
        canvas.rotate(-180, mDataRect.centerX(), mDataRect.centerY());


    }

    private void drawRoaming(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        if (roaming) {
            paint.setColor(Color.rgb(250, 250, 250));
        } else {
            paint.setColor(Color.rgb(100, 100, 100));
        }

        float s = 1000.f;
        // rescaling had to be done due to scaling error
        canvas.scale(1.f / s, 1.f / s);
        RectF nR = new RectF(mRoamingRect.left * s, mRoamingRect.top * s, mRoamingRect.right * s, mRoamingRect.bottom * s);
        nR.inset(150, 150);
        nR.offset(-200, -250);
        canvas.drawOval(nR, paint);
        nR.offset(60, 0);
        canvas.drawArc(nR, 100, 160, false, paint);
        nR.offset(-60, 0);
        canvas.drawLine(nR.left + 31.5f, nR.top + 31.5f, nR.right - 31.5f, nR.top + 31.5f, paint);
        canvas.drawLine(nR.left, nR.centerY(), nR.right, nR.centerY(), paint);
        canvas.drawLine(nR.left + 31.5f, nR.bottom - 31.5f, nR.right - 31.5f, nR.bottom - 31.5f, paint);

        // Expand rectangle To draw outer border of cell phone
        nR.inset(-50, -50);
        nR.offset(30, 30);
        paint.setColor(Color.rgb(80, 80, 80));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(nR.centerX(), nR.top, nR.right + 30, nR.bottom), 20f, 20f, paint);
        paint.setStyle(Paint.Style.FILL);
        if (roaming) {
            paint.setColor(Color.rgb(250, 250, 250));
        } else {
            paint.setColor(Color.rgb(100, 100, 100));
        }
        canvas.drawRoundRect(new RectF(nR.centerX(), nR.top, nR.right + 30, nR.bottom), 20f, 20f, paint);
        nR.inset(60, 50);
        nR.offset(20, -25);

        // screen of phone
        paint.setColor(Color.rgb(80, 80, 80));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(new RectF(nR.centerX(), nR.top, nR.right + 50, nR.bottom), paint);
        paint.setStyle(Paint.Style.FILL);
        if (roaming) {
            paint.setColor(Color.rgb(250, 250, 250));
        } else {
            paint.setColor(Color.rgb(100, 100, 100));
        }
        canvas.drawRect(new RectF(nR.centerX(), nR.top, nR.right + 50, nR.bottom), paint);
        paint.setColor(Color.rgb(80, 80, 80));
        canvas.drawOval(new RectF(nR.right - 30, nR.bottom + 25, nR.right - 10, nR.bottom + 45), paint);
        canvas.scale(s, s);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        drawBatteryBackground();
    }

    private void drawBatteryBackground() {
        if (null != mBackground) {
            mBackground.recycle();
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
        mWifiRect = new RectF(LEFT, TOP, CENTER, CENTER);
        mDataRect = new RectF(CENTER, TOP, RIGHT, CENTER);
        mRoamingRect = new RectF(CENTER - .25f, CENTER, CENTER + .25f, BOTTOM);
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
        wifiConnected = bundle.getBoolean("wifiConnected");
        dataConnected = bundle.getBoolean("dataConnected");
        roaming = bundle.getBoolean("roaming");

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        final Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        //state.putBoolean("mChargingState", mChargingState);

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

    public void setConnectivityValues(boolean wifiConnected, boolean dataConnected, boolean roaming) {
        this.wifiConnected = wifiConnected;
        this.dataConnected = dataConnected;
        this.roaming = roaming;
        invalidate();
    }
}