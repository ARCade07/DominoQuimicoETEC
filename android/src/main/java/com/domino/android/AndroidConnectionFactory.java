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
      String mongoUri = context.getString(R.string.mongo_uri);
      String databaseName = context.getString(R.string.database_name);

      ConnectionFactory.initializeWith(mongoUri, databaseName);
      initialized = true;

      android.util.Log.d("ChemDom", "Android ConnectionFactory initialized");
    } catch (Exception e) {
      android.util.Log.e("ChemDom", "Error initializing Android ConnectionFactory", e);
      initialized = false;
    }
  }

  public static boolean isInitialized() {
    return initialized;
  }

  public static void resetInitialization() {
    initialized = false;
  }
}
