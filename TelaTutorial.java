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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class TelaTutorial implements Screen {

    private Stage palco;
    private Skin tema;
    private static final float MULTIPLICADOR_HD = 3.0f;

    public TelaTutorial() {}

    @Override
    public void show() {
        //viewport expandido para preencher as laterais da tela
        palco = new Stage(new ExtendViewport(1920, 1080));
        Gdx.input.setInputProcessor(palco);
        tema = new Skin();

        //geradores de fontes
        FreeTypeFontGenerator geradorNormal = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Medium.ttf"));
        FreeTypeFontParameter parametroNormal = new FreeTypeFontParameter();
        parametroNormal.size = (int) (36 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        parametroNormal.color = Color.WHITE;
        parametroNormal.genMipMaps = true;
        parametroNormal.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNormal.magFilter = Texture.TextureFilter.Linear;
        parametroNormal.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        parametroNormal.borderWidth = 2f;
        parametroNormal.borderColor = new Color(0, 0, 0, 0);
        BitmapFont fonteNormal = geradorNormal.generateFont(parametroNormal);
        fonteNormal.setUseIntegerPositions(false);
        geradorNormal.dispose();

        FreeTypeFontGenerator geradorNegrito = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Bold.ttf"));
        FreeTypeFontParameter parametroNegrito = new FreeTypeFontParameter();
        parametroNegrito.size = (int) (36 * MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario());
        parametroNegrito.color = Color.WHITE;
        parametroNegrito.genMipMaps = true;
        parametroNegrito.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNegrito.magFilter = Texture.TextureFilter.Linear;
        parametroNegrito.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        parametroNegrito.borderWidth = 2f;
        parametroNegrito.borderColor = new Color(0, 0, 0, 0);
        BitmapFont fonteNegrito = geradorNegrito.generateFont(parametroNegrito);
        fonteNegrito.setUseIntegerPositions(false);
        geradorNegrito.dispose();

        //acessibilidade
        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean protanopia = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;
        Color corFundo = GerenciadorAcessibilidade.getCorFundoTela();

        //estilos de texto
        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloSubtitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloTexto = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoPadrao());
        Label.LabelStyle estiloVerde = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorDestaqueSucesso());
        Label.LabelStyle estiloVermelho = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorDestaqueErro());

        //layout principal
        Table raiz = new Table();
        raiz.setFillParent(true);

        //fundo em degrade sincronizado dinamicamente para respeitar modos de visao
        if (altoContraste) {
            raiz.setBackground(criarTexturaCor(corFundo));
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

        //configuracao dos botoes baseada no gerenciador evitando sombras vermelhas no tema azul
        Color corSombra = altoContraste ? Color.DARK_GRAY : (protanopia ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));
        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fonteNegrito;
        estiloBotao.fontColor = Color.WHITE;
        estiloBotao.up = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombra, 18, 9);
        estiloBotao.over = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombra, 18, 9);
        estiloBotao.down = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombra, 18, 3);
        estiloBotao.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 18, 9);
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

        Table layerSuperior = new Table();
        layerSuperior.setFillParent(true);
        layerSuperior.top().left();
        layerSuperior.add(btnVoltar).width(270).height(98).pad(30);
        layerSuperior.add().expandX();
        Table caixaTestes = new Table();
        layerSuperior.add(caixaTestes).pad(30).right();
        palco.addActor(layerSuperior);

        Table cabecalho = new Table();
        Label lblTitulo = criarRotulo("COMO JOGAR", estiloTitulo, 2.0f);
        cabecalho.add(lblTitulo).expandX().center().padTop(120).padBottom(45);
        raiz.add(cabecalho).growX().top().row();

        Table conteudo = new Table();
        Table colunaEsquerda = criarColunaEsquerda(estiloSubtitulo, estiloTexto);
        Table colunaDireita = criarColunaDireita(estiloSubtitulo, estiloTexto, estiloVerde, estiloVermelho);
        conteudo.add(colunaEsquerda).width(Value.percentWidth(0.40f, raiz)).top().pad(30).padLeft(45);
        conteudo.add(colunaDireita).width(Value.percentWidth(0.55f, raiz)).top().pad(30).padRight(45);
        raiz.add(conteudo).grow().top();

        GerenciadorAcessibilidade.configurarNavegacao(palco, btnVoltar);
    }

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escalaDesejada / MULTIPLICADOR_HD);

        return rotulo;
    }

    private Label criarRotuloComQuebra(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = criarRotulo(texto, estilo, escalaDesejada);
        rotulo.setWrap(true);

        return rotulo;
    }

    private Table criarCartaoTexto(String titulo, String texto, Label.LabelStyle estiloTitulo, Label.LabelStyle estiloTexto) {
        Table cartao = new Table();
        cartao.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
        cartao.pad(22);

        Label lblTitulo = criarRotulo(titulo, estiloTitulo, 1.0f);
        cartao.add(lblTitulo).left().padBottom(7).row();
        Label lblTexto = criarRotuloComQuebra(texto, estiloTexto, 1.0f);
        cartao.add(lblTexto).growX().left();

        return cartao;
    }

    private Table criarColunaEsquerda(Label.LabelStyle estiloSubtitulo, Label.LabelStyle estiloTexto) {
        Table coluna = new Table();
        coluna.top().left();
        Label t1 = criarRotulo("1. REGRAS BÁSICAS", estiloSubtitulo, 1.3f);
        coluna.add(t1).left().padBottom(30).row();

        coluna.add(criarCartaoTexto("1. OBJETIVO DO JOGO", "Ser o primeiro jogador a ficar sem peças ou ter a maior pontuação final.", estiloSubtitulo, estiloTexto)).growX().padBottom(52).row();
        coluna.add(criarCartaoTexto("2. DISTRIBUIÇÃO E MONTE", "Cada jogador começa com 7 peças. As restantes formam o Monte no canto esquerdo da mesa.", estiloSubtitulo, estiloTexto)).growX().padBottom(45).row();
        coluna.add(criarCartaoTexto("3. O TURNO", "Encaixe uma peça compatível em uma das pontas. Se não tiver, compre do Monte.", estiloSubtitulo, estiloTexto)).growX().row();
        return coluna;
    }

    private Table criarColunaDireita(Label.LabelStyle estiloSubtitulo, Label.LabelStyle estiloTexto,
                                     Label.LabelStyle estiloVerde, Label.LabelStyle estiloVermelho) {
        Table coluna = new Table();
        coluna.top().left();
        Label t2 = criarRotulo("2. MECÂNICA DE ENCAIXE QUÍMICO", estiloSubtitulo, 1.3f);
        coluna.add(t2).left().padBottom(30).row();

        Table caixaDestaque = new Table();
        caixaDestaque.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
        caixaDestaque.pad(22);

        Label lblCaixaTitulo = criarRotulo("A REGRA MAIS IMPORTANTE:", estiloSubtitulo, 1.0f);
        lblCaixaTitulo.setAlignment(Align.center);
        caixaDestaque.add(lblCaixaTitulo).center().padBottom(7).row();

        Label lblCaixaTexto = criarRotuloComQuebra("Conecte peças da MESMA FUNÇÃO INORGÂNICA ou\npor correspondência direta (NOME ↔ FÓRMULA).", estiloTexto, 1.0f);
        lblCaixaTexto.setAlignment(Align.center);
        caixaDestaque.add(lblCaixaTexto).growX().center();

        coluna.add(caixaDestaque).growX().padBottom(45).row();

        coluna.add(criarRotulo("A. ENCAIXE CORRETO (Acerto)", estiloVerde, 1.0f)).left().row();
        coluna.add(criarRotulo("As duas pontas são da função Ácido.", estiloTexto, 1.0f)).left().padBottom(15).row();

        Table tabelaAcerto = new Table();
        tabelaAcerto.add(criarDomino("Ácido", "HCl", estiloSubtitulo)).size(315, 105);
        tabelaAcerto.add(criarRotulo(" ↔ ", estiloVerde, 1.0f)).pad(15);
        tabelaAcerto.add(criarDomino("Ácido\nClorídrico", "Base", estiloSubtitulo)).size(315, 105);
        coluna.add(tabelaAcerto).left().padBottom(45).row();

        coluna.add(criarRotulo("B. ENCAIXE INCORRETO (Erro)", estiloVermelho, 1.0f)).left().row();
        coluna.add(criarRotulo("NaOH é uma Base e não se conecta com Óxido.", estiloTexto, 1.0f)).left().padBottom(15).row();

        Table tabelaErro = new Table();
        tabelaErro.add(criarDomino("Sal", "NaOH", estiloSubtitulo)).size(315, 105);
        tabelaErro.add(criarRotulo(" ↔ ", estiloVermelho, 1.0f)).pad(15);
        tabelaErro.add(criarDomino("Óxido", "NaCl", estiloSubtitulo)).size(315, 105);
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

    private NinePatchDrawable criarBotao3D(Color corCorpo, Color corSombra, int raio, int profundidade) {
        int escala = 4, t = 100 * escala, r = raio * escala, p = profundidade * escala;
        Pixmap pix = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(corSombra);
        pix.fillCircle(r, t-r-1, r); pix.fillCircle(t-r-1, t-r-1, r);
        pix.fillRectangle(r, t-2*r, t-2*r, 2*r);
        pix.fillRectangle(0, t-r-1-p, t, p);
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
        int escala = 4, tamanho = 100 * escala, r = raio * escala, b = tamanhoBorda * escala;
        Pixmap pix = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(corBorda);
        pix.fillCircle(r, r, r);
        pix.fillCircle(tamanho - r - 1, r, r);
        pix.fillCircle(r, tamanho - r - 1, r);
        pix.fillCircle(tamanho - r - 1, tamanho - r - 1, r);
        pix.fillRectangle(r, 0, tamanho - 2 * r, tamanho);
        pix.fillRectangle(0, r, tamanho, tamanho - 2 * r);
        pix.setColor(corFundo);
        int ri = r - b;
        if (ri < 0) ri = 0;
        pix.fillCircle(r, r, ri);
        pix.fillCircle(tamanho - r - 1, r, ri);
        pix.fillCircle(r, tamanho - r - 1, ri);
        pix.fillCircle(tamanho - r - 1, tamanho - r - 1, ri);
        pix.fillRectangle(r, b, tamanho - 2 * r, tamanho - 2 * b);
        pix.fillRectangle(b, r, tamanho - 2 * b, tamanho - 2 * r);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r);
        np.scale(1f / escala, 1f / escala);
        return new NinePatchDrawable(np);
    }

    private Table criarDomino(String textoEsquerda, String textoDireita, Label.LabelStyle estilo) {
        Table domino = new Table();
        domino.setBackground(criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
        Label lblEsquerda = criarRotuloComQuebra(textoEsquerda, estilo, 0.70f);
        lblEsquerda.setAlignment(Align.center);
        domino.add(lblEsquerda).expand().fill().pad(7);
        Image separador = new Image(criarTexturaCor(GerenciadorAcessibilidade.getCorBordaForte()));
        domino.add(separador).width(3).growY();
        Label lblDireita = criarRotuloComQuebra(textoDireita, estilo, 0.70f);
        lblDireita.setAlignment(Align.center);
        domino.add(lblDireita).expand().fill().pad(7);
        return domino;
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
    }
}
