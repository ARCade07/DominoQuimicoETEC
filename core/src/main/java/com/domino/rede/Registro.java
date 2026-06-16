package com.domino.rede;

import com.domino.logica.Tipo;
import com.domino.rede.packets.*;
import com.esotericsoftware.kryo.Kryo;

import java.util.ArrayList;
import java.util.HashMap;

public class Registro {
    // registro das classes que serão transferidas pela rede, para saber como traduzir os bytes de volta para objetos
    public static void registrarClasses(Kryo kryo){
        // informações sobre o enum de tipo
        kryo.register(Tipo.class);
        kryo.register(PacketJogada.class);
        kryo.register(PacketQuantidadePecas.class);
        kryo.register(PacketPrimeiroJogador.class);
        kryo.register(PacketPontuacao.class);
        kryo.register(PacketResultadoJogo.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(PacketResultadoJogador.class);
        kryo.register(PacketLobby.class);
        kryo.register(PacketComecarJogo.class);
        kryo.register(PacketVoltaProLobby.class);
    }

}
