package com.domino.bd;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionFactory {

    private static ConnectionFactory instanciaBD;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private ConnectionFactory(){
        try {
            Dotenv dotenv = Dotenv.load();
            String connectionString = dotenv.get("MONGO_URI");

            // Configuração do cliente MongoDB
            ConnectionString connString = new ConnectionString(connectionString);
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();

            //Criando o cliente
            mongoClient = MongoClients.create(settings);

            // Selecionando o banco de dados:
            database = mongoClient.getDatabase(dotenv.get("DATABASE_NAME"));

            System.out.println("Conexão estabelecida com sucesso ao MongoDB!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ConnectionFactory getInstance(){
        if (instanciaBD == null) {
            instanciaBD = new ConnectionFactory();
        }
        return instanciaBD;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
