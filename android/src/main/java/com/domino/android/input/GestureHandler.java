package com.domino.android.input;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.domino.input.InputManager;

public class GestureHandler extends View implements GestureDetector.OnGestureListener,
    ScaleGestureDetector.OnScaleGestureListener {

  private static final float BACK_GESTURE_THRESHOLD = 100f;
  private static final float BACK_GESTURE_EDGE = 50f;
  private static final float DRAG_THRESHOLD = 10f;

  private final GestureDetector gestureDetector;
  private final ScaleGestureDetector scaleGestureDetector;
  private final InputManager inputManager;

  private float lastX = 0;
  private float lastY = 0;
  private int activePointers = 0;
  private boolean isBackGestureCandidate = false;

  public GestureHandler(Context context) {
    super(context);
    this.inputManager = InputManager.getInstance();
    this.gestureDetector = new GestureDetector(context, this);
    this.scaleGestureDetector = new ScaleGestureDetector(context, this);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    scaleGestureDetector.onTouchEvent(event);

    int action = event.getActionMasked();
    activePointers = event.getPointerCount();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        lastX = event.getX();
        lastY = event.getY();
        isBackGestureCandidate = isNearLeftEdge(event.getX());
        break;

      case MotionEvent.ACTION_MOVE:
        float deltaX = event.getX() - lastX;
        float deltaY = event.getY() - lastY;

        if (Math.abs(deltaX) > DRAG_THRESHOLD || Math.abs(deltaY) > DRAG_THRESHOLD) {
          if (activePointers == 1) {
            handleSingleFingerDrag(deltaX, deltaY);
          } else if (activePointers == 2) {
            handleTwoFingerDrag(deltaX, deltaY);
          }
        }

        lastX = event.getX();
        lastY = event.getY();
        break;

      case MotionEvent.ACTION_UP:
        isBackGestureCandidate = false;
        break;
    }

    return true;
  }

  private boolean isNearLeftEdge(float x) {
    return x < BACK_GESTURE_EDGE;
  }

  private void handleSingleFingerDrag(float deltaX, float deltaY) {
    if (isBackGestureCandidate && deltaX > BACK_GESTURE_THRESHOLD) {
      inputManager.notifyBackGesture();
      isBackGestureCandidate = false;
    } else {
      inputManager.notifySingleFingerDrag(deltaX, deltaY);
    }
  }

  private void handleTwoFingerDrag(float deltaX, float deltaY) {
    inputManager.notifyTwoFingerDrag(deltaX, deltaY);
  }

  @Override
  public boolean onScale(ScaleGestureDetector detector) {
    float scaleFactor = detector.getScaleFactor();
    inputManager.notifyPinchZoom(scaleFactor);
    return true;
  }

  @Override
  public boolean onScaleBegin(ScaleGestureDetector detector) {
    return true;
  }

  @Override
  public void onScaleEnd(ScaleGestureDetector detector) {}

  @Override
  public boolean onDown(MotionEvent e) {
    return true;
  }

  @Override
  public void onShowPress(MotionEvent e) {}

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    return false;
  }

  @Override
  public void onLongPress(MotionEvent e) {}

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }
}
