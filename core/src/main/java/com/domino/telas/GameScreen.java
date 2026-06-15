package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import com.domino.atores.PecaVisual;
import com.domino.atores.ZonaDeSoltarPeca;
import com.domino.servicos.ServicoPecas;
import com.domino.logica.*;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.rede.packets.PacketJogada;
import com.domino.rede.packets.PacketPontuacao;
import com.domino.rede.packets.PacketQuantidadePecas;
import com.domino.texturas.Background;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {

    private final Stage stage;
    private final DragAndDrop dragAndDrop;

    private final Tabuleiro tabuleiro = new Tabuleiro();
    private final ZonaDeSoltarPeca alvoEsquerda;
    private final ZonaDeSoltarPeca alvoDireita;

    private final float MARGEM = 170f;

    private final Background background;

    //Texturas
    private Texture texturaBasePeca;
    private Texture imagemMonte;
    private Texture libgdx;

    // Camada de Comunicação em Rede
    private Cliente cliente;
    private Servidor servidor;
    private int quantidadePecas = 7;
    private int pontuacao;
    private List<Peca> pecasLogicasNaMao;
    private List<PecaVisual> pecaVisualNaMao;
    private boolean ultimaVez = false;

    public GameScreen() {
        // O FitViewport garante que o jogo não fique esticado se a janela mudar de tamanho
        stage = new Stage(new FitViewport(1920, 1080));
        // 'Stage' é quem vai receber os cliques do mouse
        Gdx.input.setInputProcessor(stage);

        // Coloca o background
        this.background = new Background();

        this.background.setPosition(
            (stage.getWidth() / 2f) - (this.background.getWidth() / 2f),
            (stage.getHeight() / 2f) - (this.background.getHeight() / 2f)
        );
        stage.addActor(this.background);

        // Inicia texturas
        this.inicilizarTexturas();

        // Prepara as zonas
        alvoEsquerda = new ZonaDeSoltarPeca(false);
        alvoEsquerda.setPosition((stage.getWidth() / 2) - 220, (stage.getHeight() / 2));

        alvoDireita = new ZonaDeSoltarPeca(true);
        alvoDireita.setPosition((stage.getWidth() / 2), (stage.getHeight() / 2));

        stage.addActor(alvoEsquerda);
        stage.addActor(alvoDireita);

        // Lógica do Drag and Drop
        dragAndDrop = new DragAndDrop();

        // Onde pode soltar (target)
        // Na direita (final = true)
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
                    // Com isso comentado, a cobrinha vai infinitamente pra esquerda
//                    else if (alvoDireita.getX() - MARGEM <= 0){
//                        alvoDireita.direcao = Direcao.CIMA;
//                    }
                    // Cobrinha horizontal
                    if (alvoDireita.getY() + alvoDireita.getHeight() >= stage.getHeight() && alvoDireita.direcao != Direcao.INVERTIDO){
                        alvoDireita.direcao = Direcao.INVERTIDO; // Vai pra esquerda
                        alvoDireita.setPosition(alvoDireita.getX() - 220, alvoDireita.getY() - (larguraVisual / 2f));
                    }

                    Peca pecaLogica = pecaSolta.getPecaLogica();
                    pecasLogicasNaMao.remove(pecaLogica);
                    pecaVisualNaMao.remove(pecaSolta);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    pontuacao += 100;

                    if (cliente != null) {
                        // pega a peça que foi colocada pelo cliente no tabuleiro
                        PacketJogada pacote = new PacketJogada(pecaSolta.getPecaLogica());
                        //pacote.copiarPeca(pecaSolta.getPecaLogica());
                        pacote.noFinal = true;

                        if(pecasLogicasNaMao.isEmpty()){
                            pacote.ultimaJogada = true;

                            PacketPontuacao pontuacaoFinal = new PacketPontuacao();
                            pontuacaoFinal.pontuacao = pontuacao;

                            cliente.enviarPontuacao(pontuacaoFinal);
                        }

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

                    if(pontuacao > 100){
                        pontuacao -= 50;
                    }

                    if(cliente != null){
                        passarVez();
                    }

                }
            }
        });
        // Para a esquerda (final = false)
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
                    // Cobrinha vai pra direita pra sempre
//                    else if (alvoEsquerda.getX() + alvoEsquerda.getWidth() + MARGEM >= stage.getWidth()){
//                        alvoEsquerda.direcao = Direcao.BAIXO;
//                    }
                    // Cobrinha horizontal -> Tem q tomar cuidado com o horizontal group da mão do jogador
                    // altura da peça = 200.
                    if (alvoEsquerda.getY() <= 200 && alvoEsquerda.direcao == Direcao.NORMAL){
                        pecaSolta.setPosition(pecaSolta.getX() + 120f, pecaSolta.getY());
                    }
                    if (alvoEsquerda.getY() <= 200) alvoEsquerda.direcao = Direcao.NORMAL;

                    Peca pecaLogica = pecaSolta.getPecaLogica();
                    pecasLogicasNaMao.remove(pecaLogica);
                    pecaVisualNaMao.remove(pecaSolta);

                    dragAndDrop.removeSource(source);
                    pecaSolta.clearListeners();

                    pontuacao += 100;

                    if (cliente != null) {
                        //pega a peça que foi colocada pelo cliente no tabuleiro
                        PacketJogada pacote = new PacketJogada(pecaSolta.getPecaLogica());
                        //pacote.copiarPeca(pecaSolta.getPecaLogica());
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
                    System.out.println("Peça incompatível");

                    float xOriginal = source.getActor().getX();
                    float yOriginal = source.getActor().getY();

                    float xMouse = source.getActor().getX();
                    float yMouse = source.getActor().getY();

                    pecaSolta.setPosition(xMouse, yMouse);
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
        this.texturaBasePeca = new Texture("pecafinal.png");
        this.imagemMonte = new Texture("monte.png");
        this.libgdx = new Texture("libgdx.png");
    }

    private void inicializarPecas(){
        HorizontalGroup pecasNaMao = new HorizontalGroup();
        pecasNaMao.space(15);
        pecasNaMao.setPosition(stage.getWidth() / 4, 125);
        stage.addActor(pecasNaMao);

        this.pecaVisualNaMao = new ArrayList<>();
        // Pega as peças do banco e embaralha
        ServicoPecas servicoPecas = new ServicoPecas();
        List<Peca> todasAsPecas = servicoPecas.buscarTodasAsPecas();
        java.util.Collections.shuffle(todasAsPecas);

        // Cria sublistas para separar a mão do jogador e mandar o restante pro monte
        this.pecasLogicasNaMao = new ArrayList<>(todasAsPecas.subList(0, Math.min(7, todasAsPecas.size())));

        // As peças restantes podem ir pro monte
        List<Peca> monte = new ArrayList<>(todasAsPecas.subList(this.pecasLogicasNaMao.size(), todasAsPecas.size()));

        // CRIANDO O MONTE
        // Botão do monte
        Button botaoMonte = new Button(Estilos.estiloBotaoGrupo);
        botaoMonte.add(new Image(libgdx));

        botaoMonte.setSize(100, 100);
        botaoMonte.setPosition(100, (stage.getHeight()/2));
        botaoMonte.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                // Para debug
                System.out.println("Clicou no monte");

                if (cliente != null && !cliente.minhaVez){
                    System.out.println("Não pode comprar o monte se não for sua vez");
                    return;
                }

                // Pegando uma peça aleatória do monte
                Random random = new Random();
                int num = random.nextInt(monte.size());
                Peca pecaDoMonte = monte.get(num);
                // Coloca na mão do jogador
                pecasLogicasNaMao.add(pecaDoMonte);

                // Bota textura na peça
                Texture texturaPeca = texturaBasePeca;
                PecaVisual pecaVisual = new PecaVisual(pecaDoMonte, texturaPeca);
                pecaVisualNaMao.add(pecaVisual);

                // Colocando na tela
                pecasNaMao.addActor(pecaVisual);

                // Configurando Drag and Drop da peça comprada
                dragAndDrop.addSource(new DragAndDrop.Source(pecaVisual){
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer){
                        if (cliente != null && !cliente.minhaVez){
                            System.out.println("Vez de adversário");
                            return null;
                        }
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(pecaVisual);

                        PecaVisual fantasma = new PecaVisual(pecaVisual.getPecaLogica(), pecaVisual.getTextura());
                        fantasma.getColor().a = 0.5f;

                        // Centraliza o fantasma no mouse (dependendo de onde você clica na peça)
                        dragAndDrop.setDragActorPosition(fantasma.getWidth()/2, -fantasma.getHeight()/2);
                        payload.setDragActor(fantasma);

                        return payload;
                    }
                });
                // Passa a vez depois de comprar
                System.out.println("Comprou e passou vez");
                passarVez();
            }
        });
        // Coloca o botão na tela depois de configurá-lo
        stage.addActor(botaoMonte);

        for (Peca pecaNaMao : pecasLogicasNaMao){
            Texture texturaPeca = this.texturaBasePeca;

            PecaVisual pecaVisual = new PecaVisual(pecaNaMao, texturaPeca);
            pecaVisualNaMao.add(pecaVisual);
        }

        for (PecaVisual pecaVisual : pecaVisualNaMao) {
            pecasNaMao.addActor(pecaVisual);

            // Configura o drag and drop pra cada peça
            dragAndDrop.addSource(new DragAndDrop.Source(pecaVisual) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    if(cliente != null && !cliente.minhaVez){
                        System.out.println("Vez de adversário");
                        return null;
                    }

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(pecaVisual);

                    PecaVisual fantasma = new PecaVisual(pecaVisual.getPecaLogica(), pecaVisual.getTextura());
                    fantasma.getColor().a = 0.5f;

                    // Centraliza o fantasma no mouse (dependendo de onde você clica na peça)
                    dragAndDrop.setDragActorPosition(fantasma.getWidth()/2, -fantasma.getHeight()/2);
                    payload.setDragActor(fantasma);

                    return payload;
                }
            });
        }
    }

    public void atualizarBrilhoPecas(boolean ehMinhaVez){
        for (PecaVisual peca : pecaVisualNaMao) {
            if (ehMinhaVez) {
                peca.setColor(Color.WHITE);
            } else {
                peca.setColor(0.6f, 0.6f, 0.6f, 0.8f);
            }
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
            Texture textura = this.texturaBasePeca;

            PecaVisual pecaVisualAdversario = new PecaVisual(pecaAdversario, textura);
            pecaVisualAdversario.setRotation(pecaVisualAdversario.getPecaLogica().getRotacao());
            final boolean estaDeitada = (pecaVisualAdversario.getRotation() == 90 || pecaVisualAdversario.getRotation() == -90);

            stage.addActor(pecaVisualAdversario);

            pecaVisualAdversario.getColor().a = 0f;
            pecaVisualAdversario.addAction(Actions.fadeIn(0.5f));
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
                // Cobrinha fica infinita -> correção do bug
//                else if (alvoDireita.getX() - MARGEM <= 0){
//                    alvoDireita.direcao = Direcao.CIMA;
//                }
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
                // Corrigindo bug da limitação da cobra
//                else if (alvoEsquerda.getX() + alvoEsquerda.getWidth() + MARGEM >= stage.getWidth()){
//                    alvoEsquerda.direcao = Direcao.BAIXO;
//                }
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
        ScreenUtils.clear(Color.BLACK);

        if (cliente != null) {
            if (cliente.minhaVez != ultimaVez) {
                ultimaVez = cliente.minhaVez;
                atualizarBrilhoPecas(ultimaVez);
            }
        }

        stage.act(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (cliente != null) cliente.fechar();

        stage.dispose();

        // Evita vazamentos e estouro de memória de vídeo externa (VRAM / Pointer Leaks)
        if (background != null) background.dispose();
    }

    // Métodos obrigatórios da interface Screen
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
