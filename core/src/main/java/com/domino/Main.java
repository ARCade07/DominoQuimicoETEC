package com.domino;

import com.badlogic.gdx.Game;
import com.domino.telas.GameScreen;
import com.domino.telas.LoginScreen;

public class Main extends Game {
    @Override
    public void create() {
        this.setScreen(new LoginScreen());
    }
}
