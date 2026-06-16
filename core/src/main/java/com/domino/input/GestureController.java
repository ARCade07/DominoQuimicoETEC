package com.domino.input;

public interface GestureController {
  void onBackGestureDetected();

  void onPinchZoom(float scaleFactor);

  void onTwoFingerDrag(float deltaX, float deltaY);

  void onSingleFingerDrag(float deltaX, float deltaY);
}
