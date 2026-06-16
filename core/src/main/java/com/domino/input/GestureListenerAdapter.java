package com.domino.input;

public abstract class GestureListenerAdapter implements GestureController {
  @Override
  public void onBackGestureDetected() {}

  @Override
  public void onPinchZoom(float scaleFactor) {}

  @Override
  public void onTwoFingerDrag(float deltaX, float deltaY) {}

  @Override
  public void onSingleFingerDrag(float deltaX, float deltaY) {}
}
