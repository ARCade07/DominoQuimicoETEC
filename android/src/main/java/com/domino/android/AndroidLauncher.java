package com.domino.android;

import android.os.Bundle;
import android.view.MotionEvent;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.domino.Main;
import com.domino.android.input.AndroidGestureProcessor;

public class AndroidLauncher extends AndroidApplication {
  private AndroidGestureProcessor gestureProcessor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inicializar a conexão com o banco de dados antes de criar a app
    AndroidConnectionFactory.initialize(this);

    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    config.useCompass = false;
    config.useAccelerometer = false;

    gestureProcessor = new AndroidGestureProcessor(this);

    initialize(new Main(), config);
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    if (gestureProcessor != null) {
      gestureProcessor.onTouchEvent(event);
    }
    return super.onGenericMotionEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (gestureProcessor != null) {
      gestureProcessor.onTouchEvent(event);
    }
    return super.onTouchEvent(event);
  }
}
