package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public abstract class BaseScreen implements Screen {
    protected Stage stage;

    public BaseScreen() {
        // Inicializa o Stage apenas uma vez
        stage = new Stage(new ExtendViewport(1920, 1080));
    }

    @Override
    public void show() {
        // Garante que a tela receba os inputs ao ser exibida
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Cor de fundo padrão (pode ser sobrescrita nas filhas, se necessário)
        ScreenUtils.clear(Color.valueOf("0D0202"));

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
