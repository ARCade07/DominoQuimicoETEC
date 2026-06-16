package com.domino.servicos;

import com.domino.bd.ConnectionFactory;
import com.domino.dao.PecaDao;
import com.domino.logica.Peca;

import java.util.List;

public class ServicoPecas {
    private final PecaDao pecaDao;

    // Construtor cria e gerencia a conexão com o banco internamente, evitando acoplamento na GameScreen
    public ServicoPecas() {
        ConnectionFactory connection = ConnectionFactory.getInstance();

        if (connection == null || connection.getDatabase() == null) {
            throw new IllegalStateException(
                "❌ ERRO CRÍTICO: Banco de dados não foi inicializado!\n" +
                "   Verifique se AndroidConnectionFactory.initialize() foi chamado ANTES de criar GameScreen.\n" +
                "   Status: database=" + (connection == null ? "null" : "not null but DB=" + (connection.getDatabase() == null ? "null" : "ok"))
            );
        }

        this.pecaDao = new PecaDao(connection);
    }

    public List<Peca> buscarTodasAsPecas() {
        return this.pecaDao.buscarPecasAleatorias(35);
    }
}
