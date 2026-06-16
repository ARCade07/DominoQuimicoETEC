package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorRanking;
import com.domino.dao.UsuarioDao;
import com.domino.modelos.Sessao;
import com.domino.modelos.Usuario;

public class StartScreen extends BaseScreen {

    private Texture texLogoCps, texChemDom, texEtec;
    private Texture texPlay, texConfig, texSair, texUser, texTutorial;
    private UsuarioDao usuarioDao;
    private ControladorRanking ranking;

    public StartScreen() {
        super();

        //Texturas específicas tela de start
        texLogoCps = new Texture(Gdx.files.internal("logo_cps_versao_br.png"));
        texChemDom = new Texture(Gdx.files.internal("chemdom_branco.png"));
        texEtec = new Texture(Gdx.files.internal("etec_ra_metropolitana_sp_santo_andre_etec_julio_de_mesquita_cor 1.png"));
        texPlay = new Texture(Gdx.files.internal("play.png"));
        texConfig = new Texture(Gdx.files.internal("configuracoes.png"));
        texSair = new Texture(Gdx.files.internal("sair.png"));
        texUser = new Texture(Gdx.files.internal("User.png"));
        texTutorial = new Texture(Gdx.files.internal("tutorial.png"));

        // IMPORTANTE: Inicializar DAO em background para não travar a thread GL
        // A conexão com MongoDB é bloqueante e não deve ser feita na thread principal de renderização
        // MUDANÇA: Antes isso era feito no construtor (síncrono), agora é assíncrono (ver BaseScreen.executeAsync)
        executeAsync(() -> {
            try {
                ConnectionFactory conexao = ConnectionFactory.getInstance();
                this.usuarioDao = new UsuarioDao(conexao);
                this.ranking = new ControladorRanking(usuarioDao);
                System.out.println("✓ Start inicializado com sucesso");
            } catch (Exception e) {
                System.err.println("❌ Erro ao inicializar Start: " + e.getMessage());
            }
        });

        montarTela();
    }

    private void montarTela() {
        //Fundo
        Table fundo = new Table();
        fundo.setFillParent(true);
        TextureRegionDrawable fundoAcessivel = GerenciadorAcessibilidade.criarTexturaGradiente(
            GerenciadorAcessibilidade.getCorFundoTela(),
            GerenciadorAcessibilidade.getCorFundoCaixaRanking()
        );
        fundo.setBackground(fundoAcessivel);
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
        Button botaoJogar = criarBotao(texPlay, "Jogar", () -> {
            System.out.println("Lógica para ir para a tela do jogo");
            PopUpCriaPartida popUp = new PopUpCriaPartida(stage);
            popUp.show();
        });

        Button botaoConfig = criarBotao(texConfig, "Configuracoes", () -> {
            System.out.println("Lógica para abrir Configurações");
        });

        Button botaoPontuacao = criarBotao(texUser, "Pontuação", () -> {
            String email = Sessao.getUsuario().getEmail();
            Usuario usuarioLogado = usuarioDao.buscarPorEmail(email);

            Sessao.setUsuario(usuarioLogado);

            RankingScreen.EntradaRanking[] listaTop = ranking.gerarRanking(usuarioLogado);
            RankingScreen.EntradaRanking jogadorAtual = ranking.gerarEntradaJogadorLogado(usuarioLogado);
            RankingScreen telaRanking = new RankingScreen(jogadorAtual, listaTop);
            ((Game) Gdx.app.getApplicationListener()).setScreen(telaRanking);
        });

        Button botaoTutorial = criarBotao(texTutorial, "Tutorial", () -> {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new TutorialScreen());
        });

        Button botaoSair = criarBotao(texSair, "Sair", () -> {
            System.out.println("Fechando o jogo...");
            Gdx.app.exit();
        });

        fundo.add(botaoJogar).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoConfig).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoPontuacao).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoTutorial).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoSair).width(600).height(100).padBottom(45).center().row();

        GerenciadorAcessibilidade.configurarNavegacao(stage, botaoJogar, botaoConfig, botaoPontuacao, botaoTutorial, botaoSair);
    }

    private Button criarBotao(Texture icone, String texto, Runnable acao) {
        Button botao = new Button(Estilos.estiloBotaoGrupo);
        botao.add(new Image(icone)).size(55, 55).padLeft(15).padRight(10);

        Label.LabelStyle estiloLabel = new Label.LabelStyle(Estilos.estiloTextoNormal);
        estiloLabel.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();

        Label labelTexto = new Label(texto, Estilos.estiloTextoNormal);

        float escalaBase = 2f / Estilos.MULTIPLICADOR_HD;
        labelTexto.setFontScale(escalaBase * GerenciadorAcessibilidade.getEscalaFonteUsuario());

        labelTexto.setFontScale(2f / Estilos.MULTIPLICADOR_HD);
        botao.add(labelTexto).expandX().padLeft(-25);

        botao.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicou em: " + texto);
                acao.run();
            }
        });

        GerenciadorAcessibilidade.aplicarFoco(botao);

        return botao;
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
    }
}
