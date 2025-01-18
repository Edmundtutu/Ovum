package com.pac.ovum.utils.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;

public class CircularPeriodProgressView extends View {
    // Color constants from first version
    private static final int PERIOD_RED = Color.parseColor("#FF4B55");
    private static final int LIGHT_RED = Color.parseColor("#FF8087");
    private static final int SAFE_GREEN = Color.parseColor("#4CAF50");
    private static final int LIGHT_GREEN = Color.parseColor("#81C784");
    private static final int OVULATION_BLUE = Color.parseColor("#2196F3");
    private static final int LIGHT_BLUE = Color.parseColor("#64B5F6");

    // Properties from both versions
    private Paint progressPaint;
    private Paint backgroundPaint;
    private Paint dotPaint;
    private Paint textPaint;
    private Paint outerCirclePaint;
    private Paint innerDashedCirclePaint;
    private RectF arcRect;
    private RectF imageRect;
    private int progress = 0;
    private int maxProgress = 28;
    private int daysUntilPeriod = 0;
    private int daysUntilFertile = 0;
    private float strokeWidth;
    private PathEffect dashEffect;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;
    private Drawable centerImage;
    private float imageSize = 0.4f;
    private ObjectAnimator pulseAnimator;
    private boolean isPulsing = false;
    private float centerImageAlpha = 1.0f;

    // Constructors
    public CircularPeriodProgressView(Context context) {
        super(context);
        init(null);
    }

    public CircularPeriodProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        strokeWidth = dpToPx(30);
        arcRect = new RectF();
        imageRect = new RectF();
        rotateMatrix = new Matrix();
        dashEffect = new DashPathEffect(new float[]{dpToPx(3), dpToPx(4)}, 0);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setAlpha(77);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(spToPx(14));
        textPaint.setTextAlign(Paint.Align.CENTER);

        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(dpToPx(4));
        outerCirclePaint.setColor(Color.parseColor("#FFC0CB"));

        innerDashedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerDashedCirclePaint.setStyle(Paint.Style.STROKE);
        innerDashedCirclePaint.setStrokeWidth(dpToPx(4));
        innerDashedCirclePaint.setColor(Color.parseColor("#FFE4C4"));
        innerDashedCirclePaint.setPathEffect(dashEffect);
    }

    private void initPulseAnimator() {
        pulseAnimator = ObjectAnimator.ofFloat(this, "centerImageAlpha", 0.2f, 1.0f, 0.2f);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);
    }

    // Method from first version for calculating gradient positions
    private float[] calculateGradientPositions(int cycleLength) {
        float periodLength = 5f / cycleLength;
        float follicularStart = periodLength;
        float ovulationStart = 14f / cycleLength;
        float ovulationEnd = (14f + 2f) / cycleLength;
        float lutealStart = ovulationEnd;
        float preMenstrualStart = (cycleLength - 5f) / cycleLength;

        return new float[] {
                0f,
                periodLength,
                follicularStart + 0.05f,
                ovulationStart - 0.05f,
                ovulationStart,
                ovulationEnd,
                lutealStart + 0.05f,
                preMenstrualStart,
                1f
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float contentWidth = w - getPaddingLeft() - getPaddingRight();
        float contentHeight = h - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;

        float padding = strokeWidth / 2;
        arcRect.set(
                getPaddingLeft() + padding,
                getPaddingTop() + padding,
                w - getPaddingRight() - padding,
                h - getPaddingBottom() - padding
        );

        float imageWidth = contentWidth * imageSize;
        float imageHeight = contentHeight * imageSize;
        imageRect.set(
                centerX - imageWidth / 2,
                centerY - imageHeight / 2,
                centerX + imageWidth / 2,
                centerY + imageHeight / 2
        );

        updateGradient(centerX, centerY);
    }

    private void updateGradient(float centerX, float centerY) {
        float[] positions = calculateGradientPositions(maxProgress);

        int[] colors = {
                PERIOD_RED,
                LIGHT_RED,
                SAFE_GREEN,
                LIGHT_BLUE,
                OVULATION_BLUE,
                LIGHT_BLUE,
                SAFE_GREEN,
                LIGHT_RED,
                PERIOD_RED
        };

        sweepGradient = new SweepGradient(centerX, centerY, colors, positions);
        rotateMatrix.setRotate(270, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        float radius = Math.min(contentWidth, contentHeight) / 2;

        canvas.drawCircle(centerX, centerY, radius * 0.8f, outerCirclePaint);
        canvas.drawCircle(centerX, centerY, radius * 0.7f, innerDashedCirclePaint);

        canvas.drawArc(arcRect, 0, 360, false, backgroundPaint);

        float sweepAngle = (progress / (float) maxProgress) * 360;
        canvas.drawArc(arcRect, -90, sweepAngle, false, progressPaint);

        if (centerImage != null) {
            int alpha = (int) (centerImageAlpha * 255);
            centerImage.setAlpha(alpha);
            centerImage.setBounds(
                    (int) imageRect.left,
                    (int) imageRect.top,
                    (int) imageRect.right,
                    (int) imageRect.bottom
            );
            centerImage.draw(canvas);
        }

        drawDotWithText(canvas, -90, Color.RED, String.valueOf(daysUntilPeriod), Color.WHITE);
        drawDotWithText(canvas, 90, Color.BLUE, String.valueOf(daysUntilFertile), Color.WHITE);

        textPaint.setTextSize(spToPx(18));
        String centerText = progress + " days";
        float textY = centerY + (centerImage != null ? imageRect.height()/2 : 0) + textPaint.getTextSize();
        canvas.drawText(centerText, centerX, textY, textPaint);
    }

    private void drawDotWithText(Canvas canvas, float angle, int dotColor, String text, int textColor) {
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        float radius = Math.min(contentWidth, contentHeight) / 2 - strokeWidth/2;

        double radians = Math.toRadians(angle);
        float dotX = (float) (centerX + radius * Math.cos(radians));
        float dotY = (float) (centerY + radius * Math.sin(radians));

        float dotRadius = strokeWidth/2 * 1.2f;

        dotPaint.setColor(dotColor);
        canvas.drawCircle(dotX, dotY, dotRadius, dotPaint);

        textPaint.setColor(textColor);
        textPaint.setTextSize(spToPx(12));
        canvas.drawText(text, dotX, dotY + textPaint.getTextSize()/3, textPaint);
    }

    // Public methods
    public void setCycleLength(int cycleDays) {
        if (cycleDays > 0) {
            this.maxProgress = cycleDays;
            if (this.progress > cycleDays) {
                this.progress = cycleDays;
            }
            if (getWidth() > 0) {
                updateGradient(getWidth() / 2f, getHeight() / 2f);
            }
            invalidate();
        }
    }

    public void setProgress(int progress) {
        this.progress = Math.min(Math.max(progress, 0), maxProgress);
        invalidate();
    }

    public void setDaysUntilPeriod(int days) {
        this.daysUntilPeriod = days;
        invalidate();
    }

    public void setDaysUntilFertile(int days) {
        this.daysUntilFertile = days;
        invalidate();
    }

    public int getCycleLength() {
        return maxProgress;
    }

    public void setCenterImage(Drawable drawable) {
        this.centerImage = drawable;
        invalidate();
    }

    public void setCenterImageResource(@DrawableRes int resourceId) {
        setCenterImage(ContextCompat.getDrawable(getContext(), resourceId));
    }

    public void setImageSizeRatio(float ratio) {
        this.imageSize = Math.min(Math.max(ratio, 0f), 1f);
        invalidate();
    }

    @Keep
    public void setCenterImageAlpha(float alpha) {
        this.centerImageAlpha = alpha;
        invalidate();
    }

    @Keep
    public float getCenterImageAlpha() {
        return centerImageAlpha;
    }

    public void startPulseEffect(long duration) {
        if (centerImage != null && !isPulsing) {
            if (pulseAnimator == null) {
                initPulseAnimator();
            }
            pulseAnimator.setDuration(duration);
            pulseAnimator.start();
            isPulsing = true;
        }
    }

    public void stopPulseEffect() {
        if (pulseAnimator != null && isPulsing) {
            pulseAnimator.cancel();
            centerImageAlpha = 1.0f;
            invalidate();
            isPulsing = false;
        }
    }

    public void configurePulseEffect(long daysLeftCount) {
        if (daysLeftCount < 1) {
            startPulseEffect(500);
        } else if (daysLeftCount <= 5) {
            startPulseEffect(1000);
        } else {
            stopPulseEffect();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPulseEffect();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}