package com.domino.rede;

import com.badlogic.gdx.Gdx;
import com.domino.rede.packets.*;
import com.domino.telas.GameScreen;
import com.domino.telas.StartScreen;
import com.domino.telas.TelaLobby;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.List;

public class Cliente {
    // instancia o cliente
    Client cliente = new Client();
    private GameScreen gameScreen;
    private TelaLobby telaLobby;
    private String enderecoIP;
    public boolean minhaVez = false;
    private List<Integer> jogadoresConectados;

    public Cliente (String enderecoIP){
        this.enderecoIP = enderecoIP;

        // inicia a thread do cliente
        cliente.start();

        // registros das classe que serão serializadas utilizando o kryo
        Registro.registrarClasses(cliente.getKryo());

        cliente.addListener(new Listener(){
            public void received(Connection conexao, Object objeto){
                if(objeto instanceof PacketJogada){
                    final PacketJogada jogada = (PacketJogada) objeto;

                    minhaVez = (jogada.proximoAJogar == conexao.getID());

                    // caso a jogada seja válida
                    if(jogada.info1 != null && gameScreen != null){
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
                if(objeto instanceof PacketPrimeiroJogador){
                    minhaVez = true;
                    System.out.println("Você começa");
                }
                if(objeto instanceof PacketResultadoJogo){
                    PacketResultadoJogo resultado = (PacketResultadoJogo) objeto;

                    for(PacketResultadoJogador resultadoJogador : resultado.resultadoFinal){

                        System.out.println("Jogador: " + resultadoJogador.idJogador + " | Pontuação: " + resultadoJogador.pontuacao);
                    }
                }
                if(objeto instanceof PacketLobby){
                    System.out.println("PACKET RECEBIDO!");

                    PacketLobby packetLobby = (PacketLobby) objeto;

                    jogadoresConectados = packetLobby.idJogadoresConectados;

                    if(telaLobby != null){

                        final List<Integer> idJogadoresConectados = packetLobby.idJogadoresConectados;

                        Gdx.app.postRunnable(() -> {telaLobby.atualizaJogadoresNaTela(idJogadoresConectados);});
                    }
                }
                if(objeto instanceof PacketComecarJogo){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            GameScreen telaJogo = new GameScreen();
                            telaJogo.setCliente(Cliente.this);
                            setGameScreen(telaJogo);
                            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(telaJogo);
                        }
                    });
                }
            }
            @Override
            public void disconnected(Connection conexao){
                System.out.println("Conexão perdida com o servidor");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        StartScreen startScreen = new StartScreen();
                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(startScreen);
                    }
                });
            }
        });

        try{
            cliente.connect(5000, enderecoIP, 54555, 54777);
        } catch (IOException e) {
            System.out.println("Não foi possível se conectar a esse servidor");
            cliente.stop();
            throw new RuntimeException(e);
        }
    }

    public int idCliente(){
        return cliente.getID();
    }

    public void setGameScreen(GameScreen telaJogo){
        this.gameScreen = telaJogo;
    }

    public void setTelaLobby(TelaLobby telaLobby){
        this.telaLobby = telaLobby;
        System.out.println(
            "setTelaLobby chamado"
        );

        // resolve a race condition
        if (jogadoresConectados != null) {
            final List<Integer> idJogadoresConectados = jogadoresConectados;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    telaLobby.atualizaJogadoresNaTela(idJogadoresConectados);
                }
            });
        }
    }

    public void enviarJogada(PacketJogada jogada){
        cliente.sendTCP(jogada);
        minhaVez = false;
    }

    public void enviarQuantidadePecas(PacketQuantidadePecas quantidade){
        cliente.sendTCP(quantidade);
    }

    public void enviarPontuacao(PacketPontuacao pontuacao){
        cliente.sendTCP(pontuacao);
    }

    public void fechar(){
        if(cliente != null) cliente.stop();
    }
}
