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
import com.domino.logica.Peca;
import com.domino.logica.Tabuleiro;
import com.domino.logica.Tipo;

import java.util.Arrays;
import java.util.List;

public class GameScreen implements Screen {
    private final Stage stage;
    private final DragAndDrop dragAndDrop;
    private final Texture texturaTeste;

    private final Tabuleiro tabuleiro = new Tabuleiro();

    public GameScreen() {
        // Cria um palco com uma resolução fixa (1280x720)
        // O FitViewport garante que o jogo não fique esticado se a janela mudar de tamanho
        stage = new Stage(new FitViewport(1280, 720));
        // 'Stage' é quem vai receber os cliques do mouse
        Gdx.input.setInputProcessor(stage);

        texturaTeste = new Texture("libgdx.png");

        // Prepara o tabuleiro visual (alvos)
        ZonaDeSoltarPeca alvoEsquerda = new ZonaDeSoltarPeca(false, texturaTeste);
        alvoEsquerda.setPosition(300, 250); // Posição X e Y na tela

        ZonaDeSoltarPeca alvoDireita = new ZonaDeSoltarPeca(true, texturaTeste);
        alvoDireita.setPosition(800, 250);

        stage.addActor(alvoEsquerda);
        stage.addActor(alvoDireita);

        // Prepara a peça (fonte)
        Peca cerebroTeste = new Peca("HCl", Tipo.ACIDO, "NaOH", Tipo.BASE);
        PecaVisual pecaNaMao = new PecaVisual(cerebroTeste, texturaTeste);
        pecaNaMao.setPosition(550, 50); // Fica na parte de baixo da tela

        stage.addActor(pecaNaMao);

        // Lógica do Drag and Drop
        dragAndDrop = new DragAndDrop();

        // Configura QUEM pode ser arrastado (Source)
        dragAndDrop.addSource(new DragAndDrop.Source(pecaNaMao) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(pecaNaMao); // Envia a peça inteira (corpo e cérebro)

                // Cria a imagem fantasma que segue o mouse
                Image fantasma = new Image(texturaTeste);
                fantasma.setSize(100, 200);
                fantasma.setColor(1, 1, 1, 0.5f); // 50% transparente
                dragAndDrop.setDragActorPosition(50, -100); // Centraliza no mouse
                payload.setDragActor(fantasma);

                return payload;
            }
        });

        // Onde pode soltar (target)
        // Na direita (final = true)
        dragAndDrop.addTarget(new DragAndDrop.Target(alvoDireita) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                // O mouse passou por cima. Fica verde só para visualização
                getActor().setColor(Color.GREEN);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                // O mouse saiu de cima.
                getActor().setColor(Color.FIREBRICK);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                // Ao soltar a peça
                System.out.println("O jogador soltou a peça no Lado Direito.");
                // Pega a peça que estava voando no mouse
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), true)){
                    // Move ela fisicamente para o centro do alvo
                    pecaSolta.setPosition(alvoDireita.getX() + 10, alvoDireita.getY() + 10);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();
                } else {
                    System.out.println("Peça incompatível");

                    // 1. Salva a posição original (base) de onde a peça saiu
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();

                    // 2. Calcula a posição global do mouse na tela
                    // getActor() pega o próprio alvo que sofreu o drop
                    float mouseNaTelaX = getActor().getX() + x;
                    float mouseNaTelaY = getActor().getY() + y;

                    // 3. Joga a peça para a posição do mouse e a faz deslizar de volta
                    pecaSolta.setPosition(mouseNaTelaX, mouseNaTelaY);
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));
                }

            }
        });
        // Para a esquerda (final = false)
        dragAndDrop.addTarget(new DragAndDrop.Target(alvoEsquerda) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.GREEN);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.FIREBRICK);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("O jogador soltou a peça no Lado Esquerdo.");
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), false)){

                    pecaSolta.setPosition(alvoEsquerda.getX() + 10, alvoEsquerda.getY() + 10);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                } else {
                    System.out.println("Peça incompatível");

                    // Posição original (base) de onde a peça saiu
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();

                    // Calcula a posição global do mouse na tela
                    // getActor() pega o próprio alvo que sofreu o drop
                    float mouseNaTelaX = getActor().getX() + x;
                    float mouseNaTelaY = getActor().getY() + y;

                    // Joga a peça para a posição do mouse e a faz deslizar de volta
                    pecaSolta.setPosition(mouseNaTelaX, mouseNaTelaY);
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));
                }
            }
        });

        HorizontalGroup pecasNaMao = new  HorizontalGroup();
        pecasNaMao.space(15);
        pecasNaMao.setPosition(stage.getWidth() / 2, 250 - 50);
        stage.addActor(pecasNaMao);

        List<Peca> pecasTeste = Arrays.asList(
            new Peca("HCl", Tipo.ACIDO, "NaOH", Tipo.BASE),   // Peça normal [Ácido | Base]
            new Peca("HCl", Tipo.ACIDO, "NaOH", Tipo.BASE),   // Peça normal [Ácido | Base]
            new Peca("H2SO4", Tipo.ACIDO, "HNO3", Tipo.ACIDO),// BUCHA [Ácido | Ácido] (Deve ficar de pé)
            new Peca("KOH", Tipo.BASE, "LiOH", Tipo.BASE)     // BUCHA [Base | Base]
        );
        for (Peca cerebro : pecasTeste) {
            PecaVisual pecaVisual = new PecaVisual(cerebro, texturaTeste);

            pecasNaMao.addActor(pecaVisual);

            // Configura o drag and drop pra cada peça
            dragAndDrop.addSource(new DragAndDrop.Source(pecaVisual) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(pecaVisual);

                    Image fantasma = new Image(texturaTeste);
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
        texturaTeste.dispose();
    }

    // Métodos obrigatórios da interface Screen
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
