package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class StartScreen implements Screen {

    private Stage stage;
    private static final float MULTIPLICADOR_HD = 3.0f;

    private BitmapFont fonteNormal;
    private BitmapFont fonteNegrito;

    private Texture texture;

    @Override
    public void show() {

        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        // Gerador de fonte normal
        FreeTypeFontGenerator geradorNormal = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Medium.ttf"));
        FreeTypeFontParameter parametroNormal = new FreeTypeFontParameter();
        parametroNormal.size = (int) (24 * MULTIPLICADOR_HD); // Tamanho real 72
        parametroNormal.color = Color.WHITE;
        parametroNormal.genMipMaps = true;
        parametroNormal.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNormal.magFilter = Texture.TextureFilter.Linear;
        parametroNormal.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        BitmapFont fonteNormal = geradorNormal.generateFont(parametroNormal);
        geradorNormal.dispose();
        fonteNormal.setUseIntegerPositions(false);

        //Gerador de fonte negrito
        FreeTypeFontGenerator geradorNegrito = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Bold.ttf"));
        FreeTypeFontParameter parametroNegrito = new FreeTypeFontParameter();
        parametroNegrito.size = (int) (24 * MULTIPLICADOR_HD); // Tamanho real 72
        parametroNegrito.color = Color.WHITE;
        parametroNegrito.genMipMaps = true;
        parametroNegrito.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroNegrito.magFilter = Texture.TextureFilter.Linear;
        parametroNegrito.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        BitmapFont fonteNegrito = geradorNegrito.generateFont(parametroNegrito);
        geradorNegrito.dispose();
        fonteNegrito.setUseIntegerPositions(false);

        //Estilos textos
        Label.LabelStyle estiloTextoNormal = new Label.LabelStyle(fonteNormal, Color.valueOf("7D0000"));
        Label.LabelStyle estiloTextoNegrito = new Label.LabelStyle(fonteNegrito, Color.valueOf("7D0000"));

        //Estilo Botão
        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fonteNormal;
        estiloBotao.fontColor = Color.valueOf("7D0000");
        estiloBotao.up = criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 8, 2);
        estiloBotao.over = criarBordaArredondadaTextura(Color.valueOf("CAAFAF"), Color.valueOf("957474"), 8, 2);
        estiloBotao.down = criarBordaArredondadaTextura(Color.valueOf("F2BDBD"), Color.valueOf("500000"), 8, 2);

        //Fundo
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.top();
        stage.addActor(fundo);

        //Cabeçalho
        Table cabecalho = new Table();
        cabecalho.setFillParent(true);
        cabecalho.top();
        stage.addActor(cabecalho);

        //Imagem Logo CPS
        texture = new Texture(Gdx.files.internal("logo_cps_versao_cor.png"));
        Image imagemLogo = new Image(texture);
        cabecalho.add(imagemLogo).expandX().left().padTop(20);

        //Imagem ChemDom
        texture = new Texture(Gdx.files.internal("chemdom.png"));
        Image imagemChemDom = new Image(texture);
        fundo.add(imagemChemDom).center().row();

        //Imagem Etec
        texture = new Texture(Gdx.files.internal("etec_ra_metropolitana_sp_santo_andre_etec_julio_de_mesquita_cor 1.png"));
        Image imagemEtec = new Image(texture);
        cabecalho.add(imagemEtec).right().padTop(20);

        //Botão Jogar
        TextButton botaoJogar = new TextButton("Jogar", estiloBotao);
        botaoJogar.getLabel().setFontScale(2f / MULTIPLICADOR_HD);
        botaoJogar.addListener(new ClickListener() {
            //Adicionar função para entrar no jogo
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        fundo.add(botaoJogar).width(600).height(100).padBottom(45).center().row();

        //Botão Configurações
        TextButton botaoConfiguracoes = new TextButton("Configurações", estiloBotao);
        botaoConfiguracoes.getLabel().setFontScale(2f / MULTIPLICADOR_HD);
        botaoConfiguracoes.addListener(new ClickListener() {
            //Adicionar função para entrar na tela de acessibilidade
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        fundo.add(botaoConfiguracoes).width(600).height(100).padBottom(45).center().row();

        //Botão Sair
        TextButton botaoSair = new TextButton("Sair", estiloBotao);
        botaoSair.getLabel().setFontScale(2f / MULTIPLICADOR_HD);
        botaoSair.addListener(new ClickListener() {
            //Adicionar função para fechar o jogo
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        fundo.add(botaoSair).width(600).height(100).padBottom(45).center().row();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        // Atualiza as animações e desenha todos os atores que estiverem no palco
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza a visualização se a janela mudar de tamanho
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (fonteNormal != null) fonteNormal.dispose();
        if (fonteNegrito != null) fonteNegrito.dispose();
        if (texture != null) texture.dispose();
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

}
