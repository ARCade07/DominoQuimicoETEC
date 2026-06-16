package com.domino.android.input;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.badlogic.gdx.InputProcessor;
import com.domino.input.InputManager;

public class AndroidGestureProcessor implements InputProcessor,
    GestureDetector.OnGestureListener,
    ScaleGestureDetector.OnScaleGestureListener {

  private static final float BACK_GESTURE_THRESHOLD = 100f;
  private static final float BACK_GESTURE_EDGE = 50f;
  private static final float DRAG_THRESHOLD = 10f;
  private static final long BACK_GESTURE_TIMEOUT = 500;

  private final GestureDetector gestureDetector;
  private final ScaleGestureDetector scaleGestureDetector;
  private final InputManager inputManager;

  private float lastX = 0;
  private float lastY = 0;
  private float downX = 0;
  private float downY = 0;
  private long downTime = 0;
  private int activePointers = 0;
  private boolean isBackGestureCandidate = false;

  public AndroidGestureProcessor(Context context) {
    this.inputManager = InputManager.getInstance();
    this.gestureDetector = new GestureDetector(context, this);
    this.scaleGestureDetector = new ScaleGestureDetector(context, this);
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }

  @Override
  public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    scaleGestureDetector.onTouchEvent(event);

    int action = event.getActionMasked();
    activePointers = event.getPointerCount();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        downY = event.getY();
        lastX = downX;
        lastY = downY;
        downTime = System.currentTimeMillis();
        isBackGestureCandidate = isNearLeftEdge(downX);
        break;

      case MotionEvent.ACTION_MOVE:
        float deltaX = event.getX() - lastX;
        float deltaY = event.getY() - lastY;

        if (Math.abs(deltaX) > DRAG_THRESHOLD || Math.abs(deltaY) > DRAG_THRESHOLD) {
          if (activePointers == 1) {
            handleSingleFingerDrag(event.getX(), deltaX, deltaY);
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

      case MotionEvent.ACTION_POINTER_UP:
      case MotionEvent.ACTION_POINTER_DOWN:
        activePointers = event.getPointerCount();
        break;
    }

    return true;
  }

  private boolean isNearLeftEdge(float x) {
    return x < BACK_GESTURE_EDGE;
  }

  private void handleSingleFingerDrag(float currentX, float deltaX, float deltaY) {
    // Back gesture detection: started near left edge, dragged right
    if (isBackGestureCandidate && (currentX - downX) > BACK_GESTURE_THRESHOLD) {
      long elapsedTime = System.currentTimeMillis() - downTime;
      if (elapsedTime < BACK_GESTURE_TIMEOUT) {
        inputManager.notifyBackGesture();
        isBackGestureCandidate = false;
        return;
      }
    }

    // Regular single finger drag
    inputManager.notifySingleFingerDrag(deltaX, deltaY);
  }

  private void handleTwoFingerDrag(float deltaX, float deltaY) {
    inputManager.notifyTwoFingerDrag(deltaX, deltaY);
  }

  @Override
  public boolean onScale(ScaleGestureDetector detector) {
    float scaleFactor = detector.getScaleFactor();
    if (scaleFactor != 1.0f) {
      inputManager.notifyPinchZoom(scaleFactor);
    }
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
