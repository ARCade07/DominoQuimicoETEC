package com.domino.texturas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Background extends Image {

    public Background(Texture textura) {
        super(textura); // O LibGDX já desenha a imagem automaticamente! AINDA BEM

        this.setSize(1920,1080);
        this.setOrigin(4000, 1500);
    }
}
