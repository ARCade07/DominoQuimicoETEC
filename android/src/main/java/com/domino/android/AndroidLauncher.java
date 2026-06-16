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

    android.util.Log.d("ChemDom", "========================================");
    android.util.Log.d("ChemDom", "🎮 ChemDom iniciando...");
    android.util.Log.d("ChemDom", "========================================");

    try {
      // Inicializar a conexão com o banco de dados antes de criar a app
      android.util.Log.d("ChemDom", "📱 Inicializando conexão com BD...");
      AndroidConnectionFactory.initialize(this);
      android.util.Log.d("ChemDom", "✓ Inicialização de BD concluída");
    } catch (Exception e) {
      android.util.Log.e("ChemDom", "⚠️  Erro ao inicializar BD: " + e.getMessage(), e);
    }

    try {
      AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
      config.useCompass = false;
      config.useAccelerometer = false;

      android.util.Log.d("ChemDom", "🚀 Criando aplicação Main...");
      initialize(new Main(), config);
      android.util.Log.d("ChemDom", "✓ Aplicação inicializada com sucesso!");

    } catch (Exception e) {
      android.util.Log.e("ChemDom", "❌ Erro ao inicializar Main: " + e.getMessage(), e);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onResume() {
    try {
      super.onResume();
      android.util.Log.d("ChemDom", "▶️  onResume() chamado");

      // Inicializar gesture processor após libGDX estar pronto
      if (gestureProcessor == null) {
        try {
          gestureProcessor = new AndroidGestureProcessor(this);
          android.util.Log.d("ChemDom", "✓ GestureProcessor inicializado");
        } catch (Exception e) {
          android.util.Log.w("ChemDom", "Aviso: Erro ao inicializar GestureProcessor: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      android.util.Log.e("ChemDom", "❌ Erro em onResume: " + e.getMessage(), e);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onPause() {
    android.util.Log.d("ChemDom", "⏸️  onPause() chamado");
    super.onPause();
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    if (gestureProcessor != null) {
      try {
        gestureProcessor.onTouchEvent(event);
      } catch (Exception e) {
        android.util.Log.w("ChemDom", "Erro ao processar gesto: " + e.getMessage());
      }
    }
    return super.onGenericMotionEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (gestureProcessor != null) {
      try {
        gestureProcessor.onTouchEvent(event);
      } catch (Exception e) {
        android.util.Log.w("ChemDom", "Erro ao processar toque: " + e.getMessage());
      }
    }
    return super.onTouchEvent(event);
  }
}
