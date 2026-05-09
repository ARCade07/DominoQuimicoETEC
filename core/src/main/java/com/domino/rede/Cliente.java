package com.domino.rede;

import com.domino.telas.GameScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;

public class Cliente {
    // instancia o cliente
    Client cliente = new Client();
    private GameScreen gameScreen;

    public Cliente (GameScreen tela){
        this.gameScreen = tela;

        // inicia a thread do cliente
        cliente.start();

        //procura se há algum servidor aberto na mesma rede LAN
        InetAddress endereco = cliente.discoverHost(54777, 5000);

        if(endereco != null){
            try {
                // tenta se conectar no servidor encontrado
                cliente.connect(5000, endereco, 54555, 54777);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Nenhum servidor foi encontrado!");
            // para a execução do cliente
            cliente.stop();
        }

        // registros das classe que serão serializadas utilizando o kryo
        Registro.registrarClasses(cliente.getKryo());

        cliente.addListener(new Listener(){

        });
    }

    public void fechar(){
        if(cliente != null) cliente.stop();
    }
}
