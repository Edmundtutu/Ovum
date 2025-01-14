package com.pac.ovum.utils.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class CircularPeriodProgressView extends View {

    private Paint progressPaint, backgroundPaint, textPaint, dotPaint;
    private int progress = 0, maxProgress = 28, daysUntilPeriod = 5, daysUntilFertile = 10;

    public CircularPeriodProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize paints for different elements
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(40f);
        progressPaint.setShader(new SweepGradient(0, 0,
                new int[]{Color.BLUE, Color.CYAN, Color.GREEN}, null));

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(40f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - 50;

        // Draw base background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // Draw progress arc
        float sweepAngle = (progress / (float) maxProgress) * 360f;
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                -90, sweepAngle, false, progressPaint);

        // Add period and fertility dots with text
        drawDotsWithText(canvas, centerX, centerY, radius);
    }

    private void drawDotsWithText(Canvas canvas, float centerX, float centerY, float radius) {
        // Example for period dot
        float periodAngleRad = (float) Math.toRadians(-90);
        float periodX = (float) (centerX + radius * Math.cos(periodAngleRad));
        float periodY = (float) (centerY + radius * Math.sin(periodAngleRad));
        canvas.drawCircle(periodX, periodY, 10f, dotPaint);

        // Example to draw text near the dot
        canvas.drawText(String.valueOf(daysUntilPeriod), periodX - 10f, periodY - 20f, textPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
