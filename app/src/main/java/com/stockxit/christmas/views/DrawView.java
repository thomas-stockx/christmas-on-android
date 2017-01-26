package com.stockxit.christmas.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.stockxit.christmas.R;
import com.stockxit.christmas.logic.Circle;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Thomas on 23/01/2017.
 */

public class DrawView extends View {

    private static final String TAG = "DrawView";

    // Background Bitmap
    private Bitmap mBitmap = null;

    private Rect mMeasuredRect;
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();

    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 50;
    private static final int CIRCLES_LIMIT = 7;

    /** All available circles */
    private HashSet<Circle> mCircles = new HashSet<Circle>(CIRCLES_LIMIT);
    private SparseArray<Circle> mCirclePointer = new SparseArray<Circle>(CIRCLES_LIMIT);

    public DrawView(Context context) {
        super(context);
        setup(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setup(context);
    }

    private void setup(final Context ct) {
        // Generate bitmap used for background
        mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.tree);

        mCirclePaint = new Paint();

        mCirclePaint.setStrokeWidth(3);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(final Canvas canv) {

        // draw background
        canv.drawBitmap(mBitmap, null, mMeasuredRect, null);

        // draw circles
        for (Circle circle : mCircles) {
            int color = circle.getColor();
            mCirclePaint.setColor(color);
            canv.drawCircle(circle.getX(), circle.getY(), circle.getR(), mCirclePaint);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        Circle touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = getTouchedCircle(xTouch, yTouch);

                if (touchedCircle != null) {
                    touchedCircle.setX(xTouch);
                    touchedCircle.setY(yTouch);
                    mCirclePointer.put(event.getPointerId(0), touchedCircle);

                    invalidate();
                    handled = true;
                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // allow multi touch moving of circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = getTouchedCircle(xTouch, yTouch);

                if (touchedCircle != null) {
                    mCirclePointer.put(pointerId, touchedCircle);
                    touchedCircle.setX(xTouch);
                    touchedCircle.setY(yTouch);
                    invalidate();
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.setX(xTouch);
                        touchedCircle.setY(yTouch);
                    }


                }

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // one of the multi-touch circles has been lifted up, don't clear yet
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    private void clearCirclePointer() {
        mCirclePointer.clear();
    }

    private Circle getTouchedCircle(final int xTouch, final int yTouch) {
        Circle touched = null;

        for (Circle circle : mCircles) {
            if ((circle.getX() - xTouch) * (circle.getX() - xTouch) + (circle.getY() - yTouch) * (circle.getY() - yTouch) <= circle.getR() * circle.getR()) {
                touched = circle;
                break;
            }
        }

        return touched;
    }

    public void addCircle(int color) {
        Circle newCircle = new Circle((int) getX() + getWidth() / 2, (int) getY() + getHeight() / 2, mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT, color);

        if (mCircles.size() == CIRCLES_LIMIT) {
            Log.w(TAG, "Clear all circles, size is " + mCircles.size());
            mCircles.clear();
        }

        Log.w(TAG, "Added circle " + newCircle);
        mCircles.add(newCircle);
        invalidate();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
}