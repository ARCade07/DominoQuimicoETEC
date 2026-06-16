package com.domino.input;

import java.util.ArrayList;
import java.util.List;

public class InputManager {
  private static final InputManager instance = new InputManager();
  private final List<GestureController> listeners = new ArrayList<>();

  private InputManager() {}

  public static InputManager getInstance() {
    return instance;
  }

  public void addGestureListener(GestureController listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeGestureListener(GestureController listener) {
    listeners.remove(listener);
  }

  public void notifyBackGesture() {
    for (GestureController listener : listeners) {
      listener.onBackGestureDetected();
    }
  }

  public void notifyPinchZoom(float scaleFactor) {
    for (GestureController listener : listeners) {
      listener.onPinchZoom(scaleFactor);
    }
  }

  public void notifyTwoFingerDrag(float deltaX, float deltaY) {
    for (GestureController listener : listeners) {
      listener.onTwoFingerDrag(deltaX, deltaY);
    }
  }

  public void notifySingleFingerDrag(float deltaX, float deltaY) {
    for (GestureController listener : listeners) {
      listener.onSingleFingerDrag(deltaX, deltaY);
    }
  }
}
