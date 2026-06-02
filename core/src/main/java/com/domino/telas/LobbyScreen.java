package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.domino.rede.Cliente;
import com.domino.rede.Servidor;

public class LobbyScreen extends BaseScreen {
    private Cliente cliente;
    private Servidor servidor;

    // Estilos Locais
    private BitmapFont fNormal, fNegrito, fInput;
    private Label.LabelStyle sTitulo, sSub, sTexto;
    private TextField.TextFieldStyle sInput;

    private Array<Actor> ordemNavegacao = new Array<>();
    private Table listaJogadores;
    private TextureRegion tracoBranco;

    public LobbyScreen() { this(null, null); }

    public LobbyScreen(Servidor servidor, Cliente cliente) {
        super();
        this.servidor = servidor;
        this.cliente = cliente;

        tracoBranco = Estilos.criarTexturaCor(Color.WHITE).getRegion();

        inicializarEstilos();
        construirInterface();

        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                return false;
            }
        });
    }

    @Override
    public void show() {
        super.show();
        atualizarNavegacao();
    }

    private void atualizarNavegacao() {
        GerenciadorAcessibilidade.configurarNavegacao(stage, ordemNavegacao.toArray(Actor.class));
    }

    private void inicializarEstilos() {
        float tb = 36 * Estilos.MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario();
        fNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", tb, 4, 2f);
        fInput = Estilos.gerarFonte("Inter_24pt-Medium.ttf", tb, 4, 2f);
        fInput.getData().setScale(0.85f / Estilos.MULTIPLICADOR_HD);
        fNegrito = Estilos.gerarFonte("Inter_24pt-Bold.ttf", tb, 8, 2f);

        sTitulo = new Label.LabelStyle(fNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        sSub = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorTextoFraco());
        sTexto = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorTextoPadrao());

        sInput = new TextField.TextFieldStyle();
        sInput.font = fInput;
        sInput.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        sInput.cursor = Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorTextoPadrao());
        sInput.cursor.setMinWidth(2);

        Color bg = GerenciadorAcessibilidade.getCorFundoCartao();
        sInput.background = criarBgInput(bg, GerenciadorAcessibilidade.getCorBordaCartao());
        sInput.focusedBackground = criarBgInput(bg, GerenciadorAcessibilidade.getCorDestaqueFoco());
    }

    private NinePatchDrawable criarBgInput(Color bg, Color borda) {
        NinePatchDrawable d = Estilos.criarBordaArredondadaTextura(bg, borda, 8, 2);
        d.getPatch().setLeftWidth(20);
        d.getPatch().setRightWidth(20);
        return d;
    }

    private void construirInterface() {
        Table raiz = new Table();
        raiz.setFillParent(true);
        stage.addActor(raiz);

        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean prota = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        raiz.setBackground(ac ? Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()) :
            Estilos.criarTexturaGradiente(Color.valueOf(prota ? "0A1428" : "4A0000"), Color.valueOf(prota ? "02050A" : "0D0202")));

        Color sombra = ac ? Color.DARK_GRAY : (prota ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton btnVoltar = criarBotaoAcao("← VOLTAR", 18, GerenciadorAcessibilidade.getCorFundoBotaoNormal(), sombra);
        btnVoltar.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                btnVoltar.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new RankingScreen());
            }
        });

        Table top = new Table();
        top.setFillParent(true);
        top.add(btnVoltar).width(270).height(98).pad(30).expand().top().left();
        stage.addActor(top);

        raiz.add(criarRotulo("SALA DE ESPERA", sTitulo, 1.8f)).colspan(2).padTop(60).padBottom(60).row();

        Table colEsq = new Table(), colDir = new Table();
        construirColunaEsquerda(colEsq);
        construirColunaDireita(colDir);

        raiz.add(colEsq).width(Value.percentWidth(0.40f, raiz)).top().padRight(30);
        raiz.add(colDir).width(Value.percentWidth(0.45f, raiz)).top().padLeft(30);
    }

    private void construirColunaEsquerda(Table col) {
        Table pHost = criarPainelBase();
        pHost.add(criarRotulo("SEU IP PARA CONVITE (HOST)", sSub, 0.7f)).left().padBottom(20).row();

        Table boxIp = new Table();
        boxIp.setBackground(sInput.background);

        final String ipReal = (servidor != null) ? servidor.obterIPLocal() : "Vejo isso depois";
        Label lblIp = criarRotulo("............", sTexto, 0.85f);
        lblIp.setAlignment(Align.center);

        boxIp.add().width(65);
        boxIp.add(lblIp).center().expandX();
        boxIp.add(criarBotaoOlho(lblIp, ipReal, "............")).size(50, 35).padRight(15);
        pHost.add(boxIp).growX().height(70).padBottom(25).row();

        TextButton btnCopiar = criarBotaoFino("COPIAR IP");
        btnCopiar.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Gdx.app.getClipboard().setContents(ipReal);
                btnCopiar.setText("IP COPIADO!");
            }
        });
        pHost.add(btnCopiar).width(350).height(65).row();
        col.add(pHost).growX().padBottom(40).row();

        Table pConectar = criarPainelBase();
        pConectar.add(criarRotulo("CONECTAR A UM AMIGO", sSub, 0.7f)).left().padBottom(20).row();

        TextField inputIp = new TextField("", sInput) {
            public void draw(Batch b, float a) {
                super.draw(b, a);
                if (getText().isEmpty()) {
                    fInput.setColor(GerenciadorAcessibilidade.getCorTextoFraco());
                    fInput.draw(b, "Digite o IP...", getX(), getY() + getHeight()/2f + fInput.getCapHeight()/2f, getWidth(), Align.center, false);
                    fInput.setColor(GerenciadorAcessibilidade.getCorTextoPadrao());
                }
            }
        };
        inputIp.setAlignment(Align.center);
        ordemNavegacao.add(inputIp);
        GerenciadorAcessibilidade.aplicarFoco(inputIp);

        pConectar.add(inputIp).growX().height(70).padBottom(25).row();
        pConectar.add(criarBotaoFino("CONECTAR")).width(350).height(65).row();
        col.add(pConectar).growX().row();
    }

    private void construirColunaDireita(Table col) {
        col.add(criarRotulo("JOGADORES CONECTADOS", sTitulo, 0.8f)).left().padBottom(20).row();
        listaJogadores = new Table();
        col.add(listaJogadores).growX().row();
        atualizaJogadoresNaTela(1);

        Color cFundo = GerenciadorAcessibilidade.getCorDestaqueSucesso();
        Color cSombra = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE ?
            Color.DARK_GRAY : new Color(cFundo.r * 0.5f, cFundo.g * 0.5f, cFundo.b * 0.5f, 1f);

        TextButton btnIniciar = criarBotaoAcao("INICIAR PARTIDA", 12, cFundo, cSombra);
        btnIniciar.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
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

        col.add(btnIniciar).width(450).height(85).center().padTop(30);
    }

    public void atualizaJogadoresNaTela(int qtd) {
        listaJogadores.clearChildren();
        for (int i = 0; i < 4; i++) {
            Table slot = (i < qtd) ? criarSlotJogador((i==0)?"Host":"Jogador "+(i+1), i==0, servidor!=null && i!=0, null) : criarSlotVazio();
            listaJogadores.add(slot).growX().height(75).padBottom(15).row();
        }
    }

    private Table criarSlotJogador(String nome, boolean host, boolean temX, Runnable acao) {
        Table t = new Table();
        t.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 2));
        t.pad(0, 20, 0, 20);

        Table esq = new Table();
        if (host) esq.add(new Image(Estilos.criarCoroaTextura(Color.valueOf("FFD700")))).size(36, 27).padRight(15);
        esq.add(criarRotulo(nome, sTexto, 0.9f));
        t.add(esq).left().expandX();

        if (temX) {
            TextButton.TextButtonStyle sx = new TextButton.TextButtonStyle();
            sx.font = fNegrito;
            sx.fontColor = GerenciadorAcessibilidade.getCorTextoFraco();
            sx.overFontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
            sx.downFontColor = sx.focusedFontColor = GerenciadorAcessibilidade.getCorDestaqueErro();

            TextButton btnX = new TextButton("X", sx);
            btnX.getLabel().setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
            btnX.pad(10);

            ordemNavegacao.add(btnX);
            GerenciadorAcessibilidade.aplicarFoco(btnX);
            btnX.addListener(new ClickListener() {
                public void clicked(InputEvent e, float x, float y) {
                    ordemNavegacao.removeValue(btnX, true);
                    atualizarNavegacao();
                    if (acao != null) acao.run();
                }
            });
            t.add(btnX).right();
        }
        return t;
    }

    private Table criarSlotVazio() {
        Table t = new Table();
        t.setBackground(new BaseDrawable() {
            @Override
            public void draw(Batch b, float x, float y, float w, float h) {
                Color c = b.getColor(), traco = GerenciadorAcessibilidade.getCorTextoFraco();
                b.setColor(traco.r, traco.g, traco.b, 0.5f);
                float dash = 12f, dist = 20f, esp = 2f, raio = 18f;

                for (float i = raio; i < w - raio; i += dist) {
                    b.draw(tracoBranco, x + i, y, Math.min(dash, w - raio - i), esp);
                    b.draw(tracoBranco, x + i, y + h - esp, Math.min(dash, w - raio - i), esp);
                }
                for (float i = raio; i < h - raio; i += dist) {
                    b.draw(tracoBranco, x, y + i, esp, Math.min(dash, h - raio - i));
                    b.draw(tracoBranco, x + w - esp, y + i, esp, Math.min(dash, h - raio - i));
                }
                b.setColor(c);
            }
        });
        t.add(criarRotulo("Aguardando jogador...", sSub, 0.8f)).center().expandX();
        return t;
    }

    // --- MÉTODOS DE FÁBRICA / UTILITÁRIOS ---

    private Table criarPainelBase() {
        Table p = new Table();
        p.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 2));
        p.pad(35);
        return p;
    }

    private ImageButton criarBotaoOlho(Label lbl, String ipReal, String ipOculto) {
        Color cErro = GerenciadorAcessibilidade.getCorDestaqueErro(), cFraca = GerenciadorAcessibilidade.getCorTextoFraco();
        Color cPadrao = GerenciadorAcessibilidade.getCorTextoPadrao(), cFoco = GerenciadorAcessibilidade.getCorDestaqueFoco();

        ImageButton.ImageButtonStyle s = new ImageButton.ImageButtonStyle();
        s.imageUp = Estilos.criarIconeOlho(cFraca, true, cErro);
        s.imageChecked = Estilos.criarIconeOlho(cPadrao, false, null);
        s.imageOver = s.imageCheckedOver = Estilos.criarIconeOlho(cFoco, false, null);

        ImageButton btn = new ImageButton(s);
        btn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) { lbl.setText(btn.isChecked() ? ipReal : ipOculto); }
        });
        btn.addListener(new FocusListener() {
            public void keyboardFocusChanged(FocusEvent e, Actor a, boolean f) {
                s.imageUp = Estilos.criarIconeOlho(f ? cFoco : cFraca, true, cErro);
                s.imageChecked = Estilos.criarIconeOlho(f ? cFoco : cPadrao, false, null);
            }
        });
        ordemNavegacao.add(btn);
        GerenciadorAcessibilidade.aplicarFoco(btn);
        return btn;
    }

    private TextButton criarBotaoAcao(String txt, int raio, Color corpo, Color sombra) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = fNegrito;
        s.fontColor = Color.WHITE;
        s.focusedFontColor = Color.BLACK;
        s.up = Estilos.criarBotao3D(corpo, sombra, raio, raio > 12 ? 9 : 6);
        s.over = Estilos.criarBotao3D(new Color(corpo.r*1.2f, corpo.g*1.2f, corpo.b*1.2f, 1f), sombra, raio, raio > 12 ? 9 : 6);
        s.down = Estilos.criarBotao3D(new Color(corpo.r*0.8f, corpo.g*0.8f, corpo.b*0.8f, 1f), sombra, raio, raio > 12 ? 3 : 2);
        s.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), raio, raio > 12 ? 9 : 6);

        TextButton btn = new TextButton(txt, s);
        btn.getLabel().setFontScale(1f / Estilos.MULTIPLICADOR_HD);
        ordemNavegacao.add(btn);
        GerenciadorAcessibilidade.aplicarFoco(btn);
        return btn;
    }

    private TextButton criarBotaoFino(String txt) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = fNegrito;
        s.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        s.up = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), Color.LIGHT_GRAY, 8, 2);
        s.over = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), Color.WHITE, 8, 2);
        s.down = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoDown(), Color.GRAY, 8, 2);
        s.focused = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorDestaqueFoco(), 8, 3);

        TextButton btn = new TextButton(txt, s);
        btn.getLabel().setFontScale(0.85f / Estilos.MULTIPLICADOR_HD);
        ordemNavegacao.add(btn);
        GerenciadorAcessibilidade.aplicarFoco(btn);
        return btn;
    }

    private Label criarRotulo(String txt, Label.LabelStyle s, float escala) {
        Label l = new Label(txt, s);
        l.setFontScale(escala / Estilos.MULTIPLICADOR_HD);
        return l;
    }

    @Override
    public void dispose() {
        super.dispose();
        fNormal.dispose(); fNegrito.dispose(); fInput.dispose();
        if (tracoBranco != null) tracoBranco.getTexture().dispose();
    }
}
