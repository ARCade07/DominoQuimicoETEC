package com.pidomino;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class TelaTutorial implements Screen {

    private Stage palco;
    private Skin tema;

    //converte a fonte para o tamanho do layout
    private static final float MULTIPLICADOR_HD = 3.0f;

    public TelaTutorial() {
    }

    @Override
    public void show() {
        palco = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(palco);
        tema = new Skin();

        //geradores de fontes
        //cria uma fonte gigante para redimensionar sem perder qualidade quando diminuir ou aumentar a tela
        FreeTypeFontGenerator geradorNormal = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Medium.ttf"));
        FreeTypeFontParameter parametroNormal = new FreeTypeFontParameter();
        parametroNormal.size = (int) (24 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        parametroNormal.color = Color.WHITE;
        parametroNormal.genMipMaps = true;
        parametroNormal.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNormal.magFilter = Texture.TextureFilter.Linear;
        parametroNormal.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        BitmapFont fonteNormal = geradorNormal.generateFont(parametroNormal);
        geradorNormal.dispose();

        //desliga o arredondamento de pixels para nao atrapalhar o redimensionamento
        fonteNormal.setUseIntegerPositions(false);

        //resolve o texture bleeding
        parametroNormal.padTop = 4;
        parametroNormal.padBottom = 4;
        parametroNormal.padLeft = 4;
        parametroNormal.padRight = 4;
        parametroNormal.spaceX = 4;
        parametroNormal.spaceY = 4;

        //cria uma fonte gigante em negrito para redimensionar sem perder qualidade quando diminuir ou aumentar a tela
        FreeTypeFontGenerator geradorNegrito = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Bold.ttf"));
        FreeTypeFontParameter parametroNegrito = new FreeTypeFontParameter();
        parametroNegrito.size = (int) (24 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        parametroNegrito.color = Color.WHITE;
        parametroNegrito.genMipMaps = true;
        parametroNegrito.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNegrito.magFilter = Texture.TextureFilter.Linear;
        parametroNegrito.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        BitmapFont fonteNegrito = geradorNegrito.generateFont(parametroNegrito);
        geradorNegrito.dispose();

        //desliga o arredondamento de pixels para nao atrapalhar o redimensionamento
        fonteNegrito.setUseIntegerPositions(false);

        //resolve o texture bleeding
        parametroNegrito.padTop = 4;
        parametroNegrito.padBottom = 4;
        parametroNegrito.padLeft = 4;
        parametroNegrito.padRight = 4;
        parametroNegrito.spaceX = 4;
        parametroNegrito.spaceY = 4;

        //estilos de texto
        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloSubtitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloTexto = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoPadrao());
        Label.LabelStyle estiloVerde = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorDestaqueSucesso());
        Label.LabelStyle estiloVermelho = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorDestaqueErro());

        //layout principal
        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.setBackground(criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()));
        palco.addActor(raiz);

        //cor do botao 3d
        Color corVinho = GerenciadorAcessibilidade.getCorDestaqueErro();
        boolean isAltoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        Color corSombra = isAltoContraste ? Color.DARK_GRAY : Color.valueOf("4D0000");

        //tudo do botao
        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fonteNegrito;
        estiloBotao.fontColor = Color.WHITE;

        //botao normal
        estiloBotao.up = criarBotao3D(corVinho, corSombra, 12, 6);

        //botao com o mouse em Cima
        estiloBotao.over = criarBotao3D(corVinho.cpy().add(0.1f, 0.1f, 0.1f, 0), corSombra, 12, 6);

        //botao ao ser clicado
        estiloBotao.down = criarBotao3D(corVinho, corSombra, 12, 2);

        //botao ao ser focado pelo teclado
        estiloBotao.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 12, 6);
        estiloBotao.focusedFontColor = Color.BLACK;

        TextButton btnVoltar = new TextButton("← VOLTAR", estiloBotao);
        btnVoltar.getLabel().setFontScale(1f / MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);

        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnVoltar.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaRanking());
            }
        });

        //teste das cores de daltonismo apagar depois
        TextButton btnTesteAcessibilidade = new TextButton("MODO: " + GerenciadorAcessibilidade.modoVisaoAtual, estiloBotao);
        btnTesteAcessibilidade.getLabel().setFontScale(0.8f / MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnTesteAcessibilidade);

        btnTesteAcessibilidade.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnTesteAcessibilidade.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                int totalModos = GerenciadorAcessibilidade.ModoVisao.values().length;
                int proximoIndice = (GerenciadorAcessibilidade.modoVisaoAtual.ordinal() + 1) % totalModos;
                GerenciadorAcessibilidade.modoVisaoAtual = GerenciadorAcessibilidade.ModoVisao.values()[proximoIndice];
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaTutorial());
            }
        });

        //teste do tamanho das letras apagar depois
        TextButton btnTesteTamanho = new TextButton("FONTE: " + GerenciadorAcessibilidade.tamanhoFonteAtual, estiloBotao);
        btnTesteTamanho.getLabel().setFontScale(0.8f / MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnTesteTamanho);

        btnTesteTamanho.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnTesteTamanho.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                int totalTamanhos = GerenciadorAcessibilidade.TamanhoFonte.values().length;
                int proximoIndice = (GerenciadorAcessibilidade.tamanhoFonteAtual.ordinal() + 1) % totalTamanhos;
                GerenciadorAcessibilidade.tamanhoFonteAtual = GerenciadorAcessibilidade.TamanhoFonte.values()[proximoIndice];
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new TelaTutorial());
            }
        });

        //camada superior com posicao absoluta
        Table layerSuperior = new Table();
        layerSuperior.setFillParent(true);
        layerSuperior.top().left();
        layerSuperior.add(btnVoltar).width(180).height(65).pad(20);
        layerSuperior.add().expandX();

        //caixa direita para os testes
        Table caixaTestes = new Table();
        caixaTestes.add(btnTesteAcessibilidade).width(230).height(50).padRight(15);
        caixaTestes.add(btnTesteTamanho).width(230).height(50);
        layerSuperior.add(caixaTestes).pad(20).right();

        palco.addActor(layerSuperior);

        Table cabecalho = new Table();
        Label lblTitulo = criarRotulo("COMO JOGAR", estiloTitulo, 2.0f);
        cabecalho.add(lblTitulo).expandX().center().padTop(40).padBottom(30);

        raiz.add(cabecalho).growX().top().row();

        Table conteudo = new Table();

        Table colunaEsquerda = criarColunaEsquerda(estiloSubtitulo, estiloTexto);
        Table colunaDireita = criarColunaDireita(estiloSubtitulo, estiloTexto, estiloVerde, estiloVermelho, fonteNormal);

        conteudo.add(colunaEsquerda).width(Value.percentWidth(0.40f, raiz)).top().pad(20).padLeft(30);
        conteudo.add(colunaDireita).width(Value.percentWidth(0.55f, raiz)).top().pad(20).padRight(30);

        raiz.add(conteudo).grow().top();

        GerenciadorAcessibilidade.configurarNavegacao(palco, btnVoltar, btnTesteAcessibilidade, btnTesteTamanho);
    }

    //ajusta a qualidade das letras
    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escalaDesejada / MULTIPLICADOR_HD);
        return rotulo;
    }

    //quebra de linha automatica
    private Label criarRotuloComQuebra(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = criarRotulo(texto, estilo, escalaDesejada);
        rotulo.setWrap(true);
        return rotulo;
    }

    //componentes da tela, tudo pra baixo sao os cartoes pra colocar os textos
    private Table criarCartaoTexto(String titulo, String texto, Label.LabelStyle estiloTitulo, Label.LabelStyle estiloTexto) {
        Table cartao = new Table();
        cartao.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 12, 2));
        cartao.pad(15);

        Label lblTitulo = criarRotulo(titulo, estiloTitulo, 1.0f);
        cartao.add(lblTitulo).left().padBottom(5).row();

        Label lblTexto = criarRotuloComQuebra(texto, estiloTexto, 1.0f);
        cartao.add(lblTexto).growX().left();

        return cartao;
    }

    private Table criarColunaEsquerda(Label.LabelStyle estiloSubtitulo, Label.LabelStyle estiloTexto) {
        Table coluna = new Table();
        coluna.top().left();

        Label t1 = criarRotulo("1. REGRAS BÁSICAS", estiloSubtitulo, 1.3f);
        coluna.add(t1).left().padBottom(20).row();

        //cartoes alinhados com o conteudo da direita
        coluna.add(criarCartaoTexto("1. OBJETIVO DO JOGO", "Ser o primeiro jogador a ficar sem peças ou ter a maior pontuação final.", estiloSubtitulo, estiloTexto)).growX().padBottom(35).row();
        coluna.add(criarCartaoTexto("2. DISTRIBUIÇÃO E MONTE", "Cada jogador começa com 7 peças. As restantes formam o Monte no canto esquerdo da mesa.", estiloSubtitulo, estiloTexto)).growX().padBottom(30).row();
        coluna.add(criarCartaoTexto("3. O TURNO", "Encaixe uma peça compatível em uma das pontas. Se não tiver, compre do Monte.", estiloSubtitulo, estiloTexto)).growX().row();

        return coluna;
    }

    private Table criarColunaDireita(Label.LabelStyle estiloSubtitulo, Label.LabelStyle estiloTexto,
                                     Label.LabelStyle estiloVerde, Label.LabelStyle estiloVermelho, BitmapFont fonteOriginal) {
        Table coluna = new Table();
        coluna.top().left();

        Label t2 = criarRotulo("2. MECÂNICA DE ENCAIXE QUÍMICO", estiloSubtitulo, 1.3f);
        coluna.add(t2).left().padBottom(20).row();

        Table caixaDestaque = new Table();
        caixaDestaque.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCaixaDestaqueErro(), GerenciadorAcessibilidade.getCorDestaqueErro(), 12, 2));
        caixaDestaque.pad(15);

        Label.LabelStyle estiloTituloVermelho = new Label.LabelStyle(fonteOriginal, GerenciadorAcessibilidade.getCorDestaqueErro());
        Label lblCaixaTitulo = criarRotulo("A REGRA MAIS IMPORTANTE:", estiloTituloVermelho, 1.0f);
        lblCaixaTitulo.setAlignment(Align.center);
        caixaDestaque.add(lblCaixaTitulo).row();

        Label lblCaixaTexto = criarRotuloComQuebra("Conecte peças da MESMA FUNÇÃO INORGÂNICA ou\npor correspondência direta (NOME ↔ FÓRMULA).", estiloTexto, 1.0f);
        lblCaixaTexto.setAlignment(Align.center);
        caixaDestaque.add(lblCaixaTexto).growX().padTop(5).row();

        coluna.add(caixaDestaque).growX().padBottom(30).row();

        coluna.add(criarRotulo("A. ENCAIXE CORRETO (Acerto)", estiloVerde, 1.0f)).left().row();
        coluna.add(criarRotulo("As duas pontas são da função Ácido.", estiloTexto, 1.0f)).left().padBottom(10).row();

        Table tabelaAcerto = new Table();

        tabelaAcerto.add(criarDomino("Ácido", "HCl", estiloSubtitulo)).size(210, 70);
        tabelaAcerto.add(criarRotulo(" ↔ ", estiloVerde, 1.0f)).pad(10);
        tabelaAcerto.add(criarDomino("Ácido\nClorídrico", "Base", estiloSubtitulo)).size(210, 70);
        coluna.add(tabelaAcerto).left().padBottom(30).row();

        coluna.add(criarRotulo("B. ENCAIXE INCORRETO (Erro)", estiloVermelho, 1.0f)).left().row();
        coluna.add(criarRotulo("NaOH é uma Base e não se conecta com Óxido.", estiloTexto, 1.0f)).left().padBottom(10).row();

        Table tabelaErro = new Table();

        tabelaErro.add(criarDomino("Sal", "NaOH", estiloSubtitulo)).size(210, 70);
        tabelaErro.add(criarRotulo(" ↔ ", estiloVermelho, 1.0f)).pad(10);
        tabelaErro.add(criarDomino("Óxido", "NaCl", estiloSubtitulo)).size(210, 70);
        coluna.add(tabelaErro).left().row();

        return coluna;
    }

    private TextureRegionDrawable criarTexturaCor(Color cor) {
        Pixmap mapaPixels = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        mapaPixels.setColor(cor);
        mapaPixels.fill();
        Texture textura = new Texture(mapaPixels);
        textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();
        return new TextureRegionDrawable(textura);
    }

    //cria botao com profundidade 3d
    private NinePatchDrawable criarBotao3D(Color corCorpo, Color corSombra, int raio, int profundidade) {
        int escala = 4;
        int t = 100 * escala;
        int r = raio * escala;
        int p = profundidade * escala;

        Pixmap pix = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);

        //desenha a sombra
        pix.setColor(corSombra);
        pix.fillCircle(r, t-r-1, r); pix.fillCircle(t-r-1, t-r-1, r);
        pix.fillRectangle(r, t-2*r, t-2*r, 2*r);
        pix.fillRectangle(0, t-r-1-p, t, p);

        //desenha o corpo
        pix.setColor(corCorpo);
        pix.fillCircle(r, r, r); pix.fillCircle(t-r-1, r, r);
        pix.fillCircle(r, t-r-1-p, r); pix.fillCircle(t-r-1, t-r-1-p, r);
        pix.fillRectangle(r, 0, t-2*r, t-p);
        pix.fillRectangle(0, r, t, t-2*r-p);

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();

        NinePatch np = new NinePatch(tex, r, r, r, r + p);
        np.scale(1f/escala, 1f/escala);
        return new NinePatchDrawable(np);
    }

    private NinePatchDrawable criarBordaArredondadaTextura(Color corFundo, Color corBorda, int raio, int tamanhoBorda) {
        int escala = 4;
        int tamanho = 100 * escala;
        int r = raio * escala;
        int b = tamanhoBorda * escala;

        Pixmap mapaPixels = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        mapaPixels.setBlending(Pixmap.Blending.None);

        mapaPixels.setColor(corBorda);
        mapaPixels.fillCircle(r, r, r);
        mapaPixels.fillCircle(tamanho - r - 1, r, r);
        mapaPixels.fillCircle(r, tamanho - r - 1, r);
        mapaPixels.fillCircle(tamanho - r - 1, tamanho - r - 1, r);
        mapaPixels.fillRectangle(r, 0, tamanho - 2 * r, tamanho);
        mapaPixels.fillRectangle(0, r, tamanho, tamanho - 2 * r);

        mapaPixels.setColor(corFundo);
        int raioInterno = r - b;
        if (raioInterno < 0) raioInterno = 0;
        mapaPixels.fillCircle(r, r, raioInterno);
        mapaPixels.fillCircle(tamanho - r - 1, r, raioInterno);
        mapaPixels.fillCircle(r, tamanho - r - 1, raioInterno);
        mapaPixels.fillCircle(tamanho - r - 1, tamanho - r - 1, raioInterno);
        mapaPixels.fillRectangle(r, b, tamanho - 2 * r, tamanho - 2 * b);
        mapaPixels.fillRectangle(b, r, tamanho - 2 * b, tamanho - 2 * r);

        Texture textura = new Texture(mapaPixels, true);
        textura.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();

        NinePatch remendo = new NinePatch(textura, r, r, r, r);
        remendo.scale(1f / escala, 1f / escala);

        return new NinePatchDrawable(remendo);
    }

    private Table criarDomino(String textoEsquerda, String textoDireita, Label.LabelStyle estilo) {
        Table domino = new Table();
        domino.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 12, 2));

        Label lblEsquerda = criarRotuloComQuebra(textoEsquerda, estilo, 0.70f);
        lblEsquerda.setAlignment(Align.center);
        domino.add(lblEsquerda).expand().fill().pad(5);

        Image separador = new Image(criarTexturaCor(GerenciadorAcessibilidade.getCorBordaForte()));
        domino.add(separador).width(2).growY();

        Label lblDireita = criarRotuloComQuebra(textoDireita, estilo, 0.70f);
        lblDireita.setAlignment(Align.center);
        domino.add(lblDireita).expand().fill().pad(5);

        return domino;
    }

    @Override
    public void render(float delta) {
        Color corFundo = GerenciadorAcessibilidade.getCorFundoTela();
        Gdx.gl.glClearColor(corFundo.r, corFundo.g, corFundo.b, corFundo.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        palco.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        palco.draw();
    }

    @Override
    public void resize(int largura, int altura) {
        palco.getViewport().update(largura, altura, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        palco.dispose();
        tema.dispose();
    }
}
