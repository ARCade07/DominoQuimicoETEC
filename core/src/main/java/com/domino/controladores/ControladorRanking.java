package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;
import com.domino.telas.RankingScreen;

import java.util.List;

public class ControladorRanking {


    private UsuarioDao u;

    public ControladorRanking(UsuarioDao u){
        this.u = u;
    }

    public RankingScreen.EntradaRanking[] gerarRanking(UsuarioDao dao, Usuario usuarioLogado) {
        List<Usuario> topUsuarios = dao.buscarTopJogadores(25);
        RankingScreen.EntradaRanking[] dados = new RankingScreen.EntradaRanking[topUsuarios.size()];

        for (int i = 0; i < topUsuarios.size(); i++) {
            Usuario u = topUsuarios.get(i);

            boolean jogadorLogado = u.getId().equals(usuarioLogado.getId());

            int pontuacaoExibicao = u.getEstat().getPontuacao();

            dados[i] = new RankingScreen.EntradaRanking(i + 1, u.getNome(), pontuacaoExibicao, jogadorLogado);
        }

        return dados;
    }

    public RankingScreen.EntradaRanking gerarEntradaJogadorLogado(UsuarioDao dao, Usuario usuarioLogado) {
        int pontuacaoExibicao = usuarioLogado.getEstat().getPontuacao();

        int posicaoDoJogador = u.buscarPosicaoJogador(pontuacaoExibicao);

        return new RankingScreen.EntradaRanking(posicaoDoJogador, usuarioLogado.getNome(), pontuacaoExibicao, true);
    }
}
