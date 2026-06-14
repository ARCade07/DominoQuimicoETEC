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
        this.pecaDao = new PecaDao(connection);
    }

    public List<Peca> buscarTodasAsPecas() {
        return this.pecaDao.buscarTodasAsPecas();
    }
}
