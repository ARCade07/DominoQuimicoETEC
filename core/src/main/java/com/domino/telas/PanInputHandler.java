package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;

public class PanInputHandler extends InputAdapter {
    private final Camera camera;
    private float lastX, lastY;
    private boolean isPanning = false;

    public PanInputHandler(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Ativa o Pan se for o botão direito do mouse ou o segundo dedo (mobile)
        if (button == Input.Buttons.RIGHT || pointer == 1) {
            lastX = screenX;
            lastY = screenY;
            isPanning = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isPanning) return false;

        // Ignora caso seja o dedo principal e o botão direito não esteja pressionado
        if (pointer == 0 && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            return false;
        }

        // Calcula o deslocamento mantendo a proporção correta independentemente do tamanho da janela
        float deltaX = (lastX - screenX) * (camera.viewportWidth / Gdx.graphics.getWidth());
        float deltaY = (screenY - lastY) * (camera.viewportHeight / Gdx.graphics.getHeight());

        camera.translate(deltaX, deltaY, 0);
        camera.update();

        lastX = screenX;
        lastY = screenY;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT || pointer == 1) {
            isPanning = false;
            return true;
        }
        return false;
    }
}
