package com.domino.telas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class PopUpAcessibilidade {
    private final Stage stage;
    private Skin skin;

    private BitmapFont fontePadrao;
    private BitmapFont fonteTitulo;
    private BitmapFont fontePequena;
    private BitmapFont fonteMedia;
    private BitmapFont fonteGrande;

    private Texture texturaFundoEscuro;
    private Image cortina;
    private Dialog popupAtual;

    private Array<EventListener> ouvintesOriginais = new Array<>();

    private static final float MULTIPLICADOR_HD = 3.0f;

    public PopUpAcessibilidade(Stage stage) {
        this.stage = stage;

        Pixmap pixmapFundo = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapFundo.setColor(new Color(0, 0, 0, 0.80f)); // Fundo escuro padrão do PopUpCriaPartida
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
        mostrarPopUpAcessibilidade();
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
        if (fonteTitulo != null) fonteTitulo.dispose();
        if (fontePequena != null) fontePequena.dispose();
        if (fonteMedia != null) fonteMedia.dispose();
        if (fonteGrande != null) fonteGrande.dispose();
        if (texturaFundoEscuro != null) texturaFundoEscuro.dispose();
    }

    private void atualizarPopUp() {
        if (popupAtual != null) {
            popupAtual.remove();
        }
        if (skin != null) skin.dispose();

        criarSkinAcessivel();
        mostrarPopUpAcessibilidade();
    }

    private void aplicarNavegacaoPopUp(Actor... atores) {
        stage.getRoot().clearListeners();
        GerenciadorAcessibilidade.configurarNavegacao(stage, atores);
    }

    private void criarSkinAcessivel() {
        skin = new Skin();

        float tamanhoBase = 14 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario();

        fontePadrao = Estilos.gerarFonte("Inter_24pt-Medium.ttf", tamanhoBase, 4, 2f);
        fontePadrao.getData().setScale(1f / MULTIPLICADOR_HD);

        fonteTitulo = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 22 * MULTIPLICADOR_HD, 4, 2f);
        fonteTitulo.getData().setScale(1f / MULTIPLICADOR_HD);

        fontePequena = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 16 * MULTIPLICADOR_HD, 4, 2f);
        fontePequena.getData().setScale(1f / MULTIPLICADOR_HD);

        fonteMedia = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 24 * MULTIPLICADOR_HD,4,2f);
        fonteMedia.getData().setScale(1f / MULTIPLICADOR_HD);

        fonteGrande = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 32 * MULTIPLICADOR_HD,4,2f);
        fonteGrande.getData().setScale(1f / MULTIPLICADOR_HD);

        Color corTexto = GerenciadorAcessibilidade.getCorTextoPadrao();
        Color corTitulo = GerenciadorAcessibilidade.getCorTextoTitulo();
        Color corFundo = GerenciadorAcessibilidade.getCorFundoCartao();
        Color corBordaDialog = GerenciadorAcessibilidade.getCorBordaForte();

        Color corFundoCardNormal = GerenciadorAcessibilidade.getCorFundoTela();
        Color corBordaCardNormal = GerenciadorAcessibilidade.getCorBordaCartao();
        Color corFundoCardSelecionado = GerenciadorAcessibilidade.getCorFundoBotaoNormal();
        Color corBordaCardSelecionado = GerenciadorAcessibilidade.getCorBordaForte();

        Label.LabelStyle lblPadrao = new Label.LabelStyle(fontePadrao, corTexto);
        skin.add("default", lblPadrao);

        Label.LabelStyle lblTitulo = new Label.LabelStyle(fonteTitulo, corTitulo);
        skin.add("title", lblTitulo);

        Label.LabelStyle lblSubTitulo = new Label.LabelStyle(fontePadrao, corTitulo);
        skin.add("subtitle", lblSubTitulo);

        skin.add("icon-small", new Label.LabelStyle(fontePequena, corTexto));
        skin.add("icon-medium", new Label.LabelStyle(fonteMedia, corTexto));
        skin.add("icon-large", new Label.LabelStyle(fonteGrande, corTexto));

        Window.WindowStyle winStyle = new Window.WindowStyle();
        winStyle.titleFont = fontePadrao;
        winStyle.titleFontColor = corTitulo;
        winStyle.background = criarBordaArredondadaTextura(corFundo, corBordaDialog, 16, 3);
        skin.add("dialog", winStyle);

        Button.ButtonStyle cardUnselected = new Button.ButtonStyle();
        cardUnselected.up = criarBordaArredondadaTextura(corFundoCardNormal, corBordaCardNormal, 16, 2);
        cardUnselected.over = criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corBordaCardNormal, 16, 2);
        skin.add("card-unselected", cardUnselected);

        Button.ButtonStyle cardSelected = new Button.ButtonStyle();
        cardSelected.up = criarBordaArredondadaTextura(corFundoCardSelecionado, corBordaCardSelecionado, 16, 4);
        skin.add("card-selected", cardSelected);

        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean protanopia = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;
        Color corSombraBtn = altoContraste ? Color.DARK_GRAY : (protanopia ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton.TextButtonStyle btnVoltarStyle = new TextButton.TextButtonStyle();
        btnVoltarStyle.font = fontePadrao;
        btnVoltarStyle.fontColor = corTexto;
        btnVoltarStyle.up = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombraBtn, 12, 6);
        btnVoltarStyle.over = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombraBtn, 12, 6);
        btnVoltarStyle.down = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombraBtn, 12, 2);
        btnVoltarStyle.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), corSombraBtn, 12, 6);
        btnVoltarStyle.focusedFontColor = Color.BLACK;
        skin.add("btn-voltar", btnVoltarStyle);
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

    private Button criarBotaoCard(String texto, String estiloIcone, boolean selecionado) {
        Button btn = new Button(skin, selecionado ? "card-selected" : "card-unselected");
        Table t = new Table();

        if (estiloIcone != null) {
            Label lblIcone = new Label("A", skin, estiloIcone);
            lblIcone.setAlignment(Align.center);
            t.add(lblIcone).padBottom(8).row();
        }

        Label lblTexto = new Label(texto, skin, "default");
        lblTexto.setAlignment(Align.center);
        lblTexto.setWrap(true);
        t.add(lblTexto).width(120);

        btn.add(t).expand().center().pad(10);
        return btn;
    }

    private Image criarLinhaDivisoria() {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(GerenciadorAcessibilidade.getCorBordaCartao());
        pix.fill();
        Texture tex = new Texture(pix);
        pix.dispose();
        Image linha = new Image(tex);
        return linha;
    }

    private void mostrarPopUpAcessibilidade() {
        popupAtual = new Dialog("", skin, "dialog");
        popupAtual.pad(40);

        Label titulo = new Label("ACESSIBILIDADE", skin, "title");
        titulo.setAlignment(Align.center);
        popupAtual.getContentTable().add(titulo).padBottom(15).colspan(4).row();

        popupAtual.getContentTable().add(criarLinhaDivisoria()).height(2).fillX().colspan(4).padBottom(20).row();

        Label subTituloVisao = new Label("MODO DE VISÃO", skin, "subtitle");
        subTituloVisao.setAlignment(Align.center);
        popupAtual.getContentTable().add(subTituloVisao).padBottom(15).colspan(4).row();

        Table visaoTable = new Table();
        visaoTable.defaults().width(140).height(120).pad(6);

        final Button btnPadrao = criarBotaoCard("Padrão", null, GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PADRAO);
        GerenciadorAcessibilidade.aplicarFoco(btnPadrao);
        btnPadrao.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.modoVisaoAtual = GerenciadorAcessibilidade.ModoVisao.PADRAO;
                atualizarPopUp();
            }
        });
        visaoTable.add(btnPadrao);

        final Button btnAltoContraste = criarBotaoCard("Alto\nContraste", null, GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE);
        GerenciadorAcessibilidade.aplicarFoco(btnAltoContraste);
        btnAltoContraste.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.modoVisaoAtual = GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
                atualizarPopUp();
            }
        });
        visaoTable.add(btnAltoContraste);

        final Button btnProtanopia = criarBotaoCard("Protanopia /\nDeuteranopia", null, GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA);
        GerenciadorAcessibilidade.aplicarFoco(btnProtanopia);
        btnProtanopia.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.modoVisaoAtual = GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;
                atualizarPopUp();
            }
        });
        visaoTable.add(btnProtanopia);

        final Button btnTritanopia = criarBotaoCard("Tritanopia", null, GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.TRITANOPIA);
        GerenciadorAcessibilidade.aplicarFoco(btnTritanopia);
        btnTritanopia.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.modoVisaoAtual = GerenciadorAcessibilidade.ModoVisao.TRITANOPIA;
                atualizarPopUp();
            }
        });
        visaoTable.add(btnTritanopia);

        popupAtual.getContentTable().add(visaoTable).padBottom(20).colspan(4).row();

        popupAtual.getContentTable().add(criarLinhaDivisoria()).height(2).fillX().colspan(4).padBottom(20).row();

        Label subTituloFonte = new Label("TAMANHO DA FONTE", skin, "subtitle");
        subTituloFonte.setAlignment(Align.center);
        popupAtual.getContentTable().add(subTituloFonte).padBottom(15).colspan(4).row();

        Table fonteTable = new Table();
        fonteTable.defaults().width(140).height(120).pad(10);

        final Button btnFontePequena = criarBotaoCard("Pequena", "icon-small", GerenciadorAcessibilidade.tamanhoFonteAtual == GerenciadorAcessibilidade.TamanhoFonte.PEQUENO);
        GerenciadorAcessibilidade.aplicarFoco(btnFontePequena);
        btnFontePequena.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.tamanhoFonteAtual = GerenciadorAcessibilidade.TamanhoFonte.PEQUENO;
                atualizarPopUp();
            }
        });
        fonteTable.add(btnFontePequena);

        final Button btnFonteMedia = criarBotaoCard("Média", "icon-medium", GerenciadorAcessibilidade.tamanhoFonteAtual == GerenciadorAcessibilidade.TamanhoFonte.MEDIO);
        GerenciadorAcessibilidade.aplicarFoco(btnFonteMedia);
        btnFonteMedia.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.tamanhoFonteAtual = GerenciadorAcessibilidade.TamanhoFonte.MEDIO;
                atualizarPopUp();
            }
        });
        fonteTable.add(btnFonteMedia);

        final Button btnFonteGrande = criarBotaoCard("Grande", "icon-large", GerenciadorAcessibilidade.tamanhoFonteAtual == GerenciadorAcessibilidade.TamanhoFonte.GRANDE);
        GerenciadorAcessibilidade.aplicarFoco(btnFonteGrande);
        btnFonteGrande.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GerenciadorAcessibilidade.tamanhoFonteAtual = GerenciadorAcessibilidade.TamanhoFonte.GRANDE;
                atualizarPopUp();
            }
        });
        fonteTable.add(btnFonteGrande);

        popupAtual.getContentTable().add(fonteTable).padBottom(30).colspan(4).row();

        final TextButton btnVoltar = new TextButton("Voltar", skin, "btn-voltar");
        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);
        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fecharEFecharRecursos();
            }
        });

        popupAtual.getContentTable().add(btnVoltar).width(260).height(54).colspan(4).align(Align.center).row();

        aplicarNavegacaoPopUp(btnPadrao, btnAltoContraste, btnProtanopia, btnTritanopia, btnFontePequena, btnFonteMedia, btnFonteGrande, btnVoltar);

        popupAtual.show(stage);
        popupAtual.pack();
        popupAtual.setPosition(Math.round((stage.getWidth() - popupAtual.getWidth()) / 2f),
            Math.round((stage.getHeight() - popupAtual.getHeight()) / 2f));
    }
}
