package com.pac.ovum.utils.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class CircularPeriodProgressView extends View {
    private Paint progressPaint;
    private Paint backgroundPaint;
    private Paint dotPaint;
    private Paint textPaint;
    private Paint outerCirclePaint;
    private Paint innerDashedCirclePaint;
    private RectF arcRect;
    private int progress = 0;
    private int maxProgress = 28;
    private int daysUntilPeriod = 0;
    private int daysUntilFertile = 0;
    private float strokeWidth;
    private PathEffect dashEffect;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    public CircularPeriodProgressView(Context context) {
        super(context);
        init(null);
    }

    public CircularPeriodProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        strokeWidth = dpToPx(30); // Updated to match the thicker stroke
        arcRect = new RectF();
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
        outerCirclePaint.setStrokeWidth(dpToPx(4)); // Updated to 8f
        outerCirclePaint.setColor(Color.parseColor("#FFC0CB")); // pale pink

        innerDashedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerDashedCirclePaint.setStyle(Paint.Style.STROKE);
        innerDashedCirclePaint.setStrokeWidth(dpToPx(4)); // Updated to 8f
        innerDashedCirclePaint.setColor(Color.parseColor("#FFE4C4")); // pale cream
        innerDashedCirclePaint.setPathEffect(dashEffect);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        // Keep the view squared
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding in calculations
        float contentWidth = w - getPaddingLeft() - getPaddingRight();
        float contentHeight = h - getPaddingTop() - getPaddingBottom();

        float padding = strokeWidth / 2;
        arcRect.set(
                getPaddingLeft() + padding,
                getPaddingTop() + padding,
                w - getPaddingRight() - padding,
                h - getPaddingBottom() - padding
        );
        // Updated gradient colors to match new design
        // Update gradient
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        int[] colors = {
                Color.parseColor("#4B9BF7"), // Blue
                Color.BLUE,                  // Blue
                Color.parseColor("#4B9BF7"), // Blue
                Color.CYAN,                  // Cyan
                Color.parseColor("#CCFDD4"), // Light green
                Color.RED,                   // Red
                Color.parseColor("#82FA96"), // Light green
                Color.parseColor("#4B9BF7")  // Blue
        };
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
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

        // Draw outer circles with padding
        canvas.drawCircle(centerX, centerY, radius * 0.8f, outerCirclePaint);
        canvas.drawCircle(centerX, centerY, radius * 0.7f, innerDashedCirclePaint);

        // Draw background arc
        canvas.drawArc(arcRect, 0, 360, false, backgroundPaint);

        // Draw progress arc
        float sweepAngle = (progress / (float) maxProgress) * 360;
        canvas.drawArc(arcRect, -90, sweepAngle, false, progressPaint);

        // Draw dots with text
        drawDotWithText(canvas, -90, Color.RED, String.valueOf(daysUntilPeriod), Color.WHITE);
        drawDotWithText(canvas, 90, Color.BLUE, String.valueOf(daysUntilFertile), Color.WHITE);

        // Draw center text
        textPaint.setTextSize(spToPx(18));
        String centerText = progress + " days";
        canvas.drawText(centerText, centerX, centerY + textPaint.getTextSize()/3, textPaint);
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

    // Add new method to set cycle length
    public void setCycleLength(int cycleDays) {
        if (cycleDays > 0) {  // Validate input
            this.maxProgress = cycleDays;
            // Ensure current progress doesn't exceed new max
            if (this.progress > cycleDays) {
                this.progress = cycleDays;
            }
            invalidate(); // Redraw with new values
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

    // Add getter for current cycle length
    public int getCycleLength() {
        return maxProgress;
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}