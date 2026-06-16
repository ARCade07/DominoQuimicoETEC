package com.domino.android;

import android.content.Context;
import com.domino.bd.ConnectionFactory;

public class AndroidConnectionFactory {
  private static boolean initialized = false;

  public static void initialize(Context context) {
    if (initialized) {
      android.util.Log.d("ChemDom", "⚠️  AndroidConnectionFactory já foi inicializado");
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

      android.util.Log.d("ChemDom", "📌 URI carregada: " + mongoUri.substring(0, Math.min(50, mongoUri.length())) + "...");
      android.util.Log.d("ChemDom", "📌 Database: " + databaseName);

      // Detectar se está usando mongodb+srv (requer DNS SRV)
      if (mongoUri.startsWith("mongodb+srv://")) {
        android.util.Log.w("ChemDom", "⚠️  AVISO: Detectado mongodb+srv. No Android, isso pode causar problemas de DNS.");
        android.util.Log.w("ChemDom", "   Se tiver problemas de conexão, considere usar mongodb:// com replicaSet");
      }

      android.util.Log.d("ChemDom", "⏳ Configurando URI/Database para ConnectionFactory (sem conectar ainda)...");
      // IMPORTANTE: apenas registra a URI/databaseName aqui. A conexão real com o MongoDB
      // (bloqueante, pode levar vários segundos) só deve ocorrer em uma thread de background
      // (ver LoginScreen.initializeDatabaseAsync). Chamar ConnectionFactory.getInstance() aqui
      // bloquearia a thread principal dentro de Activity.onCreate() e causa ANR.
      ConnectionFactory.initializeWith(mongoUri, databaseName);

      initialized = true;
      android.util.Log.d("ChemDom", "✅ AndroidConnectionFactory configurado com sucesso (conexão real ocorrerá em background)");
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
