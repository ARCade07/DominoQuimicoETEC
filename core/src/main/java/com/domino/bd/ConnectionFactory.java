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
                System.out.println("📱 Usando configurações externas de conexão (Android)");
            } else {
                try {
                    Dotenv dotenv = Dotenv.load();
                    mongoUri = dotenv.get("MONGO_URI");
                    databaseName = dotenv.get("DATABASE_NAME");

                    if (mongoUri == null || mongoUri.isEmpty() ||
                        databaseName == null || databaseName.isEmpty()) {
                        throw new IllegalStateException(
                            "MONGO_URI ou DATABASE_NAME não configuradas no .env"
                        );
                    }
                    System.out.println("📄 Usando configurações de .env");
                } catch (Exception dotenvError) {
                    System.err.println("⚠️  Aviso: Não conseguiu carregar .env: " + dotenvError.getMessage());
                    System.err.println("   Esperando configuração via AndroidConnectionFactory.initialize()");
                    isConnected = false;
                    database = null;
                    return;
                }
            }

            // Validar configurações
            if (mongoUri == null || mongoUri.isEmpty()) {
                throw new IllegalStateException("MONGO_URI está vazio");
            }
            if (databaseName == null || databaseName.isEmpty()) {
                throw new IllegalStateException("DATABASE_NAME está vazio");
            }

            System.out.println("🔗 Conectando ao MongoDB: " + mongoUri.substring(0, Math.min(50, mongoUri.length())) + "...");

            // mongodb+srv:// depende de registros DNS SRV/TXT para descobrir os hosts do
            // replica set. O resolver padrão do Android não suporta esse tipo de consulta,
            // então essa URI sempre falhará com UnknownHostException no Android.
            // NÃO existe uma forma de "converter" mongodb+srv:// para mongodb:// em tempo de
            // execução sem saber os hosts reais do replica set (o hostname base do +srv não
            // possui registro A — só existe como alvo de consulta SRV). A correção correta é
            // configurar mongo_uri (strings.xml) já no formato mongodb:// com os hosts reais,
            // obtidos uma única vez com:
            //   nslookup -type=SRV _mongodb._tcp.<host>
            //   nslookup -type=TXT <host>
            String connectionUri = mongoUri;
            if (mongoUri.startsWith("mongodb+srv://")) {
                System.err.println("❌ mongodb+srv:// não é suportado no Android (sem resolução de DNS SRV).");
                System.err.println("   Configure mongo_uri em strings.xml usando mongodb:// com os hosts reais do replica set.");
            }

            // Configuração do cliente MongoDB com timeouts maiores para Android
            // Android pode ser mais lento em inicializar conexões
            int timeoutSeconds = isAndroid() ? 20 : 10;
            System.out.println("⏱️  Timeout configurado para: " + timeoutSeconds + " segundos");

            ConnectionString connString = new ConnectionString(connectionUri);
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .applyToSocketSettings(builder ->
                    builder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                           .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                )
                .applyToConnectionPoolSettings(builder ->
                    builder.maxConnectionIdleTime(30, TimeUnit.SECONDS)
                )
                .build();

            // Criando o cliente
            mongoClient = MongoClients.create(settings);

            // Selecionando o banco de dados
            database = mongoClient.getDatabase(databaseName);

            // Teste simples de conexão (não bloqueia se falhar)
            try {
                database.listCollectionNames().first();
                isConnected = true;
                System.out.println("✅ Conexão estabelecida com sucesso ao MongoDB!");
            } catch (Exception testError) {
                System.err.println("⚠️  Cliente criado mas conexão de teste falhou: " + testError.getMessage());
                System.err.println("   Isso pode ser normal em conexões lentas (ex: emulador Android).");
                System.err.println("   A conexão pode ser estabelecida quando você tentar fazer uma operação.");
                isConnected = false;
                // Ainda assim deixar database disponível para tentativa posterior
            }

        } catch (Exception e) {
            isConnected = false;
            System.err.println("❌ Erro ao conectar ao MongoDB: " + e.getMessage());
            e.printStackTrace();
            database = null;
        }
    }

    /**
     * Detecta se está rodando em Android (verificando propriedades do sistema)
     */
    private boolean isAndroid() {
        try {
            // Android tem essa propriedade configurada
            String osName = System.getProperty("java.vendor");
            return osName != null && osName.contains("Android");
        } catch (Exception e) {
            return false;
        }
    }

    public static ConnectionFactory getInstance() {
        if (instanciaBD == null) {
            System.err.println("⚠️  AVISO: ConnectionFactory.getInstance() chamado antes de initializeWith()");
            System.err.println("   Isso pode causar database = null no Android!");
            System.err.println("   Certifique-se de chamar AndroidConnectionFactory.initialize() ANTES de usar o app");
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
     * Verifica se está conectado ao BD.
     * NÃO faz chamadas de rede aqui — isso é chamado da thread de clique/render do libGDX,
     * e uma chamada bloqueante de rede nesse ponto pode travar a UI (ANR). A verificação real
     * de conectividade ocorre em initializeConnection(), executada em background.
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
