package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class PopUpCadastro {
    private final Stage stage;
    private Skin skin;
    private BitmapFont fontePadrao;
    private Texture texturaFundoEscuro;
    private Image cortina;
    private Dialog popupAtual;

    private Array<EventListener> ouvintesOriginais = new Array<>();

    private static final float MULTIPLICADOR_HD = 3.0f;

    public PopUpCadastro(Stage stage) {
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

    public void showSucesso() {
        ouvintesOriginais.addAll(stage.getRoot().getListeners());
        stage.getRoot().clearListeners();

        stage.addActor(cortina);
        mostrarPopUpSucesso();
    }

    public void showErro() {
        ouvintesOriginais.addAll(stage.getRoot().getListeners());
        stage.getRoot().clearListeners();

        stage.addActor(cortina);
        mostrarPopUpErro();
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
        estiloBotao.up = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombraBtn, 12, 6);
        estiloBotao.over = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombraBtn, 12, 6);
        estiloBotao.down = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombraBtn, 12, 2);
        estiloBotao.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), corSombraBtn, 12, 6);
        estiloBotao.focusedFontColor = Color.BLACK;
        skin.add("default", estiloBotao);

        Window.WindowStyle winStyle = new Window.WindowStyle();
        winStyle.titleFont = fontePadrao;
        winStyle.titleFontColor = GerenciadorAcessibilidade.getCorTextoTitulo();
        winStyle.background = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 3);
        skin.add("default", winStyle);
        skin.add("dialog", winStyle);
    }

    private void mostrarPopUpSucesso() {
        popupAtual = new Dialog("", skin, "dialog");
        popupAtual.pad(40);

        Label mensagem = new Label("Cadastro realizado com sucesso!", skin);
        mensagem.setAlignment(Align.center);
        popupAtual.getContentTable().add(mensagem).padBottom(35).row();

        final TextButton btnOk = new TextButton("OK", skin);
        GerenciadorAcessibilidade.aplicarFoco(btnOk);

        btnOk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fecharEFecharRecursos();
            }
        });

        popupAtual.getContentTable().add(btnOk).width(260).height(54);

        aplicarNavegacaoPopUp(btnOk);
        stage.setKeyboardFocus(btnOk);

        popupAtual.show(stage);

        popupAtual.pack();
        popupAtual.setPosition(Math.round((stage.getWidth() - popupAtual.getWidth()) / 2f),
            Math.round((stage.getHeight() - popupAtual.getHeight()) / 2f));
    }

    private void mostrarPopUpErro() {
        popupAtual = new Dialog("", skin, "dialog");
        popupAtual.pad(40);

        Label mensagem = new Label("Algo deu errado", skin);
        mensagem.setAlignment(Align.center);
        popupAtual.getContentTable().add(mensagem).padBottom(35).row();

        final TextButton btnOk = new TextButton("Tentar Novamente", skin);
        GerenciadorAcessibilidade.aplicarFoco(btnOk);

        btnOk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fecharEFecharRecursos();
            }
        });

        popupAtual.getContentTable().add(btnOk).width(260).height(54);

        aplicarNavegacaoPopUp(btnOk);
        stage.setKeyboardFocus(btnOk);

        popupAtual.show(stage);

        popupAtual.pack();
        popupAtual.setPosition(Math.round((stage.getWidth() - popupAtual.getWidth()) / 2f),
            Math.round((stage.getHeight() - popupAtual.getHeight()) / 2f));
    }
}
