package com.domino.atores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ZonaDeSoltarPeca extends Image {
    private final boolean isLadoDireito;  // boolean 'final'

    public ZonaDeSoltarPeca(boolean isLadoDireito, Texture textura) {
        super(textura);
        this.isLadoDireito = isLadoDireito;
        this.setSize(220, 320); // Um pouco maior que a peça
    }

    public boolean isLadoDireito() {
        return isLadoDireito;
    }
}
