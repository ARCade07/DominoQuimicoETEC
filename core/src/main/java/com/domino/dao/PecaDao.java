package com.domino.dao;

import com.domino.bd.ConnectionFactory;
import com.domino.logica.Peca;
import com.domino.logica.Tipo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PecaDao {
    private final MongoCollection<Document> docsPecas;

    public PecaDao(ConnectionFactory connection) {
        this.docsPecas = connection.getDatabase().getCollection("pecas");
    }
}
