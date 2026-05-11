package com.domino.rede;

import com.domino.rede.packets.PacketJogada;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class Servidor {
    private Server servidor;

    public  Servidor () throws IOException {
        // instancia o servidor
        servidor = new Server();
        // inicia a thread do servidor e coloca ele para escutar nas respectivas portas
        servidor.start();
        servidor.bind(54555, 54777);

        // registros das classe que serão serializadas utilizando o kryo
        Registro.registrarClasses(servidor.getKryo());

        servidor.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketJogada) {
                    PacketJogada jogada = (PacketJogada) object;


                    // envia a jogada para todos os jogadores, menos o que enviou
                    servidor.sendToAllExceptTCP(connection.getID(), jogada);
                }
            }
        });
    }

    public void fechar() {
        if (servidor != null) servidor.stop();
    }
}
