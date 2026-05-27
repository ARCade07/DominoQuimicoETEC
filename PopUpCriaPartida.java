package com.pidomino;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class PopUpCriaPartida {
    private final Stage stage;
    private Skin skin;
    private BitmapFont fontePadrao;
    private Texture texturaFundoEscuro;
    private Image cortina;
    private Dialog popupAtual;

    private Array<EventListener> ouvintesOriginais = new Array<>();

    private static final float MULTIPLICADOR_HD = 3.0f;

    public PopUpCriaPartida(Stage stage) {
        this.stage = stage;

        Pixmap pixmapFundo = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapFundo.setColor(new Color(0, 0, 0, 0.80f));
        pixmapFundo.fill();
        texturaFundoEscuro = new Texture(pixmapFundo);
        pixmapFundo.dispose();

        cortina = new Image(texturaFundoEscuro);
        cortina.setFillParent(true);

        criarSkinAcessivel();
    }

    public void show() {
        ouvintesOriginais.addAll(stage.getRoot().getListeners());
        stage.getRoot().clearListeners();

        stage.addActor(cortina);
        mostrarPopUpMultiplayer();
    }

    private void fecharEFecharRecursos() {
        if (popupAtual != null) popupAtual.hide();
        cortina.remove();

        stage.getRoot().clearListeners();
        for (EventListener ouvinte : ouvintesOriginais) {
            stage.getRoot().addListener(ouvinte);
        }

        if (skin != null) skin.dispose();
        if (fontePadrao != null) fontePadrao.dispose();
        if (texturaFundoEscuro != null) texturaFundoEscuro.dispose();
    }

    private void aplicarNavegacaoPopUp(Actor... atores) {
        stage.getRoot().clearListeners();
        GerenciadorAcessibilidade.configurarNavegacao(stage, atores);
    }

    private void criarSkinAcessivel() {
        skin = new Skin();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Bold.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = (int) (18 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        parameter.color = GerenciadorAcessibilidade.getCorTextoPadrao();
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←.";
        parameter.borderWidth = 2f;
        parameter.borderColor = new Color(0, 0, 0, 0);

        parameter.padTop = 4;
        parameter.padBottom = 4;
        parameter.padLeft = 4;
        parameter.padRight = 4;
        parameter.spaceX = 4;
        parameter.spaceY = 4;

        fontePadrao = generator.generateFont(parameter);
        fontePadrao.setUseIntegerPositions(false);
        fontePadrao.getData().setScale(1f / MULTIPLICADOR_HD);
        generator.dispose();

        skin.add("default-font", fontePadrao);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = fontePadrao;
        labelStyle.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        skin.add("default", labelStyle);

        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean protanopia = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;
        Color corSombraBtn = altoContraste ? Color.DARK_GRAY : (protanopia ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fontePadrao;
        estiloBotao.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        estiloBotao.up = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombraBtn, 12, 6);
        estiloBotao.over = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombraBtn, 12, 6);
        estiloBotao.down = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombraBtn, 12, 2);
        estiloBotao.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), corSombraBtn, 12, 6);
        estiloBotao.focusedFontColor = Color.BLACK;
        skin.add("default", estiloBotao);

        Window.WindowStyle winStyle = new Window.WindowStyle();
        winStyle.titleFont = fontePadrao;
        winStyle.titleFontColor = GerenciadorAcessibilidade.getCorTextoTitulo();
        winStyle.background = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 3);
        skin.add("default", winStyle);
        skin.add("dialog", winStyle);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = fontePadrao;
        tfStyle.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        tfStyle.background = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorBordaCartao(), 8, 2);
        tfStyle.focusedBackground = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorDestaqueFoco(), 8, 2);
        tfStyle.focusedFontColor = GerenciadorAcessibilidade.getCorTextoPadrao();

        TextureRegionDrawable cursorTex = GerenciadorAcessibilidade.criarTexturaGradiente(GerenciadorAcessibilidade.getCorTextoPadrao(), GerenciadorAcessibilidade.getCorTextoPadrao());
        cursorTex.setMinWidth(2);
        tfStyle.cursor = cursorTex;
        skin.add("default", tfStyle);
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

    private void mostrarPopUpMultiplayer() {
        popupAtual = new Dialog("", skin, "dialog");
        popupAtual.pad(40);

        Label titulo = new Label("MULTIPLAYER", skin);
        titulo.setAlignment(Align.center);
        popupAtual.getContentTable().add(titulo).padBottom(25).row();

        Label subTitulo = new Label("Selecione uma opção para continuar:", skin);
        popupAtual.getContentTable().add(subTitulo).padBottom(35).row();

        Table botoesTable = new Table();
        botoesTable.defaults().width(260).height(54).pad(8);

        final TextButton btnHospedar = new TextButton("Hospedar Partida", skin);
        GerenciadorAcessibilidade.aplicarFoco(btnHospedar);

        btnHospedar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Hospedando partida e indo para o Lobby...");
                fecharEFecharRecursos();
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaLobby());
            }
        });
        botoesTable.add(btnHospedar).row();

        final TextButton btnEntrarIP = new TextButton("Entrar via IP", skin);
        GerenciadorAcessibilidade.aplicarFoco(btnEntrarIP);
        final Cell<Actor> celulaEntrar = botoesTable.add(btnEntrarIP);
        botoesTable.row();

        final TextButton btnCancelar = new TextButton("Voltar", skin);
        GerenciadorAcessibilidade.aplicarFoco(btnCancelar);

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fecharEFecharRecursos();
            }
        });
        botoesTable.add(btnCancelar).row();

        aplicarNavegacaoPopUp(btnHospedar, btnEntrarIP, btnCancelar);

        btnEntrarIP.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Table inputTable = new Table();

                final TextField campoIp = new TextField("", skin);
                campoIp.setAlignment(Align.center);
                GerenciadorAcessibilidade.aplicarFoco(campoIp);

                final TextButton btnIr = new TextButton("Ir", skin);
                GerenciadorAcessibilidade.aplicarFoco(btnIr);

                btnIr.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("Conectando ao IP: " + campoIp.getText() + " e indo para o Lobby...");
                        fecharEFecharRecursos();
                        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaLobby());
                    }
                });

                inputTable.add(campoIp).width(190).height(54).padRight(5);
                inputTable.add(btnIr).width(65).height(54);

                celulaEntrar.setActor(inputTable);
                popupAtual.pack();
                popupAtual.setPosition(Math.round((stage.getWidth() - popupAtual.getWidth()) / 2f),
                    Math.round((stage.getHeight() - popupAtual.getHeight()) / 2f));

                aplicarNavegacaoPopUp(btnHospedar, campoIp, btnIr, btnCancelar);

                stage.setKeyboardFocus(campoIp);
            }
        });

        popupAtual.getContentTable().add(botoesTable);
        popupAtual.show(stage);

        popupAtual.pack();
        popupAtual.setPosition(Math.round((stage.getWidth() - popupAtual.getWidth()) / 2f),
            Math.round((stage.getHeight() - popupAtual.getHeight()) / 2f));
    }
}
