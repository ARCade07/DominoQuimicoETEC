package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.domino.bd.ConnectionFactory;
import com.domino.dao.PecaDao;
import com.domino.logica.*;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.rede.packets.PacketJogada;
import com.domino.texturas.Background;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    // Separação Arquitetural (Camadas Gráficas)
    private final Stage worldStage;
    private final Stage hudStage;

    // Gerenciador de Input e Interações Visuais
    private DragAndDrop dragAndDrop;

    // Elementos de Regra de Negócio e Infraestrutura
    private final Tabuleiro tabuleiro = new Tabuleiro();
    private ZonaDeSoltarPeca alvoEsquerda;
    private ZonaDeSoltarPeca alvoDireita;

    private float yOriginalAlvoEsquerda;
    private float yOriginalAlvoDireita;

    // Recursos Gráficos e Gerenciamento de Assets
    private Texture texturaZonas;
    private Texture texturaBase;
    private BitmapFont fontePadrao;
    private Background background;

    // Camada de Comunicação em Rede
    private Cliente cliente;
    private Servidor servidor;

    public GameScreen() {
        // 1. Inicializa camadas isoladas com Viewports fixos
        worldStage = new Stage(new FitViewport(1920, 1080));
        hudStage = new Stage(new FitViewport(1920, 1080));

        // 2. Orquestração e Hierarquia de Entrada de Dispositivos (Inputs)
        InputMultiplexer multiplexer = new InputMultiplexer();
        OrthographicCamera cameraMundo = (OrthographicCamera) worldStage.getCamera();

        multiplexer.addProcessor(hudStage);                  // Interface (Mão do jogador) consome clicks primeiro
        multiplexer.addProcessor(new ZoomInputHandler(cameraMundo)); // Gerenciador isolado de escala (Roda do Mouse)
        multiplexer.addProcessor(new PanInputHandler(cameraMundo));  // Gerenciador isolado de translação (Arrasto do mapa)
        multiplexer.addProcessor(worldStage);                // Elementos do tabuleiro recebem inputs por último
        Gdx.input.setInputProcessor(multiplexer);

        // 3. Montagem e Alinhamento Centralizado do Cenário Cinematográfico
        this.background = new Background();

        float centroMundoX = worldStage.getWidth() / 2f;
        float centroMundoY = worldStage.getHeight() / 2f;

        this.background.setPosition(
            centroMundoX - (this.background.getWidth() / 2f),
            centroMundoY - (this.background.getHeight() / 2f)
        );
        worldStage.addActor(this.background);

        // Inicialização de texturas dependentes
        this.inicilizarTexturas();

        // Construção das Zonas de Interação de Soltura
        alvoEsquerda = new ZonaDeSoltarPeca(false, texturaZonas);
        yOriginalAlvoEsquerda = (worldStage.getHeight() / 2) - (alvoEsquerda.getHeight() / 3);
        alvoEsquerda.setPosition((worldStage.getWidth() / 2) - 200, yOriginalAlvoEsquerda);

        alvoDireita = new ZonaDeSoltarPeca(true, texturaZonas);
        yOriginalAlvoDireita = (worldStage.getHeight() / 2) - (alvoDireita.getHeight() / 3);
        alvoDireita.setPosition(worldStage.getWidth() / 2, yOriginalAlvoDireita);

        worldStage.addActor(alvoEsquerda);
        worldStage.addActor(alvoDireita);

        // 4. Lógica e Engenharia de Drag and Drop
        dragAndDrop = new DragAndDrop();

        dragAndDrop.addTarget(new DragAndDrop.Target(alvoDireita) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.WHITE);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE); // Mantém o canal Alpha intacto para evitar escurecimento definitivo
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), true)){
                    worldStage.addActor(pecaSolta);

                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());
                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    final float larguraVisual = estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
                    final float deslocamentoX = estaDeitada ? (pecaSolta.getWidth() / 2f) : 0;
                    final float deslocamentoY = estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);

                    alvoDireita.setPosition(alvoDireita.getX(), yOriginalAlvoDireita);
                    pecaSolta.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY);
                    alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, yOriginalAlvoDireita - (alvoDireita.getHeight() / 3));

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    if (cliente != null) {
                        PacketJogada pacote = new PacketJogada();
                        pacote.copiarPeca(pecaSolta.getPecaLogica());
                        pacote.noFinal = true;
                        cliente.enviarJogada(pacote);
                    }
                } else {
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));
                }
            }
        });

        dragAndDrop.addTarget(new DragAndDrop.Target(alvoEsquerda) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.WHITE);
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), false)){
                    worldStage.addActor(pecaSolta);

                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());
                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    final float larguraVisual = estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
                    final float deslocamentoX = estaDeitada ? (pecaSolta.getWidth() / 2f) : pecaSolta.getWidth();
                    final float deslocamentoY = estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);

                    alvoEsquerda.setPosition(alvoEsquerda.getX(), yOriginalAlvoEsquerda);
                    pecaSolta.setPosition(alvoEsquerda.getX() + deslocamentoX, alvoEsquerda.getY() + deslocamentoY);
                    alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, yOriginalAlvoEsquerda - (alvoEsquerda.getHeight() / 3));

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    if (cliente != null) {
                        PacketJogada pacote = new PacketJogada();
                        pacote.copiarPeca(pecaSolta.getPecaLogica());
                        pacote.noFinal = false;
                        cliente.enviarJogada(pacote);
                    }
                } else {
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));
                }
            }
        });

        this.inicializarPecas();
    }

    private void inicilizarTexturas(){
        this.texturaZonas = ZonaDeSoltarPeca.criarTextura();
        this.texturaBase = new Texture("pecafinal.png");
        this.fontePadrao = new BitmapFont();
    }

    private void inicializarPecas(){
        HorizontalGroup pecasNaMao = new HorizontalGroup();
        pecasNaMao.space(15);
        pecasNaMao.setPosition(hudStage.getWidth() / 4, 125);
        hudStage.addActor(pecasNaMao);

        List<PecaVisual> pecaVisualNaMao = new ArrayList<>();
        PecaDao p = new PecaDao(new ConnectionFactory());
        List<Peca> pecasLogicasNaMao = p.buscarPecasAleatorias(7);

        for (Peca pecaNaMao : pecasLogicasNaMao){
            PecaVisual pecaVisual = new PecaVisual(pecaNaMao, texturaBase, fontePadrao);
            pecaVisualNaMao.add(pecaVisual);
        }

        for (PecaVisual pecaVisual : pecaVisualNaMao) {
            pecasNaMao.addActor(pecaVisual);

            dragAndDrop.addSource(new DragAndDrop.Source(pecaVisual) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    if(cliente != null && !cliente.minhaVez){
                        System.out.println("Vez de adversário");
                        return null;
                    }

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(pecaVisual);

                    Image fantasma = new Image(texturaBase);
                    fantasma.setSize(100, 200);
                    fantasma.setColor(1, 1, 1, 0.5f);

                    dragAndDrop.setDragActorPosition(fantasma.getWidth()/2, -fantasma.getHeight()/2);
                    payload.setDragActor(fantasma);

                    return payload;
                }
            });
        }
    }

    public void receberJogadaRede(PacketJogada jogada) {
        Peca pecaAdversario = new Peca(jogada.info1, jogada.tipo1, jogada.info2, jogada.tipo2);

        if (tabuleiro.colocarPeca(pecaAdversario, jogada.noFinal)) {
            PecaVisual pecaVisualAdversario = new PecaVisual(pecaAdversario, texturaBase, fontePadrao);

            worldStage.addActor(pecaVisualAdversario);
            pecaVisualAdversario.setRotation(pecaVisualAdversario.getPecaLogica().getRotacao());

            final boolean estaDeitada = pecaVisualAdversario.getRotation() == 90 || pecaVisualAdversario.getRotation() == -90;
            final float larguraVisual = estaDeitada ? pecaVisualAdversario.getHeight() : pecaVisualAdversario.getWidth();

            if (jogada.noFinal) {
                final float deslocamentoX = estaDeitada ? (pecaVisualAdversario.getWidth() / 2f) : 0;
                final float deslocamentoY = estaDeitada ? -(pecaVisualAdversario.getWidth() / 2f) : -(pecaVisualAdversario.getHeight() / 4f);

                alvoDireita.setPosition(alvoDireita.getX(), yOriginalAlvoDireita);
                pecaVisualAdversario.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY);
                alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, yOriginalAlvoDireita - (alvoDireita.getHeight() / 3));

            } else {
                final float deslocamentoX = estaDeitada ? (pecaVisualAdversario.getWidth() / 2f) : pecaVisualAdversario.getWidth();
                final float deslocamentoY = estaDeitada ? -(pecaVisualAdversario.getWidth() / 2f) : -(pecaVisualAdversario.getHeight() / 4f);

                alvoEsquerda.setPosition(alvoEsquerda.getX(), yOriginalAlvoEsquerda);
                pecaVisualAdversario.setPosition(alvoEsquerda.getX() + deslocamentoX, alvoEsquerda.getY() + deslocamentoY);
                alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, yOriginalAlvoEsquerda - (alvoEsquerda.getHeight() / 3));
            }
        }
    }

    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setServidor(Servidor servidor) { this.servidor = servidor; }

    @Override
    public void render(float delta) {
        // Limpeza em preto absoluto camufla os cortes secos de proporção de tela do Viewport (FitViewport)
        ScreenUtils.clear(Color.BLACK);

        worldStage.act(delta);
        hudStage.act(delta);

        worldStage.draw();
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldStage.getViewport().update(width, height, true);
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (cliente != null) cliente.fechar();

        worldStage.dispose();
        hudStage.dispose();

        // Evita vazamentos e estouro de memória de vídeo externa (VRAM / Pointer Leaks)
        if (texturaZonas != null) texturaZonas.dispose();
        if (texturaBase != null) texturaBase.dispose();
        if (fontePadrao != null) fontePadrao.dispose();
        if (background != null) background.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
