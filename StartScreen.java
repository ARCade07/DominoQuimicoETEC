package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class StartScreen extends BaseScreen {

    private Texture texLogoCps, texChemDom, texEtec;
    private Texture texPlay, texConfig, texSair;

    // --- Variáveis de Acessibilidade ---
    private BitmapFont fonteBotao;
    private Label.LabelStyle estiloTextoBotao;
    private Button.ButtonStyle estiloBotaoMenu;
    private Array<Actor> ordemNavegacao = new Array<>();

    public StartScreen() {
        super();

        //Texturas específicas tela de start
        texLogoCps = new Texture(Gdx.files.internal("logo_cps_versao_br.png"));
        texChemDom = new Texture(Gdx.files.internal("chemdom_branco.png"));
        texEtec = new Texture(Gdx.files.internal("etec_ra_metropolitana_sp_santo_andre_etec_julio_de_mesquita_cor 1.png"));
        texPlay = new Texture(Gdx.files.internal("play.png"));
        texConfig = new Texture(Gdx.files.internal("configuracoes.png"));
        texSair = new Texture(Gdx.files.internal("sair.png"));

        inicializarEstilosAcessiveis();
        montarTela();
    }

    @Override
    public void show() {
        super.show();
        GerenciadorAcessibilidade.configurarNavegacao(stage, ordemNavegacao.toArray(Actor.class));
    }

    private void inicializarEstilosAcessiveis() {
        float escala = GerenciadorAcessibilidade.getEscalaFonteUsuario();

        fonteBotao = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 36 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteBotao.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        estiloTextoBotao = new Label.LabelStyle(fonteBotao, GerenciadorAcessibilidade.getCorTextoPadrao());

        estiloBotaoMenu = new Button.ButtonStyle();
        Color corFundo = GerenciadorAcessibilidade.getCorFundoBotaoNormal();
        Color corSombra = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE ? Color.DARK_GRAY : new Color(corFundo.r * 0.5f, corFundo.g * 0.5f, corFundo.b * 0.5f, 1f);

        estiloBotaoMenu.up = Estilos.criarBotao3D(corFundo, corSombra, 16, 6);
        estiloBotaoMenu.down = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombra, 16, 2);
        estiloBotaoMenu.over = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombra, 16, 6);
        estiloBotaoMenu.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.BLACK, 16, 6);
    }

    private void montarTela() {
        //Fundo
        Table fundo = new Table();
        fundo.setFillParent(true);
        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        fundo.setBackground(altoContraste ? Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()) : Estilos.fundoGradiente);
        fundo.top();
        stage.addActor(fundo);

        //Cabeçalho
        Table cabecalho = new Table();
        cabecalho.setFillParent(true);
        cabecalho.top();
        stage.addActor(cabecalho);

        cabecalho.add(new Image(texLogoCps)).expandX().left().padTop(20).padLeft(20);
        fundo.add(new Image(texChemDom)).center().row();
        cabecalho.add(new Image(texEtec)).right().padTop(20).padRight(20);

        //Botões
        Button btnJogar = criarBotao(texPlay, "Jogar", () -> {
            System.out.println("Lógica para ir para a tela do jogo");
            PopUpCriaPartida popUp = new PopUpCriaPartida(stage);
            popUp.show();
        });
        fundo.add(btnJogar).width(600).height(100).padBottom(45).center().row();

        Button btnConfig = criarBotao(texConfig, "Configuracoes", () -> {
            PopUpAcessibilidade popUpAcessibilidade = new PopUpAcessibilidade(stage);
            popUpAcessibilidade.show();
        });
        fundo.add(btnConfig).width(600).height(100).padBottom(45).center().row();

        Button btnSair = criarBotao(texSair, "Sair",  () -> {
            Gdx.app.exit();
        });
        fundo.add(btnSair).width(600).height(100).padBottom(45).center().row();

        ordemNavegacao.add(btnJogar);
        ordemNavegacao.add(btnConfig);
        ordemNavegacao.add(btnSair);

        GerenciadorAcessibilidade.aplicarFoco(btnJogar);
        stage.setKeyboardFocus(btnJogar);
    }

    private Button criarBotao(Texture icone, String texto, Runnable acao) {
        Button botao = new Button(estiloBotaoMenu);
        botao.add(new Image(icone)).size(55, 55).padLeft(15).padRight(10);

        Label labelTexto = new Label(texto, estiloTextoBotao);
        botao.add(labelTexto).expandX().padLeft(-25);

        botao.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicou em: " + texto);
                acao.run();
            }
        });

        return botao;
    }

    @Override
    public void render(float delta) {
        com.badlogic.gdx.utils.ScreenUtils.clear(GerenciadorAcessibilidade.getCorFundoTela());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texLogoCps != null) texLogoCps.dispose();
        if (texChemDom != null) texChemDom.dispose();
        if (texEtec != null) texEtec.dispose();
        if (texPlay != null) texPlay.dispose();
        if (texConfig != null) texConfig.dispose();
        if (texSair != null) texSair.dispose();
        if (fonteBotao != null) fonteBotao.dispose();
    }
}
