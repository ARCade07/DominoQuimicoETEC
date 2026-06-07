package com.domino.atores;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.domino.logica.Direcao;

public class ZonaDeSoltarPeca extends Image {
    private final boolean isLadoDireito;  // boolean 'final'

    public Direcao direcao;

    public ZonaDeSoltarPeca(boolean isLadoDireito) {
        this.isLadoDireito = isLadoDireito;
        this.setSize(220, 320); // Um pouco maior que a peça
        if (isLadoDireito) this.direcao = Direcao.NORMAL;
        else this.direcao = Direcao.INVERTIDO;
    }

    public boolean isLadoDireito() {
        return isLadoDireito;
    }
}
