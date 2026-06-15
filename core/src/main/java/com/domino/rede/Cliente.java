package com.domino.rede;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.domino.rede.packets.*;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.domino.telas.*;

import java.io.IOException;
import java.util.List;

public class Cliente {
    // instancia o cliente
    Client cliente = new Client();
    private GameScreen gameScreen;
    private LobbyScreen lobbyScreen;
    private String enderecoIP;
    public boolean minhaVez = false;
    private List<Integer> jogadoresConectados;
    private Servidor servidor;
    private int quantidadeDeJogadores;
    public String ipConectado;

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



                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    TelaFimDeJogo fimDeJogo = new TelaFimDeJogo(resultado, conexao.getID(), Cliente.this, servidor);
                                    ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(fimDeJogo);
                                }
                            }, 3.0f);
                        }
                    });
                }
                if(objeto instanceof PacketLobby){
                    System.out.println("PACKET RECEBIDO!");

                    PacketLobby packetLobby = (PacketLobby) objeto;

                    jogadoresConectados = packetLobby.idJogadoresConectados;
                    quantidadeDeJogadores = jogadoresConectados.size();

                    if(lobbyScreen != null){

                        final List<Integer> idJogadoresConectados = packetLobby.idJogadoresConectados;

                        Gdx.app.postRunnable(() -> {
                            lobbyScreen.atualizaJogadoresNaTela(idJogadoresConectados);});
                    }
                }
                if(objeto instanceof PacketComecarJogo){
                    System.out.println("PacketComecarJogo recebido com sucesso");
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
                if(objeto instanceof PacketVoltaProLobby){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            LobbyScreen telaLobby = new LobbyScreen(servidor, Cliente.this, ipConectado);
                            setTelaLobby(telaLobby);
                            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(telaLobby);
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

    public void setServidor (Servidor servidor){
        this.servidor = servidor;
    }

    public void setTelaLobby(LobbyScreen lobbyScreen){
        this.lobbyScreen = lobbyScreen;
        System.out.println(
            "setTelaLobby chamado"
        );

        // resolve a race condition
        if (jogadoresConectados != null) {
            final List<Integer> idJogadoresConectados = jogadoresConectados;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    lobbyScreen.atualizaJogadoresNaTela(idJogadoresConectados);
                }
            });
        }
    }

    public void enviarJogada(PacketJogada jogada){
        cliente.sendTCP(jogada);
        if(quantidadeDeJogadores != 1) minhaVez = false;
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
