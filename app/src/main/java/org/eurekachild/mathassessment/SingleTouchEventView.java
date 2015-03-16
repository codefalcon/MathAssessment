package org.eurekachild.mathassessment;

/**
 * Created by safiq on 15-03-2015.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SingleTouchEventView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    //    private boolean isClear = false;
    private GestureDetector gestureDetector;
    private boolean isFirstLoad = true;

    public SingleTouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(2f);
        paint.setTextSize(20f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
     /*   if(isClear) {
            isClear = false;
            path.reset();
        }
        else*/
        canvas.drawPath(path, paint);

        if (isFirstLoad) {
            isFirstLoad = false;
            canvas.drawText("Use this area for rough work.Double tap to clear.", 125, 125, paint);
            //canvas.drawLine(0,0,getWidth(),getHeight(),paint);
            paint.setStrokeWidth(3f);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                // nothing to do
                break;
            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            //isClear = true;
            //invalidate();
            path.reset();
            return true;
        }
    }
}
