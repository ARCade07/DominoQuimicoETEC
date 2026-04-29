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
        super(textura); // O LibGDX já desenha a imagem automaticamente! AINDA BEM
        this.pecaLogica = pecaLogica;
        this.sprite = new Sprite(textura);

        // Define um tamanho padrão para a peça (100 de largura por 200 de altura)
        this.setSize(100, 200);
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
    }


    //TODO: possível conflito de rotação
    @Override
    public void draw(Batch batch, float parentAlpha){
        if (this.pecaLogica.getInfo1().equals(this.pecaLogica.getInfo2())){
            // Bucha
            this.setRotation(0);
        }
        else if (pecaLogica.isLado2Ocupado() && !pecaLogica.isLado1Ocupado()){
            this.setRotation(90);
        }
        else {
            this.setRotation(0);
        }

        super.draw(batch, parentAlpha);
    }

    public Peca getPecaLogica() {
        return pecaLogica;
    }
    public Sprite getSprite() {
        return sprite;
    }
}
