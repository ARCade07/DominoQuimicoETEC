package com.domino;

import com.badlogic.gdx.Game;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.telas.GameScreen;
import com.domino.telas.Estilos;
import com.domino.telas.LoginScreen;
import com.domino.telas.StartScreen;

import java.util.Scanner;

public class Main extends Game {

    @Override
    public void create() {

        Estilos.inicializar();
        this.setScreen(new StartScreen());
    }
    @Override
    public void dispose() {
        super.dispose();
        Estilos.dispose();
    }
}
