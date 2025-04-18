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

/**
 * CircularPeriodProgressView is a custom view that visually represents a menstrual cycle
 * with different phases shown as a circular progress indicator.
 *
 * The view displays:
 * - A gradient-colored progress arc representing the current day in the cycle
 * - Days until next period and fertile window
 * - Optional center image with pulse animation for important days
 * - Decorative circles and indicators
 */
public class CircularPeriodProgressView extends View {
    // Color constants used for different phases of the menstrual cycle
    private static final int PERIOD_RED = Color.parseColor("#FF4B55");      // Menstruation phase
    private static final int LIGHT_RED = Color.parseColor("#FF8087");       // Transition phase
    private static final int SAFE_GREEN = Color.parseColor("#4CAF50");      // Low fertility phase
    private static final int LIGHT_GREEN = Color.parseColor("#81C784");     // Transition phase
    private static final int OVULATION_BLUE = Color.parseColor("#2196F3");  // Ovulation phase
    private static final int LIGHT_BLUE = Color.parseColor("#64B5F6");      // Transition phase

    // Paint objects for drawing different components
    private Paint progressPaint;          // For the gradient progress arc
    private Paint backgroundPaint;        // For the background circle
    private Paint dotPaint;               // For indicator dots
    private Paint textPaint;              // For text elements
    private Paint outerCirclePaint;       // For decorative outer circle
    private Paint innerDashedCirclePaint; // For decorative inner dashed circle

    // Rectangle objects that define drawing areas
    private RectF arcRect;                // Rectangle for drawing the progress arc
    private RectF imageRect;              // Rectangle for drawing the center image

    // Progress tracking variables
    private int progress = 0;             // Current day in the cycle (0-based)
    private int maxProgress = 28;         // Total length of cycle in days (default 28)
    private int daysUntilPeriod = 0;      // Days until next period starts
    private int daysUntilFertile = 0;     // Days until fertile window starts

    // Styling properties
    private float strokeWidth;            // Width of the progress arc
    private PathEffect dashEffect;        // Effect for dashed lines
    private SweepGradient sweepGradient;  // Gradient for progress arc
    private Matrix rotateMatrix;          // Matrix to rotate the gradient

    // Center image properties
    private Drawable centerImage;         // Image displayed in center
    private float imageSize = 0.4f;       // Size ratio of image (0.0-1.0)

    // Animation properties
    private ObjectAnimator pulseAnimator; // Animator for pulsing effect
    private boolean isPulsing = false;    // Flag to track animation state
    private float centerImageAlpha = 1.0f; // Current alpha value of center image

    /**
     * Basic constructor when creating view from code
     *
     * @param context The context the view is running in
     */
    public CircularPeriodProgressView(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor used when inflating view from XML
     *
     * @param context The context the view is running in
     * @param attrs The attributes of the XML tag that is inflating the view
     */
    public CircularPeriodProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Initialize all the paint objects and properties
     *
     * @param attrs Attributes passed from XML, currently unused but available for future customization
     */
    private void init(AttributeSet attrs) {
        // Initialize basic measurements and objects
        strokeWidth = dpToPx(30);  // Convert 30dp to pixels for stroke width
        arcRect = new RectF();     // Rectangle for drawing arcs
        imageRect = new RectF();   // Rectangle for center image
        rotateMatrix = new Matrix(); // Matrix for rotating gradient

        // Create dashed line effect (3dp dashes with 4dp gaps)
        dashEffect = new DashPathEffect(new float[]{dpToPx(3), dpToPx(4)}, 0);

        // Setup paint for the progress arc
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND); // Rounded ends for arcs

        // Setup paint for the background circle
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setAlpha(77); // Semi-transparent background

        // Setup paint for indicator dots
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);

        // Setup paint for text elements
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(spToPx(14)); // Convert 14sp to pixels
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Setup paint for decorative outer circle
        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(dpToPx(4));
        outerCirclePaint.setColor(Color.parseColor("#FFC0CB")); // Light pink

        // Setup paint for decorative inner dashed circle
        innerDashedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerDashedCirclePaint.setStyle(Paint.Style.STROKE);
        innerDashedCirclePaint.setStrokeWidth(dpToPx(4));
        innerDashedCirclePaint.setColor(Color.parseColor("#FFE4C4")); // Bisque color
        innerDashedCirclePaint.setPathEffect(dashEffect); // Apply dash pattern
    }

    /**
     * Initialize the pulse animation for the center image
     */
    private void initPulseAnimator() {
        // Create animator that changes alpha from 0.2 to 1.0 and back
        pulseAnimator = ObjectAnimator.ofFloat(this, "centerImageAlpha", 0.2f, 1.0f, 0.2f);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);   // Reverse animation on repeat
    }

    /**
     * Calculate positioning for the gradient color stops based on cycle phases
     *
     * @param cycleLength Total length of the menstrual cycle in days
     * @return Array of position values (0.0-1.0) for gradient color stops
     */
    private float[] calculateGradientPositions(int cycleLength) {
        // Calculate relative positions for each phase of the cycle
        float periodLength = 5f / cycleLength;               // Menstruation phase (first 5 days)
        float follicularStart = periodLength;                // Follicular phase start
        float ovulationStart = 14f / cycleLength;            // Ovulation phase start (around day 14)
        float ovulationEnd = (14f + 2f) / cycleLength;       // Ovulation phase end (2 days duration)
        float lutealStart = ovulationEnd;                    // Luteal phase start
        float preMenstrualStart = (cycleLength - 5f) / cycleLength; // Pre-menstrual phase start

        // Return positions array with slight overlaps for smoother gradient transitions
        return new float[] {
                0f,                     // Start position
                periodLength,           // End of period phase
                follicularStart + 0.05f, // Start of follicular phase (with overlap)
                ovulationStart - 0.05f, // End of follicular phase (with overlap)
                ovulationStart,         // Start of ovulation phase
                ovulationEnd,           // End of ovulation phase
                lutealStart + 0.05f,    // Start of luteal phase (with overlap)
                preMenstrualStart,      // Start of pre-menstrual phase
                1f                      // End position
        };
    }

    /**
     * Determines the size of the view based on available space
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        // Make the view a perfect square using the minimum of width and height
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    /**
     * Called when the size of the view changes to recalculate drawing areas
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate content area dimensions and center point
        float contentWidth = w - getPaddingLeft() - getPaddingRight();
        float contentHeight = h - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;

        // Set up the rectangle for drawing the progress arc
        // Account for stroke width to keep arc within view bounds
        float padding = strokeWidth / 2;
        arcRect.set(
                getPaddingLeft() + padding,
                getPaddingTop() + padding,
                w - getPaddingRight() - padding,
                h - getPaddingBottom() - padding
        );

        // Set up the rectangle for drawing the center image
        float imageWidth = contentWidth * imageSize;
        float imageHeight = contentHeight * imageSize;
        imageRect.set(
                centerX - imageWidth / 2,
                centerY - imageHeight / 2,
                centerX + imageWidth / 2,
                centerY + imageHeight / 2
        );

        // Initialize or update the gradient with the new dimensions
        updateGradient(centerX, centerY);
    }

    /**
     * Creates or updates the gradient for the progress arc
     *
     * @param centerX The x-coordinate of the view's center
     * @param centerY The y-coordinate of the view's center
     */
    private void updateGradient(float centerX, float centerY) {
        // Get gradient positions based on cycle length
        float[] positions = calculateGradientPositions(maxProgress);

        // Define colors for each phase of the cycle
        int[] colors = {
                PERIOD_RED,      // Menstruation
                LIGHT_RED,       // Transition
                SAFE_GREEN,      // Low fertility
                LIGHT_BLUE,      // Transition to ovulation
                OVULATION_BLUE,  // Ovulation
                LIGHT_BLUE,      // Transition from ovulation
                SAFE_GREEN,      // Low fertility
                LIGHT_RED,       // Pre-menstrual
                PERIOD_RED       // Back to menstruation
        };

        // Create a sweeping gradient that starts at the top (270 degrees rotation)
        sweepGradient = new SweepGradient(centerX, centerY, colors, positions);
        rotateMatrix.setRotate(270, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);
    }

    /**
     * Draws all components of the view
     *
     * @param canvas The canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate center and radius for drawing
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        float radius = Math.min(contentWidth, contentHeight) / 2;

        // Draw decorative circles
        canvas.drawCircle(centerX, centerY, radius * 0.8f, outerCirclePaint);
        canvas.drawCircle(centerX, centerY, radius * 0.7f, innerDashedCirclePaint);

        // Draw background circle (full 360 degrees)
        canvas.drawArc(arcRect, 0, 360, false, backgroundPaint);

        // Draw progress arc from top (-90 degrees) by calculated sweep angle
        float sweepAngle = (progress / (float) maxProgress) * 360;
        canvas.drawArc(arcRect, -90, sweepAngle, false, progressPaint);

        // Draw center image if available
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

        // Draw indicator dots with text for period and fertility
        drawDotWithText(canvas, -90, Color.RED, String.valueOf(daysUntilPeriod), Color.WHITE);
        drawDotWithText(canvas, 90, Color.BLUE, String.valueOf(daysUntilFertile), Color.WHITE);

        // Draw center text showing current day
        textPaint.setTextSize(spToPx(18));
        // Set an appropriate color for the text based on your expected background
        textPaint.setColor(Color.BLACK); // Or any other color that will be visible on your background
        String centerText = getCycleLength()-progress + " days";
        float textY = centerY + (centerImage != null ? imageRect.height()/2 : 0) + textPaint.getTextSize();
        canvas.drawText(centerText, centerX, textY, textPaint);
    }

    /**
     * Draws an indicator dot with text at specified angle on the progress circle
     *
     * @param canvas The canvas to draw on
     * @param angle Angle in degrees (0 = right, 90 = bottom, 180 = left, 270/-90 = top)
     * @param dotColor Color of the indicator dot
     * @param text Text to display in the dot
     * @param textColor Color of the text
     */
    private void drawDotWithText(Canvas canvas, float angle, int dotColor, String text, int textColor) {
        // Calculate center and radius
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        float radius = Math.min(contentWidth, contentHeight) / 2 - strokeWidth/2;

        // Calculate position on the circle using trigonometry
        double radians = Math.toRadians(angle);
        float dotX = (float) (centerX + radius * Math.cos(radians));
        float dotY = (float) (centerY + radius * Math.sin(radians));

        // Draw the dot slightly larger than the stroke width
        float dotRadius = strokeWidth/2 * 1.2f;
        dotPaint.setColor(dotColor);
        canvas.drawCircle(dotX, dotY, dotRadius, dotPaint);

        // Draw the text centered in the dot
        textPaint.setColor(textColor);
        textPaint.setTextSize(spToPx(12));
        canvas.drawText(text, dotX, dotY + textPaint.getTextSize()/3, textPaint);
    }

    /**
     * Sets the total length of the menstrual cycle in days
     *
     * @param cycleDays Number of days in the cycle
     */
    public void setCycleLength(int cycleDays) {
        if (cycleDays > 0) {
            this.maxProgress = cycleDays;
            // Ensure progress doesn't exceed the new cycle length
            if (this.progress > cycleDays) {
                this.progress = cycleDays;
            }
            // Update gradient if view has been sized
            if (getWidth() > 0) {
                updateGradient(getWidth() / 2f, getHeight() / 2f);
            }
            invalidate();
        }
    }

    /**
     * Sets the current day in the cycle
     *
     * @param progress Current day (1-based, will be constrained to valid range)
     */
    public void setProgress(int progress) {
        // Constrain progress to valid range (0 to maxProgress)
        this.progress = Math.min(Math.max(progress, 0), maxProgress);
        invalidate();
    }

    /**
     * Sets the number of days until next period
     *
     * @param days Number of days until period
     */
    public void setDaysUntilPeriod(int days) {
        this.daysUntilPeriod = days;
        invalidate();
    }

    /**
     * Sets the number of days until fertile window
     *
     * @param days Number of days until fertile window
     */
    public void setDaysUntilFertile(int days) {
        this.daysUntilFertile = days;
        invalidate();
    }

    /**
     * Gets the current cycle length in days
     *
     * @return Current cycle length
     */
    public int getCycleLength() {
        return maxProgress;
    }

    /**
     * Sets the drawable to display in the center
     *
     * @param drawable Drawable to display
     */
    public void setCenterImage(Drawable drawable) {
        this.centerImage = drawable;
        invalidate();
    }

    /**
     * Sets the center image from a resource ID
     *
     * @param resourceId Resource ID of drawable to display
     */
    public void setCenterImageResource(@DrawableRes int resourceId) {
        setCenterImage(ContextCompat.getDrawable(getContext(), resourceId));
    }

    /**
     * Sets the size ratio of the center image (relative to view size)
     *
     * @param ratio Size ratio between 0.0 and 1.0
     */
    public void setImageSizeRatio(float ratio) {
        this.imageSize = Math.min(Math.max(ratio, 0f), 1f);
        invalidate();
    }

    /**
     * Sets the alpha value of the center image (used by animator)
     *
     * @param alpha Alpha value between 0.0 and 1.0
     */
    @Keep // Prevent this method from being removed by ProGuard
    public void setCenterImageAlpha(float alpha) {
        this.centerImageAlpha = alpha;
        invalidate();
    }

    /**
     * Gets the alpha value of the center image (used by animator)
     *
     * @return Current alpha value
     */
    @Keep // Prevent this method from being removed by ProGuard
    public float getCenterImageAlpha() {
        return centerImageAlpha;
    }

    /**
     * Starts a pulsing animation effect on the center image
     *
     * @param duration Duration of one pulse cycle in milliseconds
     */
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

    /**
     * Stops the pulsing animation effect
     */
    public void stopPulseEffect() {
        if (pulseAnimator != null && isPulsing) {
            pulseAnimator.cancel();
            centerImageAlpha = 1.0f; // Reset alpha to fully visible
            invalidate();
            isPulsing = false;
        }
    }

    /**
     * Configures the pulse effect based on days left count
     * Fast pulse for day of event, slower pulse for approaching days
     *
     * @param daysLeftCount Number of days until important event
     */
    public void configurePulseEffect(long daysLeftCount) {
        if (daysLeftCount < 1) {
            // Day of event - fast pulse (500ms)
            startPulseEffect(500);
        } else if (daysLeftCount <= 5) {
            // Approaching event - slower pulse (1000ms)
            startPulseEffect(1000);
        } else {
            // Far from event - no pulse
            stopPulseEffect();
        }
    }

    /**
     * Clean up animations when view is detached from window
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPulseEffect(); // Ensure animations are canceled
    }

    /**
     * Utility method to convert density-independent pixels to pixels
     *
     * @param dp Value in dp
     * @return Value in pixels
     */
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    /**
     * Utility method to convert scale-independent pixels to pixels
     *
     * @param sp Value in sp
     * @return Value in pixels
     */
    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}