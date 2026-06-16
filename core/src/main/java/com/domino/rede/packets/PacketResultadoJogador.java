package com.domino.rede.packets;

public class PacketResultadoJogador {
    public int idJogador;
    public int pontuacao;

    public PacketResultadoJogador() {
    }

    public PacketResultadoJogador(int idJogador, int pontuacao) {
        this.idJogador = idJogador;
        this.pontuacao = pontuacao;
    }

}
