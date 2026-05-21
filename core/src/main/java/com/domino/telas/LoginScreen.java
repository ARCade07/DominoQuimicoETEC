package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LoginScreen implements Screen {

    private Stage stage;
    private static final float MULTIPLICADOR_HD = 3.0f;

    private BitmapFont fonteNormal;
    private BitmapFont fonteNegrito;

    private Texture texture;

    @Override
    public void show() {

        stage = new Stage(new ExtendViewport(1920, 1080));
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

        //Gerador de fonte para campo de texto
        FreeTypeFontGenerator geradorCampoTexto = new FreeTypeFontGenerator(Gdx.files.internal("Inter_24pt-Medium.ttf"));
        FreeTypeFontParameter parametroCampoTexto = new FreeTypeFontParameter();
        parametroCampoTexto.size = 24;
        parametroCampoTexto.color = Color.WHITE;
        parametroCampoTexto.genMipMaps = true;
        parametroCampoTexto.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametroCampoTexto.magFilter = Texture.TextureFilter.Linear;
        parametroCampoTexto.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";
        BitmapFont fonteCampoTexto = geradorCampoTexto.generateFont(parametroCampoTexto);
        geradorCampoTexto.dispose();
        fonteCampoTexto.setUseIntegerPositions(false);

        //Estilos textos
        Label.LabelStyle estiloTextoNormal = new Label.LabelStyle(fonteNormal, Color.valueOf("FFFFFF"));
        Label.LabelStyle estiloTextoNegrito = new Label.LabelStyle(fonteNegrito, Color.valueOf("FFFFFF"));

        //Estilo Botão entrar
        TextButton.TextButtonStyle estiloBotaoEntrar = new TextButton.TextButtonStyle();
        estiloBotaoEntrar.font = fonteNormal;
        estiloBotaoEntrar.fontColor = Color.valueOf("F4E7E7");
        estiloBotaoEntrar.up = criarBordaArredondadaTextura(Color.valueOf("7D0000"), Color.valueOf("7D0000"), 8, 2);
        estiloBotaoEntrar.over = criarBordaArredondadaTextura(Color.valueOf("957474"), Color.valueOf("957474"), 8, 2);
        estiloBotaoEntrar.down = criarBordaArredondadaTextura(Color.valueOf("500000"), Color.valueOf("500000"), 8, 2);

        //Estilo Campo de texto
        TextField.TextFieldStyle estiloCampoTexto = new TextField.TextFieldStyle();
        estiloCampoTexto.font = fonteCampoTexto;
        estiloCampoTexto.fontColor = Color.valueOf("7D0000");
        estiloCampoTexto.background = criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 8, 2);

        //Cursor em Campo de texto
        TextureRegionDrawable texturaCursor = criarTexturaCor(Color.BLACK);
        texturaCursor.setMinWidth(2f);
        estiloCampoTexto.cursor = texturaCursor;

        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(criarTexturaGradiente(Color.valueOf("4A0000"), Color.valueOf("0D0202")));
        stage.addActor(fundo);

        //Cria cartão de login
        Table cartaoLogin = new Table();
        cartaoLogin.setBackground(criarBordaArredondadaTextura(Color.valueOf("1A0404"), Color.valueOf("500000"), 8, 2));
        cartaoLogin.pad(60, 80, 60, 80);

        //Imagem User
        texture = new Texture(Gdx.files.internal("User.png"));
        Image imagemUsuario = new Image(texture);
        //Fundo da imagem user
        NinePatchDrawable fundoArredondadoUsuario = criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 45, 2);
        Image fundoUsuario = new Image(fundoArredondadoUsuario);
        //Junta o fundo com a imagem em um stack
        Stack stackUsuario = new Stack();
        stackUsuario.add(fundoUsuario);
        Table tabelaIcone = new Table();
        tabelaIcone.add(imagemUsuario).size(60, 60);
        stackUsuario.add(tabelaIcone);
        cartaoLogin.add(stackUsuario).width(90).height(90).center().padBottom(20).row();

        //Título
        Label titulo = criarRotulo("Login", estiloTextoNormal, 1.8f);
        cartaoLogin.add(titulo).left().padBottom(40).row();

        //Campo Username
        Table grupoUsername = new Table();
        grupoUsername.setBackground(criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 8, 2));
        Image iconeUserCampo = new Image(texture);
        grupoUsername.add(iconeUserCampo).size(24, 24).padLeft(5).padRight(10);
        TextField.TextFieldStyle estiloCampoSemFundo = new TextField.TextFieldStyle(estiloCampoTexto);
        estiloCampoSemFundo.background = criarTexturaCor(new Color(0, 0, 0, 0));
        TextField campoUsername = new TextField("", estiloCampoSemFundo);
        grupoUsername.add(campoUsername).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoUsername).width(340).height(50).padBottom(40).row();

        //Imagem Senha
        Texture texturePassword = new Texture(Gdx.files.internal("Cadeado.png"));

        //Campo Senha
        Table grupoSenha = new Table();
        grupoSenha.setBackground(criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 8, 2));
        Image iconeSenhaCampo = new Image(texturePassword);
        grupoSenha.add(iconeSenhaCampo).size(24, 24).padLeft(5).padRight(10);
        TextField campoSenha = new TextField("", estiloCampoSemFundo);
        campoSenha.setPasswordMode(true);
        campoSenha.setPasswordCharacter('*');
        grupoSenha.add(campoSenha).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoSenha).width(340).height(50).padBottom(10).row();

        //Label Esqueceu senha
        Label linkEsqueceuSenha = new Label("Esqueceu a senha?", estiloTextoNegrito);
        linkEsqueceuSenha.setFontScale(0.6f / MULTIPLICADOR_HD);
        linkEsqueceuSenha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Esqueci a Senha!");
                //Função para trocar de tela e função de mandar e-mail para troca de senha
            }
        });
        cartaoLogin.add(linkEsqueceuSenha).right().padBottom(20).row();

        //Botão Entrar
        TextButton botaoEntrar = new TextButton("Entrar", estiloBotaoEntrar);
        botaoEntrar.getLabel().setFontScale(1.2f / MULTIPLICADOR_HD);
        botaoEntrar.addListener(new ClickListener() {
            //Adicionar função para verificar usuario e senha no banco
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        cartaoLogin.add(botaoEntrar).width(180).height(60).padBottom(15).center().row();

        //Label Cadastre-se
        Label linkCadastreSe = new Label("Cadastrar-se", estiloTextoNegrito);
        linkCadastreSe.setFontScale(0.8f / MULTIPLICADOR_HD);
        linkCadastreSe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Cadastrar-se!");
                //Função para trocar de tela
            }
        });
        cartaoLogin.add(linkCadastreSe).center().padBottom(20).row();

        fundo.add(cartaoLogin).expand().center();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(new Color(0.15f, 0.15f, 0.15f, 1f));

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

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escalaDesejada / MULTIPLICADOR_HD);
        return rotulo;
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

    private TextureRegionDrawable criarTexturaGradiente(Color corTopo, Color corBase) {
        int altura = 512;
        Pixmap mapaPixels = new Pixmap(1, altura, Pixmap.Format.RGBA8888);

        for (int y = 0; y < altura; y++) {
            float ratio = (float) y / (altura - 1);

            Color corAtual = new Color(corTopo).lerp(corBase, ratio);

            mapaPixels.setColor(corAtual);
            mapaPixels.drawPixel(0, y);
        }

        Texture textura = new Texture(mapaPixels);
        textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();

        return new TextureRegionDrawable(textura);
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

}
