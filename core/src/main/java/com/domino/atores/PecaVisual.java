package com.domino.atores;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import com.domino.logica.Peca;
import com.domino.telas.Estilos;

public class PecaVisual extends Group {
    private final Peca pecaLogica;
    private final Texture textura;

    public PecaVisual(Peca pecaLogica, Texture texturaBase) {
        this.pecaLogica = pecaLogica;
        this.textura = texturaBase;

        // Define um tamanho padrão para a peça (100 de largura por 200 de altura)
        this.setSize(100, 200);
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);

        // Fundo da peça
        Image fundo = new Image(texturaBase);
        fundo.setSize(100, 200);
        this.addActor(fundo);

        // Textos
        Label.LabelStyle estiloTexto = Estilos.estiloTextoPeca;

        Label lblInfo1 = new Label(pecaLogica.getInfo1(), estiloTexto);
        lblInfo1.setSize(100, 100);
        lblInfo1.setPosition(0, 100);
        lblInfo1.setAlignment(Align.center);

        Label lblInfo2 = new Label(pecaLogica.getInfo2(), estiloTexto);
        lblInfo2.setSize(100, 100);
        lblInfo2.setPosition(0, 0);
        lblInfo2.setAlignment(Align.center);

        lblInfo1.setFontScale(0.3f);
        lblInfo1.setWrap(true);
        this.addActor(lblInfo1);
        lblInfo2.setFontScale(0.3f);
        lblInfo2.setWrap(true);
        this.addActor(lblInfo2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        if (this.getPecaLogica().isBucha()) this.setRotation(0);

        super.draw(batch, parentAlpha);
    }

    public Peca getPecaLogica() {
        return pecaLogica;
    }

    public Texture getTextura() {
        return textura;
    }
}
