package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    private Stage stage;

    public GameScreen() {
        // Cria um palco com uma resolução fixa (ex: 1280x720)
        // O FitViewport garante que o jogo não fique esticado se a janela mudar de tamanho
        stage = new Stage(new FitViewport(1280, 720));

        // Dizemos ao LibGDX que o 'Stage' é quem vai receber os cliques do mouse
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com uma cor de fundo (um cinza escuro, quase quadro-negro)
        ScreenUtils.clear(new Color(0.15f, 0.15f, 0.15f, 1f));

        // Atualiza as animações e desenha todos os atores que estiverem no palco
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza a visualização se a janela mudar de tamanho
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose(); // Libera a memória ao fechar a tela
    }

    // Métodos obrigatórios da interface Screen (podem ficar vazios por enquanto)
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
