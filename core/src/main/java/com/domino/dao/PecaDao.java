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
    public Peca converterDocumentoParaPeca(Document doc) {
        Peca p = new Peca();

        p.setInfo1(doc.getString("info1"));
        p.setTipo1(Tipo.valueOf(doc.getString("tipo1")));
        p.setInfo2(doc.getString("info2"));
        p.setTipo2(Tipo.valueOf(doc.getString("tipo2")));

        return p;
    }
}
