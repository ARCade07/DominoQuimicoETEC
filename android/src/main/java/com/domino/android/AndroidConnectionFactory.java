package com.domino.android;

import android.content.Context;
import com.domino.bd.ConnectionFactory;

public class AndroidConnectionFactory {
  private static boolean initialized = false;

  public static void initialize(Context context) {
    if (initialized) {
      return;
    }

    try {
      String mongoUri = null;
      String databaseName = null;

      try {
        mongoUri = context.getString(R.string.mongo_uri);
        databaseName = context.getString(R.string.database_name);
      } catch (Exception e) {
        android.util.Log.e("ChemDom", "Erro ao ler strings.xml: " + e.getMessage());
        throw e;
      }

      if (mongoUri == null || mongoUri.isEmpty() ||
          databaseName == null || databaseName.isEmpty()) {
        throw new IllegalStateException(
          "MongoDB configuração vazia em strings.xml. " +
          "mongoUri=" + mongoUri + ", databaseName=" + databaseName
        );
      }

      android.util.Log.d("ChemDom", "Inicializando com mongoDB: " + mongoUri.substring(0, Math.min(50, mongoUri.length())) + "...");

      ConnectionFactory.initializeWith(mongoUri, databaseName);
      initialized = true;

      android.util.Log.d("ChemDom", "✓ AndroidConnectionFactory inicializado com sucesso");
    } catch (Exception e) {
      android.util.Log.e("ChemDom", "✗ Erro ao inicializar AndroidConnectionFactory: " + e.getMessage(), e);
      e.printStackTrace();
      initialized = false;
      // Não lançar exceção para não crashear a app
    }
  }

  public static boolean isInitialized() {
    return initialized;
  }

  public static void resetInitialization() {
    initialized = false;
  }
}
