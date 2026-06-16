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

    try {
      // Log de inicialização
      android.util.Log.d("ChemDom", "========================================");
      android.util.Log.d("ChemDom", "🎮 ChemDom iniciando...");
      android.util.Log.d("ChemDom", "========================================");

      // Inicializar a conexão com o banco de dados antes de criar a app
      android.util.Log.d("ChemDom", "📱 Inicializando conexão com BD...");
      AndroidConnectionFactory.initialize(this);
      android.util.Log.d("ChemDom", "✓ Inicialização de BD concluída");

      AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
      config.useCompass = false;
      config.useAccelerometer = false;

      gestureProcessor = new AndroidGestureProcessor(this);

      android.util.Log.d("ChemDom", "🚀 Criando aplicação Main...");
      initialize(new Main(), config);
      android.util.Log.d("ChemDom", "✓ Aplicação iniciada com sucesso!");

    } catch (Exception e) {
      android.util.Log.e("ChemDom", "❌ Erro fatal ao inicializar: " + e.getMessage(), e);
      e.printStackTrace();
      // Não re-lançar para evitar crash imediato
      // A app será inicializada mas o BD pode estar indisponível
    }
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
