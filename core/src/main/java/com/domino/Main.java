package com.domino;

import com.badlogic.gdx.Game;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.telas.GameScreen;

public class Main extends Game {

    private int modoEscolhido; // 1 para Host, 2 para Cliente

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
                Cliente cliente = new Cliente(telaJogo);

                telaJogo.setServidor(servidor);
                telaJogo.setCliente(cliente);

            } else if (modoEscolhido == 2) {
                System.out.println("Iniciando como CLIENTE...");
                Cliente cliente = new Cliente( telaJogo);
                telaJogo.setCliente(cliente);
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar na rede: " + e.getMessage());
            e.printStackTrace();
        }

        // Coloca a tela na janela do jogo
        this.setScreen(telaJogo);
    }
}
