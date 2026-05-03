package com.domino.dao;

import com.domino.bd.ConnectionFactory;
import com.domino.modelos.Estatisticas;
import com.domino.modelos.Usuario;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

public class UsuarioDao {
    // Onde os documentos BSON serão guardados:
    private final MongoCollection<Document> docsUsuarios;

    // Realiza conexão e seleciona a coleção
    public UsuarioDao(ConnectionFactory connection) {

        this.docsUsuarios = connection.getDatabase().getCollection("usuarios");

        // Criação de um índice único email (para não haver repetições)
        IndexOptions opcoes = new IndexOptions().unique(true);
        this.docsUsuarios.createIndex(Indexes.ascending("email"), opcoes);
    }
}
