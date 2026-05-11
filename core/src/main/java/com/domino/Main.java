package com.domino;

import com.badlogic.gdx.Game;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.telas.GameScreen;

import java.util.Scanner;

public class Main extends Game {

    private int modoEscolhido; // 1 para Host, 2 para Cliente
    Scanner scanner = new Scanner(System.in);

    public Main(int modoEscolhido) {
        this.modoEscolhido = modoEscolhido;
    }

    @Override
    public void create() {
        GameScreen telaJogo = new GameScreen();

        try {
            if (modoEscolhido == 1) {
                System.out.println("Iniciando como HOST...");
                Servidor servidor = new Servidor();
                String ip = servidor.obterIPLocal();
                System.out.println("IP do servidor: " + ip);
                Cliente cliente = new Cliente(telaJogo, "localhost");

                telaJogo.setServidor(servidor);
                telaJogo.setCliente(cliente);

            } else if (modoEscolhido == 2) {
                System.out.println("Iniciando como CLIENTE...");
                System.out.println("Digite o IP da sala: ");
                String ip = scanner.next();
                Cliente cliente = new Cliente( telaJogo, ip);
                telaJogo.setCliente(cliente);
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar na rede: " + e.getMessage());
            e.printStackTrace();
        }

        this.setScreen(telaJogo);
    }
}
