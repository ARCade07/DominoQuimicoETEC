package com.domino.rede;

import com.badlogic.gdx.Gdx;
import com.domino.rede.packets.PacketJogada;
import com.domino.telas.GameScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;

public class Cliente {
    // instancia o cliente
    Client cliente = new Client();
    private GameScreen gameScreen;
    private String enderecoIP;

    public Cliente (GameScreen tela, String enderecoIP){
        this.gameScreen = tela;
        this.enderecoIP = enderecoIP;




        // inicia a thread do cliente
        cliente.start();



        try{
            cliente.connect(5000, enderecoIP, 54555, 54777);
        } catch (IOException e) {
            System.out.println("Não foi possível se conectar a esse servidor");
            cliente.stop();
            throw new RuntimeException(e);
        }


        // registros das classe que serão serializadas utilizando o kryo
        Registro.registrarClasses(cliente.getKryo());

        cliente.addListener(new Listener(){
            public void received(Connection conexao, Object objeto){
                if(objeto instanceof PacketJogada){
                    final PacketJogada jogada = (PacketJogada) objeto;

                    // acessa a thread principal do lib e envia para ela a alteração no tabuleiro
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            // metodo da tela principal que vai receber a jogada do adversário
                            gameScreen.receberJogadaRede(jogada);
                        }
                    });
                }
            }
        });
    }

    public void enviarJogada(PacketJogada jogada){
        cliente.sendTCP(jogada);
    }

    public void fechar(){
        if(cliente != null) cliente.stop();
    }
}
