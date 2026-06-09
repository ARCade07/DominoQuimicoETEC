package com.domino.dao;

import com.domino.bd.ConnectionFactory;
import com.domino.logica.Peca;
import com.domino.logica.Tipo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PecaDao {
    private final MongoCollection<Document> docsPecas;

    public PecaDao(ConnectionFactory connection) {
        this.docsPecas = connection.getDatabase().getCollection("pecas");
    }

    // Qntd é a quantidade de peças iniciais na mão do jogador
    public List<Peca> buscarPecasAleatorias(int qntd) {
        List<Peca> listaDePecas = new ArrayList<>();

        // try-with-resources garante que o cursor seja fechado automaticamente, evitando vazamento de memória.
        // Aggregate permite criar um pipeline de operações no banco antes de retornar os dados.
        try (MongoCursor<Document> cursor = docsPecas.aggregate(
            // Sample pega documentos aleatoriamente com base na quantidade solicitada
            List.of(Aggregates.sample(qntd))).iterator()) {

            while (cursor.hasNext()){
                Document doc = cursor.next();
                Peca peca = converterDocumentoParaPeca(doc);
                listaDePecas.add(peca);
            }
        }

        return listaDePecas;
    }

    public Peca converterDocumentoParaPeca(Document doc) {
        String info1 = doc.getString("info1");
        // Utilizando o Factory Method customizado para evitar quebra por acentuação ou case sensitivity
        Tipo tipo1 = Tipo.fromString(doc.getString("tipo1"));

        String info2 = doc.getString("info2");
        Tipo tipo2 = Tipo.fromString(doc.getString("tipo2"));

        return new Peca(info1, tipo1, info2, tipo2);
    }
}
