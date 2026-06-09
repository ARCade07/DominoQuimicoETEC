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
import com.domino.rede.packets.PacketPontuacao;
import com.domino.rede.packets.PacketQuantidadePecas;
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

    private final float MARGEM = 170f;

    // Texturas
    private Texture texturaPeca_a_a;
    private Texture texturaPeca_a_b;
    private Texture texturaPeca_b_b;
    // Recursos Gráficos e Gerenciamento de Assets
    private Texture texturaZonas;
    private Texture texturaBase;
    private BitmapFont fontePadrao;
    private Background background;

    // Camada de Comunicação em Rede
    private Cliente cliente;
    private Servidor servidor;
    private int quantidadePecas = 7;
    private int pontuacao;
    private List<Peca> pecasLogicasNaMao;

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

        // Prepara as zonas
        alvoEsquerda = new ZonaDeSoltarPeca(false);
        alvoEsquerda.setPosition((stage.getWidth() / 2) - 220, (stage.getHeight() / 2));

        alvoDireita = new ZonaDeSoltarPeca(true);
        alvoDireita.setPosition((stage.getWidth() / 2), (stage.getHeight() / 2));

        worldStage.addActor(alvoEsquerda);
        worldStage.addActor(alvoDireita);

        // 4. Lógica e Engenharia de Drag and Drop
        dragAndDrop = new DragAndDrop();

        dragAndDrop.addTarget(new DragAndDrop.Target(alvoDireita) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload.getDragActor() != null){
                    payload.getDragActor().setColor(1f, 1f, 1f, 1f);
                }
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                if (payload.getDragActor() != null){
                    payload.getDragActor().setColor(1f, 1f, 1f, 0.5f);
                }
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), true)){
                    worldStage.addActor(pecaSolta);

                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());
                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    // Gira a peça para arrumar visualmente no tabuleiro
                    if (alvoDireita.direcao == Direcao.CIMA) pecaSolta.setRotation(pecaSolta.getRotation() + 90);
                    if (alvoDireita.direcao == Direcao.INVERTIDO) pecaSolta.setRotation(pecaSolta.getRotation() - 180);

                    // Calcula dimensões da peça solta (varia conforme a direção)
                    float larguraVisual = alvoDireita.direcao.calcularLarguraVisual(pecaSolta, estaDeitada);
                    float deslocamentoX = alvoDireita.direcao.calcularDeslocamentoX(pecaSolta, estaDeitada);
                    float deslocamentoY = alvoDireita.direcao.calcularDeslocamentoY(pecaSolta, estaDeitada);

                    alvoDireita.direcao.calcularCoordenadas(alvoDireita, pecaSolta, larguraVisual, deslocamentoX, deslocamentoY);

                    // Faz a cobrinha
                    if (alvoDireita.getX() + alvoDireita.getWidth() + MARGEM >= stage.getWidth()){
                        alvoDireita.direcao = Direcao.CIMA;
                    }
                    else if (alvoDireita.getX() - MARGEM <= 0){
                        alvoDireita.direcao = Direcao.CIMA;
                    }
                    // Cobrinha horizontal
                    if (alvoDireita.getY() + alvoDireita.getHeight() >= stage.getHeight() && alvoDireita.direcao != Direcao.INVERTIDO){
                        alvoDireita.direcao = Direcao.INVERTIDO; // Vai pra esquerda
                        alvoDireita.setPosition(alvoDireita.getX() - 220, alvoDireita.getY() - (larguraVisual / 2f));
                    }

                    Peca pecaLogica = pecaSolta.getPecaLogica();
                    pecasLogicasNaMao.remove(pecaLogica);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    pontuacao += 100;


                    if (cliente != null) {
                        // pega a peça que foi colocada pelo cliente no tabuleiro
                        PacketJogada pacote = new PacketJogada(pecaSolta.getPecaLogica());
//                        pacote.copiarPeca(pecaSolta.getPecaLogica());
                        pacote.noFinal = true;

                        if(pecasLogicasNaMao.isEmpty()){
                            pacote.ultimaJogada = true;

                            PacketPontuacao pontuacaoFinal = new PacketPontuacao();
                            pontuacaoFinal.pontuacao = pontuacao;

                            cliente.enviarPontuacao(pontuacaoFinal);
                        }

                        cliente.enviarJogada(pacote);

                        // atualiza a quantidade de peças para os outros jogadores
                        PacketQuantidadePecas atualizaQuantidade = new PacketQuantidadePecas();
                        quantidadePecas--;
                        atualizaQuantidade.quantidadePecas = quantidadePecas;

                        cliente.enviarQuantidadePecas(atualizaQuantidade);
                    }
                } else {
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));

                    if(pontuacao > 0){
                        pontuacao -= 50;
                    }

                    if(cliente != null){
                        passarVez();
                    }

                }
                System.out.println("Pontuação: " + pontuacao);
            }
        });

        dragAndDrop.addTarget(new DragAndDrop.Target(alvoEsquerda) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload.getDragActor() != null){
                    payload.getDragActor().setColor(1f, 1f, 1f, 1f);
                }
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                if (payload.getDragActor() != null){
                    payload.getDragActor().setColor(1f, 1f, 1f, 0.5f);
                }
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                PecaVisual pecaSolta = (PecaVisual) payload.getObject();

                if (tabuleiro.colocarPeca(pecaSolta.getPecaLogica(), false)){
                    worldStage.addActor(pecaSolta);

                    pecaSolta.setRotation(pecaSolta.getPecaLogica().getRotacao());
                    final boolean estaDeitada = pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90;

                    // Gira a peça para arrumar visualmente no tabuleiro
                    if (alvoEsquerda.direcao == Direcao.BAIXO) pecaSolta.setRotation(pecaSolta.getRotation() + 90);
                    if (alvoEsquerda.direcao == Direcao.NORMAL) pecaSolta.setRotation(pecaSolta.getRotation() - 180);

                    final float larguraVisual = alvoEsquerda.direcao.calcularLarguraVisual(pecaSolta, estaDeitada);
                    final float deslocamentoX = alvoEsquerda.direcao.calcularDeslocamentoX(pecaSolta, estaDeitada);
                    final float deslocamentoY = alvoEsquerda.direcao.calcularDeslocamentoY(pecaSolta, estaDeitada);

                    alvoEsquerda.direcao.calcularCoordenadas(alvoEsquerda, pecaSolta, larguraVisual, deslocamentoX, deslocamentoY);

                    // Cobrinha vertical
                    if (alvoEsquerda.getX() - MARGEM <= 0){
                        alvoEsquerda.direcao = Direcao.BAIXO;
                    }
                    else if (alvoEsquerda.getX() + alvoEsquerda.getWidth() + MARGEM >= stage.getWidth()){
                        alvoEsquerda.direcao = Direcao.BAIXO;
                    }
                    // Cobrinha horizontal -> Tem q tomar cuidado com o horizontal group da mão do jogador
                    // altura da peça = 200.
                    if (alvoEsquerda.getY() <= 200 && alvoEsquerda.direcao == Direcao.NORMAL){
                        pecaSolta.setPosition(pecaSolta.getX() + 120f, pecaSolta.getY());
                    }
                    if (alvoEsquerda.getY() <= 200) alvoEsquerda.direcao = Direcao.NORMAL;

                    Peca pecaLogica = pecaSolta.getPecaLogica();
                    pecasLogicasNaMao.remove(pecaLogica);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    pontuacao += 100;


                    if (cliente != null) {
                        //pega a peça que foi colocada pelo cliente no tabuleiro
                        PacketJogada pacote = new PacketJogada(pecaSolta.getPecaLogica());
//                        pacote.copiarPeca(pecaSolta.getPecaLogica());
                        pacote.noFinal = false;

                        if(pecasLogicasNaMao.isEmpty()){
                            pacote.ultimaJogada = true;

                            PacketPontuacao pontuacaoFinal = new PacketPontuacao();
                            pontuacaoFinal.pontuacao = pontuacao;

                            cliente.enviarPontuacao(pontuacaoFinal);
                        }

                        cliente.enviarJogada(pacote);

                        // atualiza a quantidade de peças para os outros jogadores
                        PacketQuantidadePecas atualizaQuantidade = new PacketQuantidadePecas();
                        quantidadePecas--;
                        atualizaQuantidade.quantidadePecas = quantidadePecas;

                        cliente.enviarQuantidadePecas(atualizaQuantidade);
                    }
                } else {
                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();
                    pecaSolta.addAction(Actions.moveTo(xOriginal, yOriginal, 0.5f));

                    if(pontuacao > 100){
                        pontuacao -= 50;
                    }

                    if(cliente != null){
                        passarVez();
                    }

                }
                System.out.println("Pontuação: " + pontuacao);

            }
        });

        this.inicializarPecas();
    }

    private void passarVez(){
        PacketJogada pacote = new PacketJogada();
        cliente.enviarJogada(pacote);
    }

    private void inicilizarTexturas(){
        this.texturaPeca_a_a = new Texture("peca_a_a.png");
        this.texturaPeca_a_b = new Texture("peca_a_b.png");
        this.texturaPeca_b_b = new Texture("peca_b_b.png");
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
        pecasLogicasNaMao = p.buscarPecasAleatorias(7);

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
        if(jogada.proximoAJogar == -1){
            PacketPontuacao packetPontuacao = new PacketPontuacao();
            packetPontuacao.pontuacao = pontuacao;

            cliente.enviarPontuacao(packetPontuacao);
        }

        Peca pecaAdversario = new Peca(jogada.info1, jogada.tipo1, jogada.info2, jogada.tipo2);

        if (tabuleiro.colocarPeca(pecaAdversario, jogada.noFinal)) {
            PecaVisual pecaVisualAdversario = new PecaVisual(pecaAdversario, texturaBase, fontePadrao);

            Texture textura = getTextura(jogada.info1, jogada.info2);
            PecaVisual pecaVisualAdversario = new PecaVisual(pecaAdversario, textura);
            pecaVisualAdversario.setRotation(pecaVisualAdversario.getPecaLogica().getRotacao());
            final boolean estaDeitada = (pecaVisualAdversario.getRotation() == 90 || pecaVisualAdversario.getRotation() == -90);

            stage.addActor(pecaVisualAdversario);
            // posicionamento da peça e da zona
            if (jogada.noFinal) {
                // Gira a peça para arrumar visualmente no tabuleiro
                if (alvoDireita.direcao == Direcao.CIMA) pecaVisualAdversario.setRotation(pecaVisualAdversario.getRotation() + 90);
                if (alvoDireita.direcao == Direcao.INVERTIDO) pecaVisualAdversario.setRotation(pecaVisualAdversario.getRotation() - 180);

                final float larguraVisual = alvoDireita.direcao.calcularLarguraVisual(pecaVisualAdversario, estaDeitada);
                final float deslocamentoX = alvoDireita.direcao.calcularDeslocamentoX(pecaVisualAdversario, estaDeitada);
                final float deslocamentoY = alvoDireita.direcao.calcularDeslocamentoY(pecaVisualAdversario, estaDeitada);

                alvoDireita.direcao.calcularCoordenadas(alvoDireita, pecaVisualAdversario, larguraVisual, deslocamentoX, deslocamentoY);

                if (alvoDireita.getX() + alvoDireita.getWidth() + MARGEM >= stage.getWidth()){
                    alvoDireita.direcao = Direcao.CIMA;
                }
                else if (alvoDireita.getX() - MARGEM <= 0){
                    alvoDireita.direcao = Direcao.CIMA;
                }
                if (alvoDireita.getY() + alvoDireita.getHeight() >= stage.getHeight() && alvoDireita.direcao != Direcao.INVERTIDO){
                    alvoDireita.direcao = Direcao.INVERTIDO; // Vai pra esquerda
                    alvoDireita.setPosition(alvoDireita.getX() - alvoDireita.getWidth(), alvoDireita.getY() - (larguraVisual) / 2f);
                }
            } else {
                if (alvoEsquerda.direcao == Direcao.BAIXO) pecaVisualAdversario.setRotation(pecaVisualAdversario.getRotation() + 90);
                if (alvoEsquerda.direcao == Direcao.NORMAL) pecaVisualAdversario.setRotation(pecaVisualAdversario.getRotation() - 180);

                final float larguraVisual = alvoEsquerda.direcao.calcularLarguraVisual(pecaVisualAdversario, estaDeitada);
                final float deslocamentoX = alvoEsquerda.direcao.calcularDeslocamentoX(pecaVisualAdversario, estaDeitada);
                final float deslocamentoY = alvoEsquerda.direcao.calcularDeslocamentoY(pecaVisualAdversario, estaDeitada);

                alvoEsquerda.direcao.calcularCoordenadas(alvoEsquerda, pecaVisualAdversario, larguraVisual, deslocamentoX, deslocamentoY);

                // Cobrinha
                if (alvoEsquerda.getX() - MARGEM <= 0){
                    alvoEsquerda.direcao = Direcao.BAIXO;
                }
                else if (alvoEsquerda.getX() + alvoEsquerda.getWidth() + MARGEM >= stage.getWidth()){
                    alvoEsquerda.direcao = Direcao.BAIXO;
                }
                if (alvoEsquerda.getY() <= 200 && alvoEsquerda.direcao == Direcao.NORMAL){ // Só entra aqui depois de ter feito a cobra
                    // Soma 120px para compensar a largura da zona com a largura da peça
                    pecaVisualAdversario.setPosition(pecaVisualAdversario.getX() + 120f, pecaVisualAdversario.getY());
                }
                if (alvoEsquerda.getY() <= 200) alvoEsquerda.direcao = Direcao.NORMAL;
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
