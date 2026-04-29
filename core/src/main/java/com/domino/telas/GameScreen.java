package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import com.domino.atores.PecaVisual;
import com.domino.atores.ZonaDeSoltarPeca;
import com.domino.logica.*;
import com.domino.texturas.Background;

//import com.domino.textures.Background;

import java.util.List;

public class GameScreen implements Screen {
    private final Stage stage;
    private final DragAndDrop dragAndDrop;

    private final Tabuleiro tabuleiro = new Tabuleiro();
    private final ZonaDeSoltarPeca alvoEsquerda;
    private final ZonaDeSoltarPeca alvoDireita;

    // Texturas
    Texture texturaZonas;
    Texture texturaPeca_a_a;
    Texture texturaPeca_a_b;
    Texture texturaPeca_b_b;

    public GameScreen() {
        // O FitViewport garante que o jogo não fique esticado se a janela mudar de tamanho
        stage = new Stage(new FitViewport(1920, 1080));
        // 'Stage' é quem vai receber os cliques do mouse
        Gdx.input.setInputProcessor(stage);

        // Coloca o background
        Texture backgroundTextura = new Texture("background.png");
        Background background = new Background(backgroundTextura);
        stage.addActor(background);

        // Inicia texturas
        this.inicilizarTexturas();

        // Prepara as zonas
        alvoEsquerda = new ZonaDeSoltarPeca(false, texturaZonas);
        alvoEsquerda.setPosition((stage.getWidth() / 2) - 200, stage.getHeight() / 2);

        alvoDireita = new ZonaDeSoltarPeca(true, texturaZonas);
        alvoDireita.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);

        stage.addActor(alvoEsquerda);
        stage.addActor(alvoDireita);

        // Lógica do Drag and Drop
        dragAndDrop = new DragAndDrop();

        // Onde pode soltar (target)
        // Na direita (final = true)
        dragAndDrop.addTarget(new DragAndDrop.Target(alvoDireita) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                // O mouse passou por cima. Fica verde só para visualização
                getActor().setColor(Color.WHITE);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                // O mouse saiu de cima.
                getActor().setColor(Color.BLACK);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("Peça solta no lado direito");

                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), true)){
                    // Para debug
                    System.out.println("Compatível");

                    // Move a peça e atualiza a zona
                    pecaSolta.setPosition(alvoDireita.getX(), alvoDireita.getY());
                    alvoDireita.setPosition(alvoDireita.getX() + pecaSolta.getWidth(), alvoDireita.getY());

                    // Atualiza a rotação da peça (atualizada na classe Tabuleiro)
                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();
                } else {
                    System.out.println("Peça incompatível");

                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();

                    float xMouse = source.getActor().getX();
                    float yMouse = source.getActor().getY();

                    pecaSolta.setPosition(xMouse, yMouse);
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));

                }
            }
        });
        // Para a esquerda (final = false)
        dragAndDrop.addTarget(new DragAndDrop.Target(alvoEsquerda) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.WHITE);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.BLACK);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("Peça solta no lado esquerdo");

                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), false)){
                    // Para debug
                    System.out.println("Compatível");

                    // Move a peça e atualiza a zona
                    pecaSolta.setPosition(alvoEsquerda.getX(), alvoEsquerda.getY());
                    alvoEsquerda.setPosition(alvoEsquerda.getX() - pecaSolta.getWidth(), alvoEsquerda.getY());

                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();
                } else {
                    System.out.println("Peça incompatível");

                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();

                    float xMouse = source.getActor().getX();
                    float yMouse = source.getActor().getY();

                    pecaSolta.setPosition(xMouse, yMouse);
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));

                }

            }
        });
        this.inicializarPecas();
    }

    private void inicilizarTexturas(){
        this.texturaZonas = new Texture("libgdx.png");
        this.texturaPeca_a_a = new Texture("peca_a_a.png");
        this.texturaPeca_a_b = new Texture("peca_a_b.png");
        this.texturaPeca_b_b = new Texture("peca_b_b.png");
    }

    private void inicializarPecas(){
        HorizontalGroup pecasNaMao = new  HorizontalGroup();
        pecasNaMao.space(15);
        pecasNaMao.setPosition(stage.getWidth() / 3, 200);
        stage.addActor(pecasNaMao);

        // Processo pode ser otimizado. Isso é uma solução prática para poder testar as conexões rapidamente.
        Peca logicaPeca_a_a = new Peca("A", Tipo.ACIDO, "A", Tipo.ACIDO);
        Peca logicaPeca_a_b = new Peca("A", Tipo.ACIDO, "B", Tipo.BASE);
        Peca logicaPeca_b_b = new Peca("B", Tipo.BASE, "B", Tipo.BASE);

        PecaVisual peca_a_a = new PecaVisual(logicaPeca_a_a, texturaPeca_a_a);
        PecaVisual peca_a_b = new PecaVisual(logicaPeca_a_b, texturaPeca_a_b);
        PecaVisual peca_b_b = new PecaVisual(logicaPeca_b_b, texturaPeca_b_b);


        List<PecaVisual> pecaVisualNaMao = List.of(peca_a_a, peca_a_b, peca_b_b);

        for (PecaVisual pecaVisual : pecaVisualNaMao) {
            pecasNaMao.addActor(pecaVisual);

            // Configura o drag and drop pra cada peça
            dragAndDrop.addSource(new DragAndDrop.Source(pecaVisual) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(pecaVisual);

                    Image fantasma = new Image(pecaVisual.getSprite());
                    fantasma.setSize(100, 200);
                    fantasma.setColor(1, 1, 1, 0.5f);

                    // Centraliza o fantasma no mouse (dependendo de onde você clica na peça)
                    dragAndDrop.setDragActorPosition(fantasma.getWidth()/2, -fantasma.getHeight()/2);
                    payload.setDragActor(fantasma);

                    return payload;
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com uma cor de fundo (cinza escuro)
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

    // Métodos obrigatórios da interface Screen
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
