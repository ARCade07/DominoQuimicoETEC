package com.domino.bd;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.concurrent.TimeUnit;

public class ConnectionFactory {

    private static ConnectionFactory instanciaBD;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private boolean isConnected = false;

    // Variáveis estáticas para inicialização externa (Android)
    private static String externalMongoUri;
    private static String externalDatabaseName;

    private ConnectionFactory() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            String mongoUri;
            String databaseName;

            // Tenta usar variáveis externas (Android), senão carrega do .env
            if (externalMongoUri != null && externalDatabaseName != null) {
                mongoUri = externalMongoUri;
                databaseName = externalDatabaseName;
                System.out.println("Usando configurações externas de conexão");
            } else {
                Dotenv dotenv = Dotenv.load();
                mongoUri = dotenv.get("MONGO_URI");
                databaseName = dotenv.get("DATABASE_NAME");

                if (mongoUri == null || databaseName == null) {
                    throw new IllegalStateException(
                        "MONGO_URI ou DATABASE_NAME não configuradas. " +
                        "Configure via .env ou AndroidConnectionFactory.initialize()"
                    );
                }
                System.out.println("Usando configurações de .env");
            }

            // Configuração do cliente MongoDB com timeouts
            ConnectionString connString = new ConnectionString(mongoUri);
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .applyToSocketSettings(builder ->
                    builder.connectTimeout(10, TimeUnit.SECONDS)
                           .readTimeout(10, TimeUnit.SECONDS)
                )
                .applyToConnectionPoolSettings(builder ->
                    builder.maxConnectionIdleTime(30, TimeUnit.SECONDS)
                )
                .build();

            // Criando o cliente
            mongoClient = MongoClients.create(settings);

            // Selecionando o banco de dados
            database = mongoClient.getDatabase(databaseName);

            // Teste simples de conexão
            database.listCollectionNames().first();

            isConnected = true;
            System.out.println("✓ Conexão estabelecida com sucesso ao MongoDB!");

        } catch (Exception e) {
            isConnected = false;
            System.err.println("✗ Erro ao conectar ao MongoDB: " + e.getMessage());
            e.printStackTrace();
            database = null;
        }
    }

    public static ConnectionFactory getInstance() {
        if (instanciaBD == null) {
            instanciaBD = new ConnectionFactory();
        }
        return instanciaBD;
    }

    /**
     * Inicializa a factory com variáveis externas (ex: Android)
     * Deve ser chamado ANTES de getInstance()
     */
    public static void initializeWith(String mongoUri, String databaseName) {
        externalMongoUri = mongoUri;
        externalDatabaseName = databaseName;

        // Se já foi inicializada, reinicializar com novas configurações
        if (instanciaBD != null) {
            instanciaBD.fecharConexao();
            instanciaBD = new ConnectionFactory();
        }
    }

    public void fecharConexao() {
        if (mongoClient != null) {
            mongoClient.close();
            isConnected = false;
            System.out.println("Conexão com o banco encerrada.");
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Verifica se está conectado ao BD
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Obtém informações de status da conexão
     */
    public String getStatus() {
        if (isConnected) {
            return "✓ Conectado ao MongoDB";
        } else if (database != null) {
            return "⚠ Conexão instável ao MongoDB";
        } else {
            return "✗ Sem conexão ao MongoDB";
        }
    }
}
