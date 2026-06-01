package com.domino.rede;

import com.domino.rede.packets.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class Servidor {
    private Server servidor;
    private List<Integer> jogadoresConectados = new ArrayList<>();
    private Map<Integer, Integer> pontuacaoFinalJogadores = new HashMap<>();

    public  Servidor () throws IOException {
        // instancia o servidor
        servidor = new Server();
        // inicia a thread do servidor e coloca ele para escutar nas respectivas portas
        servidor.start();

        // registros das classe que serão serializadas utilizando o kryo
        Registro.registrarClasses(servidor.getKryo());

        servidor.bind(54555, 54777);



        servidor.addListener(new Listener(){
            @Override
            public void connected(Connection connection){
                jogadoresConectados.add(connection.getID());
                System.out.println("Jogador " + connection.getID() + " conectou!");
                PacketEntrouJogador packetEntrouJogador = new PacketEntrouJogador();
                packetEntrouJogador.quantidadeJogadores = jogadoresConectados.size();
                System.out.println("Enviando PacketEntrouJogador " + packetEntrouJogador.quantidadeJogadores);
                servidor.sendToAllTCP(packetEntrouJogador);
            }
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketJogada) {
                    PacketJogada jogada = (PacketJogada) object;

                    if(!jogada.ultimaJogada){
                        // pega quem enviou a jogada
                        int jogadorAtual = jogadoresConectados.indexOf(connection.getID());
                        // calcula, através do array de jogadoresConectados, quem será o próximo a jogar
                        int proximoAJogar = (jogadorAtual + 1) % jogadoresConectados.size();
                        int idProximoAJogar = jogadoresConectados.get(proximoAJogar);
                        // envia pela rede o  id do próximo jogador
                        jogada.proximoAJogar = idProximoAJogar;
                    }
                    else jogada.proximoAJogar = -1;

                    // envia a jogada para todos os jogadores, menos o que enviou
                    servidor.sendToAllExceptTCP(connection.getID(), jogada);
                }

                if(object instanceof PacketQuantidadePecas){
                    PacketQuantidadePecas quantidadePecas = (PacketQuantidadePecas) object;

                    int quemEnviou = connection.getID();

                    quantidadePecas.jogador = quemEnviou;
                    servidor.sendToAllExceptTCP(quemEnviou, quantidadePecas);
                }

                if(object instanceof PacketPontuacao){
                    PacketPontuacao packetPontuacao = (PacketPontuacao) object;
                    int pontuacaoFinal = packetPontuacao.pontuacao;
                    int idJogador = connection.getID();

                    pontuacaoFinalJogadores.put(idJogador, pontuacaoFinal);

                    if(pontuacaoFinalJogadores.size() == jogadoresConectados.size()){
                        // utiliza entry para acessar o mapa como um conjunto de pares para poder ordenar depois
                        List<Map.Entry<Integer, Integer>> pontuacaoFinalJogadoresOrdenada = new ArrayList<>(pontuacaoFinalJogadores.entrySet());

                        pontuacaoFinalJogadoresOrdenada.sort(Map.Entry.<Integer,Integer>comparingByValue().reversed());

                        PacketResultadoJogo packetResultadoJogo = new PacketResultadoJogo();
                        for(Map.Entry<Integer, Integer> entry : pontuacaoFinalJogadoresOrdenada){

                            PacketResultadoJogador resultado = new PacketResultadoJogador(entry.getKey(),entry.getValue());

                            packetResultadoJogo.resultadoFinal.add(resultado);
                        }

                        servidor.sendToAllTCP(packetResultadoJogo);
                    }
                }
            }
        });
    }

    public String obterIPLocal (){
        try {
            // endereço IP da máquina local
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println("Erro ao obter seu IP : " + e.getMessage());
            return "FALHA";
        }
    }

    public void removerJogador(int idJogador){
        for(Connection conexao : servidor.getConnections()){
            if(conexao.getID() == idJogador){
                conexao.close();
                break;
            }
        }
    }

    public void botaoInicioClicado(int idHost){
        PacketComecarJogo packetComecarJogo = new PacketComecarJogo();
        servidor.sendToAllExceptTCP(idHost, packetComecarJogo);
    }

    public void decidirQuemComeca(){
        Random random = new Random();

        int indiceAleatorio = random.nextInt(jogadoresConectados.size());
        int quemComeca = jogadoresConectados.get(indiceAleatorio);

        PacketPrimeiroJogador primeiroJogador = new PacketPrimeiroJogador();


        servidor.sendToTCP(quemComeca, primeiroJogador);
    }

    public void fechar() {
        if (servidor != null) servidor.stop();
    }
}
