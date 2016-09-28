package com.thedeveloperworldisyours.gears.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.thedeveloperworldisyours.gears.R;

/**
 * Created by javierg on 27/09/2016.
 */

public class GearsProgressView extends View {

    /**
     * Default Diameter size
     */
    private static final int DEFAULT_DIAMETER_SIZE = 120;

    /**
     * Ratio line start X
     */
    private static final float RATIO_LINE_START_X = 5 / 6.f;

    /**
     * Ratio line start Y
     */
    private static final float RATIO_LINE_START_Y = 3 / 4.f;

    /**
     * Ratio line start Y
     */
    private static final float RATIO_LINE_START_Y_ABOVE = 0 / 4.f;

    /**
     * Ratio arc start X
     */
    private static final float RATIO_ARC_START_X = 2 / 5.f;

    /**
     * Hourglass separation angle
     */
    private static final float GEAR_SEPARATION_ANGLE = 45;

    /**
     * Pain Color
     */
    private static final String PAINT_COLOR = "#b72d28";

    /**
     * Background Color
     */
    private static final String BACKGROUND_COLOR = "#cfcfcf";

    /**
     * space hourglass
     */
    private static final float SPACE_GEAR = 12;

    /**
     * Hourglass line length
     */
    private static final float GEAR_LINE_LENGTH = 15;

    /**
     * default offset X
     */
    private static final int DEFAULT_OFFSET_X = 20;

    /**
     * default offset Y
     */
    private static final int DEFAULT_OFFSET_Y = 20;

    /**
     * (mLineStartX, mLineStartY)，mLineLength
     */
    private float mLineStartX, mLineStartY, mLineStartAboveY, mLineLength;

    /**
     * x,y
     */
    private float textX, textY;

    /**
     * Radios
     */
    private float gearRadius;

    /**
     * x,y
     */
    private double gearStartX, gearStartY, gearStopX, gearStopY;

    private float offsetY = DEFAULT_OFFSET_Y, mOffsetSpin, mOffsetSpinHight;

    private Paint mPaint, mStrongPain, mThinPaint, mBackgroundPaint;

    private TextPaint mTextPaint;

    private RectF rectF;


    public GearsProgressView(Context context) {
        this(context, null);
    }

    public GearsProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GearsProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
        initRes();
    }

    private void initRes() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.parseColor(PAINT_COLOR));

        mStrongPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrongPain.setStyle(Paint.Style.STROKE);
        mStrongPain.setStrokeWidth(20);
        mStrongPain.setColor(Color.parseColor(PAINT_COLOR));

        mThinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThinPaint.setStyle(Paint.Style.STROKE);
        mThinPaint.setStrokeWidth(10);
        mThinPaint.setColor(Color.parseColor(PAINT_COLOR));

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.parseColor(PAINT_COLOR));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);
        mBackgroundPaint.setStrokeWidth(1);
        mBackgroundPaint.setColor(Color.parseColor(BACKGROUND_COLOR));

        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;

        Resources r = Resources.getSystem();
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DIAMETER_SIZE, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DIAMETER_SIZE, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final int width = getWidth();
        final int height = getHeight();


        mLineLength = width * RATIO_LINE_START_X;

        mLineStartX = (width - mLineLength) * .5f;
        mLineStartY = height * RATIO_LINE_START_Y;
        mLineStartAboveY = height * RATIO_LINE_START_Y_ABOVE;

        textX = width * .5f;
        textY = mLineStartY + (height - mLineStartY) * .5f + Math.abs(mTextPaint.descent() + mTextPaint.ascent()) * .5f;


        gearRadius = (mLineLength - mLineLength * RATIO_ARC_START_X) * .5f;
        calculateAndSetRectPoint();
        initAnimationDriver();
    }


    /**
     * calculated and set rectangle point
     */
    private void calculateAndSetRectPoint() {
        float rectLeft = mLineStartX + mLineLength * .5f - gearRadius;
        float rectTop = mLineStartY - gearRadius + offsetY;
        float rectRight = mLineLength - rectLeft + 2 * mLineStartX;
        float rectBottom = rectTop + 2 * gearRadius;

        rectF.set(rectLeft, rectTop, rectRight, rectBottom);
    }

    /**
     * Calculated center
     */
    public float calculateCenter() {
        float rectLeft = mLineStartX + mLineLength * .5f - gearRadius;
        float rectRight = mLineLength - rectLeft + 2 * mLineStartX;
        return rectRight - rectLeft;
    }

    /**
     * init animation driver
     */
    private void initAnimationDriver() {

        startSpinAnimation();
        startSpinAnimationHight();

    }

    /**
     * Started spin animation
     */
    private void startSpinAnimation() {
        ValueAnimator spinAnima = ValueAnimator.ofFloat(0, 360);
        spinAnima.setRepeatCount(-1);
        spinAnima.setDuration(24 * 1000);
        spinAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetSpin = Float.parseFloat(animation.getAnimatedValue().toString());
                postInvalidate();
            }
        });
        spinAnima.start();
    }

    /**
     * Started spin animation
     */
    private void startSpinAnimationHight() {
        ValueAnimator spinAnima = ValueAnimator.ofFloat(0, 360);
        spinAnima.setRepeatCount(-1);
        spinAnima.setDuration(24 * 1000);
        spinAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetSpinHight = Float.parseFloat(animation.getAnimatedValue().toString());
                postInvalidate();
            }
        });
        spinAnima.reverse();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Line below
        canvas.drawLine(mLineStartX, mLineStartY, mLineStartX + mLineLength, mLineStartY, mStrongPain);

        //first gear
        canvas.drawCircle(calculateCenter(), mLineStartY, 4 * gearRadius / 3, mStrongPain);
        canvas.drawCircle(calculateCenter(), mLineStartY, gearRadius / 2, mPaint);
        drawRadiusCenter(canvas);
        drawRadius(canvas);

        //Second gear
        canvas.drawCircle(calculateCenter() + DEFAULT_OFFSET_X, mLineStartAboveY, 4 * gearRadius / 3, mStrongPain);
        canvas.drawCircle(calculateCenter() + DEFAULT_OFFSET_X, mLineStartAboveY, gearRadius / 2, mPaint);
        drawRadiusHight(canvas);

        drawUnderLineView(canvas);
    }

    private void drawUnderLineView(Canvas canvas) {
        canvas.save();
        canvas.drawRect(0, mLineStartY + mPaint.getStrokeWidth() * .5f, getWidth(), getHeight(), mBackgroundPaint);
        canvas.drawText(getResources().getString(R.string.app_name), textX, textY, mTextPaint);
        canvas.restore();
    }

    private void drawRadius(Canvas canvas) {
        for (int a = 0; a <= 360; a += GEAR_SEPARATION_ANGLE) {
            gearStartX = Math.cos(Math.toRadians(a + mOffsetSpin)) * (gearRadius - SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + getWidth() * .5f;
            gearStartY = Math.sin(Math.toRadians(a + mOffsetSpin)) * (gearRadius - SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + mLineStartY;

            gearStopX = Math.cos(Math.toRadians(a + mOffsetSpin)) * (gearRadius + SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + getWidth() * .5f;
            gearStopY = Math.sin(Math.toRadians(a + mOffsetSpin)) * (gearRadius + SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + mLineStartY;
            if (gearStartY <= mLineStartY && gearStopY <= mLineStartY) {
                canvas.drawLine((float) gearStartX, (float) gearStartY, (float) gearStopX, (float) gearStopY, mStrongPain);
            }
        }
    }

    private void drawRadiusCenter(Canvas canvas) {
        for (int a = 0; a <= 360; a += GEAR_SEPARATION_ANGLE) {
            gearStartX = calculateCenter();
            gearStartY = mLineStartY;

            gearStopX = Math.cos(Math.toRadians(a + mOffsetSpin)) * (gearRadius / 2) + getWidth() * .5f;
            gearStopY = Math.sin(Math.toRadians(a + mOffsetSpin)) * (gearRadius / 2) + mLineStartY;
            if (gearStartY <= mLineStartY && gearStopY <= mLineStartY) {
                canvas.drawLine((float) gearStartX, (float) gearStartY, (float) gearStopX, (float) gearStopY, mPaint);
            }
        }
    }

    private void drawRadiusHight(Canvas canvas) {
        for (int a = 0; a <= 360; a += GEAR_SEPARATION_ANGLE) {
            gearStartX = Math.cos(Math.toRadians(a + mOffsetSpinHight)) * (gearRadius - SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + getWidth() * .5f;
            gearStartY = Math.sin(Math.toRadians(a + mOffsetSpinHight)) * (gearRadius - SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + mLineStartAboveY;

            gearStopX = Math.cos(Math.toRadians(a + mOffsetSpinHight)) * (gearRadius + SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + getWidth() * .5f;
            gearStopY = Math.sin(Math.toRadians(a + mOffsetSpinHight)) * (gearRadius + SPACE_GEAR + GEAR_LINE_LENGTH + mThinPaint.getStrokeWidth()) + mLineStartAboveY;

            if (gearStartY <= mLineStartY && gearStopY <= mLineStartY) {
                canvas.drawLine((float) gearStartX + DEFAULT_OFFSET_X, (float) gearStartY, (float) gearStopX + DEFAULT_OFFSET_X, (float) gearStopY, mStrongPain);
            }
        }
    }


}
