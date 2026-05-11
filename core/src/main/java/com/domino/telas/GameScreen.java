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
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.rede.packets.PacketJogada;
import com.domino.texturas.Background;

import java.util.List;

public class GameScreen implements Screen {
    private final Stage stage;
    private final DragAndDrop dragAndDrop;

    private final Tabuleiro tabuleiro = new Tabuleiro();
    private final ZonaDeSoltarPeca alvoEsquerda;
    private final ZonaDeSoltarPeca alvoDireita;

    private float yOriginalAlvoEsquerda;
    private float yOriginalAlvoDireita;

    // Texturas
    private Texture texturaZonas;
    private Texture texturaPeca_a_a;
    private Texture texturaPeca_a_b;
    private Texture texturaPeca_b_b;

    private Cliente cliente;
    private Servidor servidor;

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
        yOriginalAlvoEsquerda = (stage.getHeight() / 2) - (alvoEsquerda.getHeight() / 3);
        alvoEsquerda.setPosition((stage.getWidth() / 2) - 200, yOriginalAlvoEsquerda);

        alvoDireita = new ZonaDeSoltarPeca(true, texturaZonas);
        yOriginalAlvoDireita = (stage.getHeight() / 2) - (alvoDireita.getHeight() / 3);
        alvoDireita.setPosition(stage.getWidth() / 2, yOriginalAlvoDireita);

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

                    // Tira a peça do HorizontalGroup e coloca em Stage para poder trocar coordenadas sem conflito
                    stage.addActor(pecaSolta);

                    // Atualiza rotação da peça (atualizada na classe Tabuleiro)
                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());

                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    // Se a peça estiver deitada, perde metade da altura e ganha metade da largura (100 x 200)
                    final float larguraVisual = estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
                    final float deslocamentoX = estaDeitada ? (pecaSolta.getWidth() / 2f) : 0;
                    final float deslocamentoY = estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);

                    // Atualiza o y da zona rapidamente para ajustar o y da peça solta
                    alvoDireita.setPosition(alvoDireita.getX(), yOriginalAlvoDireita);

                    // Move a peça
                    pecaSolta.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY);

                    // Atualiza a zona
                    //if (tabuleiro.primeiraJogada()){
                        // Se for a primeira jogada, atualiza a outra zona também
                        //alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, alvoDireita.getY());
                    //}
                    alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, yOriginalAlvoDireita - (alvoDireita.getHeight() / 3));

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    // pega a peça que foi colocada pelo cliente no tabuleiro
                    if (cliente != null) {
                        PacketJogada pacote = new PacketJogada();
                        pacote.info1 = (String) pecaSolta.getPecaLogica().getInfo1();
                        pacote.info2 = (String) pecaSolta.getPecaLogica().getInfo2();
                        pacote.tipo1 = pecaSolta.getPecaLogica().getTipo1();
                        pacote.tipo2 = pecaSolta.getPecaLogica().getTipo2();
                        pacote.noFinal = true;

                        cliente.enviarJogada(pacote);
                    }
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

                    // Tira a peça do HorizontalGroup e coloca em Stage para poder trocar coordenadas sem conflito
                    stage.addActor(pecaSolta);

                    // Atualiza rotação
                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());

                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    // Se a peça estiver deitada, perde metade da altura e ganha metade da largura (100 x 200)
                    final float larguraVisual = estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
                    final float deslocamentoX = estaDeitada ? (pecaSolta.getWidth() / 2f) : pecaSolta.getWidth();
                    final float deslocamentoY = estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);

                    // Atualiza o y da zona rapidamente para ajustar o y da peça solta
                    alvoEsquerda.setPosition(alvoEsquerda.getX(), yOriginalAlvoEsquerda);

                    // Move a peça
                    pecaSolta.setPosition(alvoEsquerda.getX() + deslocamentoX, alvoEsquerda.getY() + deslocamentoY);

                    // Atualiza a zona
                    //if (tabuleiro.primeiraJogada()){
                    // Se for a primeira jogada, atualiza a outra zona também
                    //alvoDireita.setPosition(alvoDireita.getX() - larguraVisual, alvoEsquerda.getY());
                    //}
                    alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, yOriginalAlvoEsquerda - (alvoEsquerda.getHeight() / 3));


                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    //pega a peça que foi colocada pelo cliente no tabuleiro
                    if (cliente != null) {
                        PacketJogada pacote = new PacketJogada();
                        pacote.info1 = (String) pecaSolta.getPecaLogica().getInfo1();
                        pacote.info2 = (String) pecaSolta.getPecaLogica().getInfo2();
                        pacote.tipo1 = pecaSolta.getPecaLogica().getTipo1();
                        pacote.tipo2 = pecaSolta.getPecaLogica().getTipo2();
                        pacote.noFinal = false;

                        cliente.enviarJogada(pacote);
                    }
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
        pecasNaMao.setPosition(stage.getWidth() / 4, 125);
        stage.addActor(pecasNaMao);

        // Processo pode ser otimizado. Isso é uma solução prática para poder testar as conexões rapidamente.
        Peca logicaPeca_a_a = new Peca("A", Tipo.ACIDO, "A", Tipo.ACIDO);
        Peca logicaPeca_a_b = new Peca("A", Tipo.ACIDO, "B", Tipo.BASE);
        Peca logicaPeca_b_b = new Peca("B", Tipo.BASE, "B", Tipo.BASE);

        PecaVisual peca_a_a = new PecaVisual(logicaPeca_a_a, texturaPeca_a_a);
        PecaVisual peca_a_b = new PecaVisual(logicaPeca_a_b, texturaPeca_a_b);
        PecaVisual peca_b_b = new PecaVisual(logicaPeca_b_b, texturaPeca_b_b);

        Peca logicaPeca_a_b2 = new Peca("A", Tipo.ACIDO, "B", Tipo.BASE);
        PecaVisual peca_a_b2 = new PecaVisual(logicaPeca_a_b2, texturaPeca_a_b);

        Peca logicaPeca_a_a2 = new Peca("A", Tipo.ACIDO, "A", Tipo.ACIDO);
        PecaVisual peca_a_a2 = new PecaVisual(logicaPeca_a_a2, texturaPeca_a_a);

        Peca logicaPeca_a_a3 = new Peca("A", Tipo.ACIDO, "A", Tipo.ACIDO);
        PecaVisual peca_a_a3 = new PecaVisual(logicaPeca_a_a3, texturaPeca_a_a);


        List<PecaVisual> pecaVisualNaMao = List.of(peca_a_a, peca_a_b, peca_b_b, peca_a_a2, peca_a_b2, peca_a_a3);


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

    // pega a textura da peça que foi jogada pelo oponente
    private Texture getTextura(String info1, String info2) {
        // caso a peça seja uma bucha
        if (info1.equals("A") && info2.equals("A")) return texturaPeca_a_a;
        if (info1.equals("B") && info2.equals("B")) return texturaPeca_b_b;

        // caso mão seja uma bucha
        if ((info1.equals("A") && info2.equals("B")) || (info1.equals("B") && info2.equals("A"))) {
            return texturaPeca_a_b;
        }

        return null;
    }

    public void receberJogadaRede(PacketJogada jogada) {
        Peca pecaAdversario = new Peca(jogada.info1, jogada.tipo1, jogada.info2, jogada.tipo2);

        if (tabuleiro.colocarPeca(pecaAdversario, jogada.noFinal)) {

            Texture textura = getTextura(jogada.info1, jogada.info2);
            PecaVisual visualAdversario = new PecaVisual(pecaAdversario, textura);

            stage.addActor(visualAdversario);
            visualAdversario.setRotation(pecaAdversario.getRotacao());

            // dimensionamento
            final boolean estaDeitada = visualAdversario.getRotation() == 90 || visualAdversario.getRotation() == -90;
            final float larguraVisual = estaDeitada ? visualAdversario.getHeight() : visualAdversario.getWidth();

            // posicionamento da peça e da zona
            if (jogada.noFinal) {
                final float deslocamentoX = estaDeitada ? (visualAdversario.getWidth() / 2f) : 0;
                final float deslocamentoY = estaDeitada ? -(visualAdversario.getWidth() / 2f) : -(visualAdversario.getHeight() / 4f);

                alvoDireita.setPosition(alvoDireita.getX(), yOriginalAlvoDireita);
                visualAdversario.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY);
                alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, yOriginalAlvoDireita - (alvoDireita.getHeight() / 3));

            } else {
                final float deslocamentoX = estaDeitada ? (visualAdversario.getWidth() / 2f) : visualAdversario.getWidth();
                final float deslocamentoY = estaDeitada ? -(visualAdversario.getWidth() / 2f) : -(visualAdversario.getHeight() / 4f);

                alvoEsquerda.setPosition(alvoEsquerda.getX(), yOriginalAlvoEsquerda);
                visualAdversario.setPosition(alvoEsquerda.getX() + deslocamentoX, alvoEsquerda.getY() + deslocamentoY);
                alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, yOriginalAlvoEsquerda - (alvoEsquerda.getHeight() / 3));
            }
        }
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
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
        if (cliente != null) cliente.fechar();
//        if (servidor != null) servidor.fechar();
        stage.dispose(); // Libera a memória ao fechar a tela
    }

    // Métodos obrigatórios da interface Screen
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
