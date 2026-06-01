package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import java.util.List;

public class TelaLobby implements Screen {
    private Cliente cliente;
    private Servidor servidor;

    private Stage palco;
    private Skin tema;
    private static final float MULTIPLICADOR_HD = 3.0f;

    private BitmapFont fonteNormal;
    private BitmapFont fonteNegrito;
    private BitmapFont fonteInput;

    private TextureRegion tracoCinza;

    private Array<Actor> ordemNavegacao;
    private InputListener listenerTiraFoco;
    private Table listaJogadores;

    public TelaLobby() {}

    public TelaLobby(Servidor servidor, Cliente cliente){
        this.servidor = servidor;
        this.cliente = cliente;
    }

    @Override
    public void show() {
        palco = new Stage(new ExtendViewport(1920, 1080));
        Gdx.input.setInputProcessor(palco);
        tema = new Skin();
        ordemNavegacao = new Array<>();

        Pixmap pixTraco = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixTraco.setColor(Color.WHITE);
        pixTraco.fill();
        tracoCinza = new TextureRegion(new Texture(pixTraco));
        pixTraco.dispose();

        carregarFontes();
        configurarEstilos();

        construirInterface();

        listenerTiraFoco = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    palco.setKeyboardFocus(null);
                }
                return false;
            }
        };

        atualizarNavegacao();
    }

    private void atualizarNavegacao() {
        palco.getRoot().clearListeners();

        if (listenerTiraFoco != null) {
            palco.getRoot().addCaptureListener(listenerTiraFoco);
        }

        GerenciadorAcessibilidade.configurarNavegacao(palco, ordemNavegacao.toArray(Actor.class));
    }

    private void carregarFontes() {
        FreeTypeFontGenerator geradorNormal = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Medium.ttf"));
        FreeTypeFontParameter paramNormal = new FreeTypeFontParameter();
        paramNormal.size = (int) (36 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        paramNormal.color = Color.WHITE;
        paramNormal.genMipMaps = true;
        paramNormal.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        paramNormal.magFilter = Texture.TextureFilter.Linear;
        paramNormal.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←.";
        paramNormal.borderWidth = 2f;
        paramNormal.borderColor = new Color(0, 0, 0, 0);
        paramNormal.padTop = 4;
        paramNormal.padBottom = 4;
        paramNormal.padLeft = 4;
        paramNormal.padRight = 4;
        paramNormal.spaceX = 4;
        paramNormal.spaceY = 4;

        fonteNormal = geradorNormal.generateFont(paramNormal);
        fonteNormal.setUseIntegerPositions(false);

        FreeTypeFontParameter paramInput = new FreeTypeFontParameter();
        paramInput.size = (int) (36 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        paramInput.color = Color.WHITE;
        paramInput.genMipMaps = true;
        paramInput.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        paramInput.magFilter = Texture.TextureFilter.Linear;
        paramInput.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←.";
        paramInput.borderWidth = 2f;
        paramInput.borderColor = new Color(0, 0, 0, 0);
        paramInput.padTop = 4;
        paramInput.padBottom = 4;
        paramInput.padLeft = 4;
        paramInput.padRight = 4;
        paramInput.spaceX = 4;
        paramInput.spaceY = 4;

        fonteInput = geradorNormal.generateFont(paramInput);
        fonteInput.setUseIntegerPositions(false);
        fonteInput.getData().setScale(0.85f / MULTIPLICADOR_HD);

        geradorNormal.dispose();

        FreeTypeFontGenerator geradorNegrito = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Bold.ttf"));
        FreeTypeFontParameter paramNegrito = new FreeTypeFontParameter();
        paramNegrito.size = (int) (36 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        paramNegrito.color = Color.WHITE;
        paramNegrito.genMipMaps = true;
        paramNegrito.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        paramNegrito.magFilter = Texture.TextureFilter.Linear;
        paramNegrito.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←.";
        paramNegrito.borderWidth = 2f;
        paramNegrito.borderColor = new Color(0, 0, 0, 0);
        paramNegrito.padTop = 8;
        paramNegrito.padBottom = 8;
        paramNegrito.padLeft = 8;
        paramNegrito.padRight = 8;
        paramNegrito.spaceX = 8;
        paramNegrito.spaceY = 8;

        fonteNegrito = geradorNegrito.generateFont(paramNegrito);
        fonteNegrito.setUseIntegerPositions(false);
        geradorNegrito.dispose();
    }

    private void configurarEstilos() {
        Label.LabelStyle sTitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        tema.add("titulo", sTitulo);

        Label.LabelStyle sSubtitulo = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoFraco());
        tema.add("subtitulo", sSubtitulo);

        Label.LabelStyle sTexto = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoPadrao());
        tema.add("texto", sTexto);

        TextField.TextFieldStyle sInput = new TextField.TextFieldStyle();
        sInput.font = fonteInput;
        sInput.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();

        Color corBgPainel = GerenciadorAcessibilidade.getCorFundoCartao();
        Color corBordaPainel = GerenciadorAcessibilidade.getCorBordaCartao();
        Color corFoco = GerenciadorAcessibilidade.getCorDestaqueFoco();

        NinePatchDrawable bgNormal = criarBordaArredondadaTextura(corBgPainel, corBordaPainel, 8, 2);
        bgNormal.getPatch().setLeftWidth(20);
        bgNormal.getPatch().setRightWidth(20);
        sInput.background = bgNormal;

        NinePatchDrawable bgFoco = criarBordaArredondadaTextura(corBgPainel, corFoco, 8, 2);
        bgFoco.getPatch().setLeftWidth(20);
        bgFoco.getPatch().setRightWidth(20);
        sInput.focusedBackground = bgFoco;

        TextureRegionDrawable cursorTex = criarTexturaCor(GerenciadorAcessibilidade.getCorTextoPadrao());
        cursorTex.setMinWidth(2);
        sInput.cursor = cursorTex;

        tema.add("default", sInput);
    }

    private void construirInterface() {
        Table raiz = new Table();
        raiz.setFillParent(true);

        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean protanopia = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        if (altoContraste) {
            raiz.setBackground(criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()));
        } else {
            Color corTopo, corBase;
            if (protanopia) {
                corTopo = Color.valueOf("0A1428");
                corBase = Color.valueOf("02050A");
            } else {
                corTopo = Color.valueOf("4A0000");
                corBase = Color.valueOf("0D0202");
            }
            raiz.setBackground(GerenciadorAcessibilidade.criarTexturaGradiente(corTopo, corBase));
        }
        palco.addActor(raiz);

        Color corSombra = altoContraste ? Color.DARK_GRAY : (protanopia ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton.TextButtonStyle estiloBotaoVoltar = new TextButton.TextButtonStyle();
        estiloBotaoVoltar.font = fonteNegrito;
        estiloBotaoVoltar.fontColor = Color.WHITE;
        estiloBotaoVoltar.up = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombra, 18, 9);
        estiloBotaoVoltar.over = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombra, 18, 9);
        estiloBotaoVoltar.down = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombra, 18, 3);
        estiloBotaoVoltar.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 18, 9);
        estiloBotaoVoltar.focusedFontColor = Color.BLACK;

        TextButton btnVoltar = new TextButton("← VOLTAR", estiloBotaoVoltar);
        btnVoltar.getLabel().setFontScale(1f / MULTIPLICADOR_HD);

        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);
        ordemNavegacao.add(btnVoltar);

        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnVoltar.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaRanking());
            }
        });

        Table layerSuperior = new Table();
        layerSuperior.setFillParent(true);
        layerSuperior.top().left();
        layerSuperior.add(btnVoltar).width(270).height(98).pad(30);
        palco.addActor(layerSuperior);

        Label lblTitulo = criarRotulo("SALA DE ESPERA", "titulo", 1.8f);
        raiz.add(lblTitulo).colspan(2).padTop(60).padBottom(60).row();

        Table colunaEsquerda = new Table();
        Table colunaDireita = new Table();

        construirColunaEsquerda(colunaEsquerda);
        construirColunaDireita(colunaDireita);

        raiz.add(colunaEsquerda).width(Value.percentWidth(0.40f, raiz)).top().padRight(30);
        raiz.add(colunaDireita).width(Value.percentWidth(0.45f, raiz)).top().padLeft(30);
    }

    private void construirColunaEsquerda(Table coluna) {
        Table painelHost = criarPainelBase();

        Label lblSuaIP = criarRotulo("SEU IP PARA CONVITE (HOST)", "subtitulo", 0.7f);
        painelHost.add(lblSuaIP).left().padBottom(20).row();

        Table caixaFakeInput = new Table();
        caixaFakeInput.setBackground(tema.get("default", TextField.TextFieldStyle.class).background);

        final String IP_REAL;
        if(servidor != null){
            IP_REAL = servidor.obterIPLocal();
        }
        else{
            IP_REAL = "Vejo isso depois";
        }

        final String IP_OCULTO = "............";

        Label lblNumeroIp = criarRotulo(IP_OCULTO, "texto", 0.85f);
        lblNumeroIp.setAlignment(Align.center);

        final TextureRegionDrawable olhoFechadoNormal = criarIconeOlho(GerenciadorAcessibilidade.getCorTextoFraco(), true);
        final TextureRegionDrawable olhoAbertoNormal = criarIconeOlho(GerenciadorAcessibilidade.getCorTextoPadrao(), false);
        final TextureRegionDrawable olhoFechadoFoco = criarIconeOlho(GerenciadorAcessibilidade.getCorDestaqueFoco(), true);
        final TextureRegionDrawable olhoAbertoFoco = criarIconeOlho(GerenciadorAcessibilidade.getCorDestaqueFoco(), false);

        ImageButton.ImageButtonStyle estiloOlho = new ImageButton.ImageButtonStyle();
        estiloOlho.imageUp = olhoFechadoNormal;
        estiloOlho.imageChecked = olhoAbertoNormal;
        estiloOlho.imageOver = olhoFechadoFoco;
        estiloOlho.imageCheckedOver = olhoAbertoFoco;

        final ImageButton btnVisibilidade = new ImageButton(estiloOlho);

        GerenciadorAcessibilidade.aplicarFoco(btnVisibilidade);
        ordemNavegacao.add(btnVisibilidade);

        btnVisibilidade.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    btnVisibilidade.getStyle().imageUp = olhoFechadoFoco;
                    btnVisibilidade.getStyle().imageChecked = olhoAbertoFoco;
                } else {
                    btnVisibilidade.getStyle().imageUp = olhoFechadoNormal;
                    btnVisibilidade.getStyle().imageChecked = olhoAbertoNormal;
                }
            }
        });

        btnVisibilidade.addListener(new ClickListener() {
            boolean visivel = false;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                visivel = !visivel;
                if (visivel) {
                    lblNumeroIp.setText(IP_REAL);
                    btnVisibilidade.setChecked(true);
                } else {
                    lblNumeroIp.setText(IP_OCULTO);
                    btnVisibilidade.setChecked(false);
                }
            }
        });

        caixaFakeInput.add().width(65);
        caixaFakeInput.add(lblNumeroIp).center().expandX();
        caixaFakeInput.add(btnVisibilidade).size(50, 35).padRight(15);

        painelHost.add(caixaFakeInput).growX().height(70).padBottom(25).row();

        TextButton btnCopiar = criarBotaoFino("COPIAR IP");

        GerenciadorAcessibilidade.aplicarFoco(btnCopiar);
        ordemNavegacao.add(btnCopiar);

        btnCopiar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.getClipboard().setContents(IP_REAL);
                btnCopiar.setText("IP COPIADO!");
            }
        });
        painelHost.add(btnCopiar).width(350).height(65).row();

        coluna.add(painelHost).growX().padBottom(40).row();

        Table painelConectar = criarPainelBase();

        Label lblConectar = criarRotulo("CONECTAR A UM AMIGO", "subtitulo", 0.7f);
        painelConectar.add(lblConectar).left().padBottom(20).row();

        TextField campoDigitarIp = new TextField("", tema) {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                if (getText().isEmpty()) {
                    BitmapFont font = getStyle().font;
                    Color corAntiga = font.getColor();
                    font.setColor(GerenciadorAcessibilidade.getCorTextoFraco());

                    float y = getY() + (getHeight() / 2f) + (font.getCapHeight() / 2f);
                    font.draw(batch, "Digite o IP da sala aqui...", getX(), y, getWidth(), Align.center, false);
                    font.setColor(corAntiga);
                }
            }
        };
        campoDigitarIp.setMessageText("");
        campoDigitarIp.setAlignment(Align.center);

        GerenciadorAcessibilidade.aplicarFoco(campoDigitarIp);
        ordemNavegacao.add(campoDigitarIp);

        painelConectar.add(campoDigitarIp).growX().height(70).padBottom(25).row();

        TextButton btnConectar = criarBotaoFino("CONECTAR");

        GerenciadorAcessibilidade.aplicarFoco(btnConectar);
        ordemNavegacao.add(btnConectar);

        painelConectar.add(btnConectar).width(350).height(65).row();

        coluna.add(painelConectar).growX().row();
    }

    private void construirColunaDireita(Table coluna) {
        Label lblJogadores = criarRotulo("JOGADORES CONECTADOS", "titulo", 0.8f);
        coluna.add(lblJogadores).left().padBottom(20).row();

        listaJogadores = new Table();

        coluna.add(listaJogadores).growX().row();


        Color corFundoBtnIniciar = GerenciadorAcessibilidade.getCorDestaqueSucesso();
        Color corSombraBtnIniciar = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE ?
            Color.DARK_GRAY : new Color(corFundoBtnIniciar.r * 0.5f, corFundoBtnIniciar.g * 0.5f, corFundoBtnIniciar.b * 0.5f, 1f);

        TextButton.TextButtonStyle estiloIniciar = new TextButton.TextButtonStyle();
        estiloIniciar.font = fonteNegrito;
        estiloIniciar.fontColor = Color.BLACK;
        estiloIniciar.up = criarBotao3D(corFundoBtnIniciar, corSombraBtnIniciar, 12, 6);
        estiloIniciar.over = criarBotao3D(new Color(corFundoBtnIniciar.r * 1.2f, corFundoBtnIniciar.g * 1.2f, corFundoBtnIniciar.b * 1.2f, 1f), corSombraBtnIniciar, 12, 6);
        estiloIniciar.down = criarBotao3D(new Color(corFundoBtnIniciar.r * 0.8f, corFundoBtnIniciar.g * 0.8f, corFundoBtnIniciar.b * 0.8f, 1f), corSombraBtnIniciar, 12, 2);
        estiloIniciar.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), corSombraBtnIniciar, 12, 6);
        estiloIniciar.focusedFontColor = Color.BLACK;

        TextButton btnIniciar = new TextButton("INICIAR PARTIDA", estiloIniciar);
        btnIniciar.getLabel().setFontScale(1f / MULTIPLICADOR_HD);

        GerenciadorAcessibilidade.aplicarFoco(btnIniciar);
        ordemNavegacao.add(btnIniciar);

        btnIniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen telaJogo = new GameScreen();


                telaJogo.setCliente(cliente);
                cliente.setGameScreen(telaJogo);

                if (servidor != null) {
                    telaJogo.setServidor(servidor);
                    servidor.botaoInicioClicado(cliente.idCliente());
                    servidor.decidirQuemComeca();

                }

                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(telaJogo);
            }
        });

        coluna.add(btnIniciar).width(450).height(85).center().padTop(30);
    }

    public void atualizaJogadoresNaTela(List<Integer> idjogadoresConectados) {
                listaJogadores.clearChildren();
                int tamanho = idjogadoresConectados.size();

                for (int i = 0; i < 4; i++) {
                    if (i < tamanho) {

                        final int idJogador = idjogadoresConectados.get(i);

                        String nomeJogador = (i == 0) ? "Host" : "Jogador " + (idjogadoresConectados.get(i));
                        boolean isHost = (i == 0);


                        boolean temX = (servidor != null && i != 0);;

                        Runnable acaoRemover = new Runnable() {
                            @Override
                            public void run() {
                                servidor.removerJogador(idJogador);
                            }
                        };

                        Table slotOcupado = criarSlotJogador(nomeJogador, isHost, temX, acaoRemover);
                        listaJogadores.add(slotOcupado).growX().height(75).padBottom(15).row();
                    } else {
                        Table slotVazio = criarSlotVazio();
                        listaJogadores.add(slotVazio).growX().height(75).padBottom(15).row();
                    }
                }


    }

    private Table criarPainelBase() {
        Table painel = new Table();
        painel.setBackground(criarBordaArredondadaTextura(
            GerenciadorAcessibilidade.getCorFundoCartao(),
            GerenciadorAcessibilidade.getCorBordaForte(),
            16, 2));
        painel.pad(35);
        return painel;
    }

    private Table criarSlotJogador(String nome, boolean isHost, boolean temX, final Runnable acaoRemover) {
        Table slot = new Table();
        Color bgCartao = GerenciadorAcessibilidade.getCorFundoTela();
        Color borderCartao = GerenciadorAcessibilidade.getCorBordaCartao();
        slot.setBackground(criarBordaArredondadaTextura(bgCartao, borderCartao, 18, 2));
        slot.padLeft(20).padRight(20);

        Table conteudoEsquerda = new Table();
        if (isHost) {
            Image imgCoroa = new Image(criarCoroaTextura(Color.valueOf("FFD700")));
            conteudoEsquerda.add(imgCoroa).size(36, 27).padRight(15);
        }

        Label lblNome = criarRotulo(nome, "texto", 0.9f);
        conteudoEsquerda.add(lblNome);

        slot.add(conteudoEsquerda).left().expandX();

        if (temX) {
            TextButton.TextButtonStyle estiloX = new TextButton.TextButtonStyle();
            estiloX.font = fonteNegrito;
            estiloX.fontColor = GerenciadorAcessibilidade.getCorTextoFraco();
            estiloX.overFontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
            estiloX.downFontColor = GerenciadorAcessibilidade.getCorDestaqueErro();
            estiloX.focusedFontColor = GerenciadorAcessibilidade.getCorDestaqueFoco();

            final TextButton btnX = new TextButton("X", estiloX);
            btnX.getLabel().setFontScale(0.8f / MULTIPLICADOR_HD);
            btnX.pad(10);

            GerenciadorAcessibilidade.aplicarFoco(btnX);
            ordemNavegacao.add(btnX);

            btnX.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ordemNavegacao.removeValue(btnX, true);
                    atualizarNavegacao();

                    //colocar a logica de conexao com o servidor
                    if (acaoRemover != null) {
                        acaoRemover.run();
                    }
                }
            });
            slot.add(btnX).right();
        }

        return slot;
    }

    private Table criarSlotVazio() {
        Table slot = new Table() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                Color corAnterior = batch.getColor();

                Color corTraco = GerenciadorAcessibilidade.getCorTextoFraco();
                batch.setColor(new Color(corTraco.r, corTraco.g, corTraco.b, 0.5f));

                float dashWidth = 12f;
                float spaceWidth = 8f;
                float distTotal = dashWidth + spaceWidth;
                float largura = getWidth();
                float altura = getHeight();
                float x = getX();
                float y = getY();
                float espessura = 2f;
                float raio = 18f;

                for (float i = raio; i < largura - raio; i += distTotal) {
                    batch.draw(tracoCinza, x + i, y, Math.min(dashWidth, largura - raio - i), espessura);
                    batch.draw(tracoCinza, x + i, y + altura - espessura, Math.min(dashWidth, largura - raio - i), espessura);
                }
                for (float i = raio; i < altura - raio; i += distTotal) {
                    batch.draw(tracoCinza, x, y + i, espessura, Math.min(dashWidth, altura - raio - i));
                    batch.draw(tracoCinza, x + largura - espessura, y + i, espessura, Math.min(dashWidth, altura - raio - i));
                }
                batch.setColor(corAnterior);
            }
        };
        slot.setBackground(criarBordaArredondadaTextura(new Color(0, 0, 0, 0.2f), new Color(0,0,0,0), 18, 0));

        Label lblAguardando = criarRotulo("Aguardando jogador...", "subtitulo", 0.8f);
        slot.add(lblAguardando).center().expandX();
        return slot;
    }

    private TextureRegionDrawable criarIconeOlho(Color cor, boolean fechado) {
        int tamanho = 64;
        Pixmap pix = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);

        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(0, 0, 0, 0);
        pix.fill();

        pix.setColor(cor);
        pix.fillTriangle(6, 32, 32, 12, 58, 32);
        pix.fillTriangle(6, 32, 32, 52, 58, 32);

        pix.setColor(0, 0, 0, 0);
        pix.fillTriangle(12, 32, 32, 17, 52, 32);
        pix.fillTriangle(12, 32, 32, 47, 52, 32);

        pix.setBlending(Pixmap.Blending.SourceOver);

        pix.setColor(cor);
        pix.fillCircle(32, 32, 9);

        if (fechado) {
            pix.setColor(GerenciadorAcessibilidade.getCorDestaqueErro());
            for (int i = -3; i <= 3; i++) {
                pix.drawLine(12 + i, 52, 52 + i, 12);
            }
        }

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextureRegionDrawable criarCoroaTextura(Color cor) {
        int w = 60;
        int h = 45;
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(cor);
        pix.fillRectangle(6, 30, 48, 9);
        pix.fillTriangle(6, 30, 21, 30, 3, 6);
        pix.fillTriangle(21, 30, 39, 30, 30, 0);
        pix.fillTriangle(39, 30, 54, 30, 57, 6);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextButton criarBotaoFino(String texto) {
        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fonteNegrito;
        estiloBotao.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();

        Color bgSecundario = GerenciadorAcessibilidade.getCorFundoTela();
        Color bgFoco = GerenciadorAcessibilidade.getCorDestaqueFoco();

        estiloBotao.up = criarBordaArredondadaTextura(bgSecundario, Color.LIGHT_GRAY, 8, 2);
        estiloBotao.over = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), Color.WHITE, 8, 2);
        estiloBotao.down = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoDown(), Color.GRAY, 8, 2);
        estiloBotao.focused = criarBordaArredondadaTextura(bgSecundario, bgFoco, 8, 3);

        TextButton btn = new TextButton(texto, estiloBotao);
        btn.getLabel().setFontScale(0.85f / MULTIPLICADOR_HD);
        return btn;
    }

    private Label criarRotulo(String texto, String estilo, float escala) {
        Label rotulo = new Label(texto, tema, estilo);
        rotulo.setFontScale(escala / MULTIPLICADOR_HD);
        return rotulo;
    }

    private TextureRegionDrawable criarTexturaCor(Color cor) {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(cor);
        pix.fill();
        Texture tex = new Texture(pix);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private NinePatchDrawable criarBotao3D(Color corCorpo, Color corSombra, int raio, int profundidade) {
        int escala = 4, t = 100 * escala, r = raio * escala, p = profundidade * escala;
        Pixmap pix = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(corSombra);
        pix.fillCircle(r, t - r - 1, r);
        pix.fillCircle(t - r - 1, t - r - 1, r);
        pix.fillRectangle(r, t - 2 * r, t - 2 * r, 2 * r);
        pix.fillRectangle(0, t - r - 1 - p, t, p);

        pix.setColor(corCorpo);
        pix.fillCircle(r, r, r);
        pix.fillCircle(t - r - 1, r, r);
        pix.fillCircle(r, t - r - 1 - p, r);
        pix.fillCircle(t - r - 1, t - r - 1 - p, r);
        pix.fillRectangle(r, 0, t - 2 * r, t - p);
        pix.fillRectangle(0, r, t, t - 2 * r - p);

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r + p);
        np.scale(1f / escala, 1f / escala);
        return new NinePatchDrawable(np);
    }

    private NinePatchDrawable criarBordaArredondadaTextura(Color corFundo, Color corBorda, int raio, int tamanhoBorda) {
        int escala = 4, tamanho = 100 * escala, r = raio * escala, b = tamanhoBorda * escala;
        Pixmap pix = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);

        pix.setColor(corBorda);
        if(raio > 0) {
            pix.fillCircle(r, r, r);
            pix.fillCircle(tamanho - r - 1, r, r);
            pix.fillCircle(r, tamanho - r - 1, r);
            pix.fillCircle(tamanho - r - 1, tamanho - r - 1, r);
        }
        pix.fillRectangle(r, 0, tamanho - 2 * r, tamanho);
        pix.fillRectangle(0, r, tamanho, tamanho - 2 * r);

        pix.setColor(corFundo);
        int ri = r - b;
        if (ri < 0) ri = 0;
        if(raio > 0 && ri > 0) {
            pix.fillCircle(r, r, ri);
            pix.fillCircle(tamanho - r - 1, r, ri);
            pix.fillCircle(r, tamanho - r - 1, ri);
            pix.fillCircle(tamanho - r - 1, tamanho - r - 1, ri);
        }
        pix.fillRectangle(raio > 0 ? r : b, b, tamanho - (raio > 0 ? 2 * r : 2 * b), tamanho - 2 * b);
        pix.fillRectangle(b, raio > 0 ? r : b, tamanho - 2 * b, tamanho - (raio > 0 ? 2 * r : 2 * b));

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r);
        np.scale(1f / escala, 1f / escala);
        return new NinePatchDrawable(np);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        palco.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        palco.draw();
    }

    @Override
    public void resize(int largura, int altura) {
        palco.getViewport().update(largura, altura, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        palco.dispose();
        tema.dispose();
        fonteNormal.dispose();
        fonteNegrito.dispose();
        fonteInput.dispose();
        if (tracoCinza != null) tracoCinza.getTexture().dispose();
    }
}
