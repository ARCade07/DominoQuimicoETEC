package com.domino.rede.packets;

import com.domino.logica.Tipo;
import com.domino.logica.Peca;

public class PacketJogada {
    public String info1;
    public String info2;
    public Tipo tipo1;
    public Tipo tipo2;
    public boolean noFinal;
    public int proximoAJogar;

    public int rotacao;
    public boolean isBucha;

    public void copiarPeca(Peca p){
        this.info1 = p.getInfo1();
        this.info2 = p.getInfo2();
        this.tipo1 = p.getTipo1();
        this.tipo2 = p.getTipo2();
        this.rotacao = p.getRotacao();
        this.isBucha = p.isBucha();
    }
}
