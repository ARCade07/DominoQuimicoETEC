package com.domino.atores;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.domino.logica.Peca;

public class PecaVisual extends Image {
    private final Peca pecaLogica;
    private final Sprite sprite;

    public PecaVisual(Peca pecaLogica, Texture textura) {
        // Passa a textura pra desenhar
        super(textura);
        this.pecaLogica = pecaLogica;
        this.sprite = new Sprite(textura);

        // Define um tamanho padrão para a peça (100 de largura por 200 de altura)
        this.setSize(100, 200);
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
    }



    @Override
    public void draw(Batch batch, float parentAlpha){
        if (this.getPecaLogica().isBucha()) this.setRotation(0);

        super.draw(batch, parentAlpha);
    }

    public Peca getPecaLogica() {
        return pecaLogica;
    }
    public Sprite getSprite() {
        return sprite;
    }
}
