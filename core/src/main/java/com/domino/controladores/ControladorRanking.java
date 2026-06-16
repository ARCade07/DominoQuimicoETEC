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

    public RankingScreen.EntradaRanking[] gerarRanking(Usuario usuarioLogado) {
        List<Usuario> topUsuarios = u.buscarTopJogadores(25);
        RankingScreen.EntradaRanking[] dados = new RankingScreen.EntradaRanking[topUsuarios.size()];

        for (int i = 0; i < topUsuarios.size(); i++) {
            Usuario u = topUsuarios.get(i);


            boolean jogadorLogado = u.getId().equals(usuarioLogado.getId());

            int pontuacaoExibicao = u.getEstat().getPontuacao();

            dados[i] = new RankingScreen.EntradaRanking(i + 1, u.getNome(), pontuacaoExibicao, jogadorLogado);
        }

        return dados;
    }

    public RankingScreen.EntradaRanking gerarEntradaJogadorLogado(Usuario usuarioLogado) {
        int pontuacaoExibicao = usuarioLogado.getEstat().getPontuacao();

        int posicaoDoJogador = u.buscarPosicaoJogador(pontuacaoExibicao);

        int partidasPerdidas = usuarioLogado.getEstat().getPartidasPerdidas();
        int partidasGanhas = usuarioLogado.getEstat().getPartidasGanhas();
        int partidasJogadas = partidasGanhas + partidasPerdidas;

        double taxaDeVitorias = 0.0;
        if (partidasJogadas > 0) {
            taxaDeVitorias = ((double) partidasGanhas / partidasJogadas) * 100.0;
        }

        return new RankingScreen.EntradaRanking(posicaoDoJogador, usuarioLogado.getNome(), pontuacaoExibicao, true, partidasJogadas, taxaDeVitorias);
    }
}
