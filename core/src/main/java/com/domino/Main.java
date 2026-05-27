package com.domino;

import com.badlogic.gdx.Game;
import com.domino.telas.GameScreen;
import com.domino.telas.Estilos;
import com.domino.telas.LoginScreen;
import com.domino.telas.StartScreen;

public class Main extends Game {
    @Override
    public void create() {

        Estilos.inicializar();
        this.setScreen(new LoginScreen());
    }
    @Override
    public void dispose() {
        super.dispose();
        Estilos.dispose();
    }
}
