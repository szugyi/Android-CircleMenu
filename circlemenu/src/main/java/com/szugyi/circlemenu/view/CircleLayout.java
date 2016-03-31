package com.szugyi.circlemenu.view;

/*
 * Copyright 2015 Csaba Szugyiczki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.szugyi.circlemenu.R;

import java.security.InvalidParameterException;

/**
 * @author Szugyi Creates a rotatable circle menu which can be parameterized by
 *         custom attributes. Handles touches and gestures to make the menu
 *         rotatable, and to make the menu items selectable and clickable.
 */
public class CircleLayout extends ViewGroup {
    public enum FirstChildPosition {
        EAST(0), SOUTH(90), WEST(180), NORTH(270);

        private int angle;

        FirstChildPosition(int angle) {
            this.angle = angle;
        }

        public int getAngle() {
            return angle;
        }

    }

    // Event listeners
    private OnItemClickListener onItemClickListener = null;
    private OnItemSelectedListener onItemSelectedListener = null;
    private OnCenterClickListener onCenterClickListener = null;
    private OnRotationFinishedListener onRotationFinishedListener = null;

    // Background image
    private Bitmap originalBackground, scaledBackground;

    // Sizes of the ViewGroup
    private int circleWidth, circleHeight;
    private float radius = 0;

    // Child sizes
    private int maxChildWidth = 0;
    private int maxChildHeight = 0;

    // Touch detection
    private GestureDetector gestureDetector;
    // Detecting inverse rotations
    private boolean[] quadrantTouched;

    // Settings of the ViewGroup
    private int speed = 25;
    private float angle = 90;
    private FirstChildPosition firstChildPosition = FirstChildPosition.SOUTH;
    private boolean isRotating = true;

    // Touch helpers
    private double touchStartAngle;
    private boolean didMove = false;

    // Tapped and selected child
    private View selectedView = null;

    // Rotation animator
    private ObjectAnimator animator;

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Initializes the ViewGroup and modifies it's default behavior by the
     * passed attributes
     *
     * @param attrs the attributes used to modify default settings
     */
    protected void init(AttributeSet attrs) {
        gestureDetector = new GestureDetector(getContext(),
                new MyGestureListener());
        quadrantTouched = new boolean[]{false, false, false, false, false};

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleLayout);


            speed = a.getInt(R.styleable.CircleLayout_speed, speed);
            isRotating = a.getBoolean(R.styleable.CircleLayout_isRotating, isRotating);

            // The angle where the first menu item will be drawn
            angle = a.getInt(R.styleable.CircleLayout_firstChildPosition, (int) angle);
            for (FirstChildPosition pos : FirstChildPosition.values()) {
                if (pos.getAngle() == angle) {
                    firstChildPosition = pos;
                    break;
                }
            }

            if (originalBackground == null) {
                int picId = a.getResourceId(R.styleable.CircleLayout_circleBackground, -1);

                // If a background image was set as an attribute,
                // retrieve the image
                if (picId != -1) {
                    originalBackground = BitmapFactory.decodeResource(getResources(), picId);
                }
            }

            a.recycle();

            // Needed for the ViewGroup to be drawn
            setWillNotDraw(false);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle % 360;
        setChildAngles();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed <= 0) {
            throw new InvalidParameterException("Speed must be a positive integer number");
        }
        this.speed = speed;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public void setRotating(boolean isRotating) {
        this.isRotating = isRotating;
    }

    public FirstChildPosition getFirstChildPosition() {
        return firstChildPosition;
    }

    public void setFirstChildPosition(FirstChildPosition firstChildPosition) {
        this.firstChildPosition = firstChildPosition;
        if (selectedView != null) {
            if (isRotating) {
                rotateViewToCenter(selectedView);
            } else {
                setAngle(firstChildPosition.getAngle());
            }
        }
    }

    /**
     * Returns the currently selected menu
     *
     * @return the view which is currently the closest to the first item
     * position
     */
    public View getSelectedItem() {
        if (selectedView == null) {
            selectedView = getChildAt(0);
        }
        return selectedView;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        updateAngle();
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        updateAngle();
    }

    @Override
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
        updateAngle();
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        updateAngle();
    }

    private void updateAngle() {
        // Update the position of the views, so we know which is the selected
        setChildAngles();
        rotateViewToCenter(selectedView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // The sizes of the ViewGroup
        circleHeight = getHeight();
        circleWidth = getWidth();

        if (originalBackground != null) {
            // Scaling the size of the background image
            if (scaledBackground == null) {
                float diameter = radius * 2;
                float sx = diameter / originalBackground
                        .getWidth();
                float sy = diameter / originalBackground
                        .getHeight();

                Matrix matrix = new Matrix();
                matrix.postScale(sx, sy);

                scaledBackground = Bitmap.createBitmap(originalBackground, 0, 0,
                        originalBackground.getWidth(), originalBackground.getHeight(),
                        matrix, false);
            }

            if (scaledBackground != null) {
                // Move the background to the center
                int cx = (circleWidth - scaledBackground.getWidth()) / 2;
                int cy = (circleHeight - scaledBackground.getHeight()) / 2;

                canvas.drawBitmap(scaledBackground, cx, cy, null);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure child views first
        maxChildWidth = 0;
        maxChildHeight = 0;

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }

        // Then decide what size we want to be
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(widthSize, heightSize);
        } else {
            //Be whatever you want
            width = maxChildWidth * 3;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(heightSize, widthSize);
        } else {
            //Be whatever you want
            height = maxChildHeight * 3;
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutWidth = r - l;
        int layoutHeight = b - t;

        radius = (layoutWidth <= layoutHeight) ? layoutWidth / 3
                : layoutHeight / 3;

        circleHeight = getHeight();
        circleWidth = getWidth();
        setChildAngles();
    }

    /**
     * Rotates the given view to the firstChildPosition
     *
     * @param view the view to be rotated
     */
    public void rotateViewToCenter(View view) {
        if (isRotating) {
            float viewAngle = view.getTag() != null ? (Float) view.getTag() : 0;
            float destAngle = firstChildPosition.getAngle() - viewAngle;

            if (destAngle < 0) {
                destAngle += 360;
            }

            if (destAngle > 180) {
                destAngle = -1 * (360 - destAngle);
            }

            animateTo(angle + destAngle, 7500L / speed);
        }
    }

    private void rotateButtons(float degrees) {
        angle += degrees;
        setChildAngles();
    }

    private void setChildAngles() {
        int left, top, childWidth, childHeight, childCount = getChildCount();
        float angleDelay = 360.0f / childCount;
        float halfAngle = angleDelay / 2;
        float localAngle = angle;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            if (localAngle > 360) {
                localAngle -= 360;
            } else if (localAngle < 0) {
                localAngle += 360;
            }

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            left = Math
                    .round((float) (((circleWidth / 2.0) - childWidth / 2.0) + radius
                            * Math.cos(Math.toRadians(localAngle))));
            top = Math
                    .round((float) (((circleHeight / 2.0) - childHeight / 2.0) + radius
                            * Math.sin(Math.toRadians(localAngle))));

            child.setTag(localAngle);

            float distance = Math.abs(localAngle - firstChildPosition.getAngle());
            boolean isFirstItem = distance <= halfAngle || distance >= (360 - halfAngle);
            if (isFirstItem && selectedView != child) {
                selectedView = child;
                if (onItemSelectedListener != null && isRotating) {
                    onItemSelectedListener.onItemSelected(child);
                }
            }

            child.layout(left, top, left + childWidth, top + childHeight);
            localAngle += angleDelay;
        }
    }

    private void animateTo(float endDegree, long duration) {
        if (animator != null && animator.isRunning() || Math.abs(angle - endDegree) < 1) {
            return;
        }

        animator = ObjectAnimator.ofFloat(CircleLayout.this, "angle", angle, endDegree);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            private boolean wasCanceled = false;

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (wasCanceled) {
                    return;
                }

                if (onRotationFinishedListener != null) {
                    View view = getSelectedItem();
                    onRotationFinishedListener.onRotationFinished(view);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCanceled = true;
            }
        });
        animator.start();
    }

    private void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
    }

    /**
     * @return The angle of the unit circle with the image views center
     */
    private double getPositionAngle(double xTouch, double yTouch) {
        double x = xTouch - (circleWidth / 2d);
        double y = circleHeight - yTouch - (circleHeight / 2d);

        switch (getPositionQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
            case 3:
                return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                // ignore, does not happen
                return 0;
        }
    }

    /**
     * @return The quadrant of the position
     */
    private static int getPositionQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            gestureDetector.onTouchEvent(event);
            if (isRotating) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // reset the touched quadrants
                        for (int i = 0; i < quadrantTouched.length; i++) {
                            quadrantTouched[i] = false;
                        }

                        stopAnimation();
                        touchStartAngle = getPositionAngle(event.getX(),
                                event.getY());
                        didMove = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        double currentAngle = getPositionAngle(event.getX(),
                                event.getY());
                        rotateButtons((float) (touchStartAngle - currentAngle));
                        touchStartAngle = currentAngle;
                        didMove = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (didMove) {
                            rotateViewToCenter(selectedView);
                        }
                        break;
                    default:
                        break;
                }
            }

            // set the touched quadrant to true
            quadrantTouched[getPositionQuadrant(event.getX()
                    - (circleWidth / 2), circleHeight - event.getY()
                    - (circleHeight / 2))] = true;
            return true;
        }
        return false;
    }

    private class MyGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isRotating) {
                return false;
            }
            // get the quadrant of the start and the end of the fling
            int q1 = getPositionQuadrant(e1.getX() - (circleWidth / 2),
                    circleHeight - e1.getY() - (circleHeight / 2));
            int q2 = getPositionQuadrant(e2.getX() - (circleWidth / 2),
                    circleHeight - e2.getY() - (circleHeight / 2));

            if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
                    .abs(velocityY))
                    || (q1 == 3 && q2 == 3)
                    || (q1 == 1 && q2 == 3)
                    || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
                    .abs(velocityY))
                    || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
                    || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
                    || (q1 == 2 && q2 == 4 && quadrantTouched[3])
                    || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {
                // the inverted rotations
                animateTo(
                        getCenteredAngle(angle - (velocityX + velocityY) / 25),
                        25000L / speed);
            } else {
                // the normal rotation
                animateTo(
                        getCenteredAngle(angle + (velocityX + velocityY) / 25),
                        25000L / speed);
            }

            return true;
        }

        private float getCenteredAngle(float angle) {
            if (getChildCount() == 0) {
                // Prevent divide by zero
                return angle;
            }

            float angleDelay = 360 / getChildCount();
            float localAngle = angle % 360;

            if (localAngle < 0) {
                localAngle = 360 + localAngle;
            }

            for (float i = firstChildPosition.getAngle(); i < firstChildPosition.getAngle() + 360; i += angleDelay) {
                float locI = i % 360;
                float diff = localAngle - locI;
                if (Math.abs(diff) < angleDelay) {
                    angle -= diff;
                    break;
                }
            }

            return angle;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View tappedView = null;
            int tappedViewsPosition = pointToChildPosition(e.getX(), e.getY());
            if (tappedViewsPosition >= 0) {
                tappedView = getChildAt(tappedViewsPosition);
                tappedView.setPressed(true);
            } else {
                // Determine if it was a center click
                float centerX = circleWidth / 2F;
                float centerY = circleHeight / 2F;

                if (onCenterClickListener != null
                        && e.getX() < centerX + radius - (maxChildWidth / 2)
                        && e.getX() > centerX - radius + (maxChildWidth / 2)
                        && e.getY() < centerY + radius - (maxChildHeight / 2)
                        && e.getY() > centerY - radius + (maxChildHeight / 2)) {
                    onCenterClickListener.onCenterClick();
                    return true;
                }
            }

            if (tappedView != null) {
                if (selectedView == tappedView) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(tappedView);
                    }
                } else {
                    rotateViewToCenter(tappedView);
                    if (!isRotating) {
                        if (onItemSelectedListener != null) {
                            onItemSelectedListener.onItemSelected(tappedView);
                        }

                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(tappedView);
                        }
                    }
                }
                return true;
            }
            return super.onSingleTapUp(e);
        }

        private int pointToChildPosition(float x, float y) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getLeft() < x && view.getRight() > x
                        & view.getTop() < y && view.getBottom() > y) {
                    return i;
                }
            }
            return -1;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(View view);
    }

    public void setOnItemSelectedListener(
            OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnCenterClickListener {
        void onCenterClick();
    }

    public void setOnCenterClickListener(
            OnCenterClickListener onCenterClickListener) {
        this.onCenterClickListener = onCenterClickListener;
    }

    public interface OnRotationFinishedListener {
        void onRotationFinished(View view);
    }

    public void setOnRotationFinishedListener(
            OnRotationFinishedListener onRotationFinishedListener) {
        this.onRotationFinishedListener = onRotationFinishedListener;
    }
}
