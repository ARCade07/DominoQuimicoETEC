package com.domino.telas; // Ajuste para o pacote correto, se necessário

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class ZoomInputHandler extends InputAdapter {

    private final OrthographicCamera camera;
    private final float zoomMinimo;
    private final float zoomMaximo;
    private final float velocidadeZoom;

    public ZoomInputHandler(OrthographicCamera camera) {
        this.camera = camera;
        this.zoomMinimo = 0.5f; // Permite aproximar até 200% do tamanho original
        this.zoomMaximo = 3.0f; // Permite afastar até ver o triplo do cenário
        this.velocidadeZoom = 0.15f; // O quão rápido a câmera responde à roda do mouse
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // amountY > 0 significa scroll para baixo (afastar a câmera / zoom out)
        // amountY < 0 significa scroll para cima (aproximar a câmera / zoom in)

        // Aplica o incremento no zoom atual da câmera
        camera.zoom += amountY * velocidadeZoom;

        // Limita o zoom para que não ultrapasse os limites estabelecidos
        camera.zoom = MathUtils.clamp(camera.zoom, zoomMinimo, zoomMaximo);

        // Atualiza as matrizes matemáticas da câmera do LibGDX
        camera.update();

        // Retorna true para informar ao Multiplexer que consumimos este evento de input
        return true;
    }
}
