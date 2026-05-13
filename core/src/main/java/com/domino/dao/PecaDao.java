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
        // try with resources para que a conexão com o banco seja fechada automaticamente.
        // Aggregate é uma alternativa ao find. Além disso, nele os dados podem passam por diversas etapas
        // antes de voltar.
        try (MongoCursor<Document> cursor = docsPecas.aggregate(
            // Sample é a etapa para pegar aleatoriamente os docs
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
        Tipo tipo1  = Tipo.valueOf(doc.getString("tipo1"));

        String info2 = doc.getString("info2");
        Tipo tipo2 = Tipo.valueOf(doc.getString("tipo2"));

        return new Peca(info1, tipo1, info2, tipo2);
    }
}
