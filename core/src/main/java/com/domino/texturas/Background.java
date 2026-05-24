package com.domino.texturas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Background extends Image {
    private final Texture textura;

    public Background(Texture textura) {
        super(textura);
        this.textura = textura;
        // Configuração de GPU para repetição nativa da imagem POT (128x128)
        this.textura.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textura == null || getStage() == null) return;

        var camera = getStage().getCamera();
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        // Deslocamento de corte baseado na coordenada exata da câmera do Stage
        batch.draw(
            textura,
            camera.position.x - camera.viewportWidth / 2f,
            camera.position.y - camera.viewportHeight / 2f,
            camera.viewportWidth,
            camera.viewportHeight,
            (int) (camera.position.x),
            (int) (-camera.position.y),
            (int) camera.viewportWidth,
            (int) camera.viewportHeight,
            false,
            false
        );
    }
}
