package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class TeacherScreen extends BaseScreen {

    private Texture texLogoCps, texChemDom, texEtec;
    private Texture texRanking, texConfig, texSair;
    private UsuarioDao usuarioDao;
    private ControladorRanking ranking;

    public TeacherScreen() {
        super();

        //Texturas específicas tela de teacher
        texLogoCps = new Texture(Gdx.files.internal("logo_cps_versao_br.png"));
        texChemDom = new Texture(Gdx.files.internal("chemdom_branco.png"));
        texEtec = new Texture(Gdx.files.internal("etec_ra_metropolitana_sp_santo_andre_etec_julio_de_mesquita_cor 1.png"));
        texRanking = new Texture(Gdx.files.internal("ranking.png"));
        texConfig = new Texture(Gdx.files.internal("configuracoes.png"));
        texSair = new Texture(Gdx.files.internal("sair.png"));

        // IMPORTANTE: Inicializar DAO em background para não travar a thread GL
        // A conexão com MongoDB é bloqueante e não deve ser feita na thread principal de renderização
        // MUDANÇA: Antes isso era feito no construtor (síncrono), agora é assíncrono (ver BaseScreen.executeAsync)
        executeAsync(() -> {
            try {
                ConnectionFactory conexao = ConnectionFactory.getInstance();
                this.usuarioDao = new UsuarioDao(conexao);
                this.ranking = new ControladorRanking(usuarioDao);
                System.out.println("✓ Teacher inicializado com sucesso");
            } catch (Exception e) {
                System.err.println("❌ Erro ao inicializar Teacher: " + e.getMessage());
            }
        });

        montarTela();
    }

    public void recarregarTela() {
        stage.clear();
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
        Button botaoRanking = criarBotao(texRanking, "Ranking", () -> {
            System.out.println("Abrindo o Ranking...");
            String email = Sessao.getUsuario().getEmail();
            Usuario usuarioLogado = usuarioDao.buscarPorEmail(email);

            Sessao.setUsuario(usuarioLogado);

            RankingScreen.EntradaRanking[] listaTop = ranking.gerarRanking(usuarioLogado);
            RankingScreen.EntradaRanking jogadorAtual = ranking.gerarEntradaJogadorLogado(usuarioLogado);

            RankingScreen telaRanking = new RankingScreen(jogadorAtual, listaTop);
            ((Game) Gdx.app.getApplicationListener()).setScreen(telaRanking);
        });

        Button botaoConfig = criarBotao(texConfig, "Configuracoes", () -> {
            System.out.println("Lógica para abrir Configurações");
            PopUpAcessibilidade popUpAcessibilidade = new PopUpAcessibilidade(stage, this::recarregarTela);
            popUpAcessibilidade.show();
        });

        Button botaoSair = criarBotao(texSair, "Sair", () -> {
            System.out.println("Fechando o jogo...");
            Gdx.app.exit();
        });

        fundo.add(botaoRanking).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoConfig).width(600).height(100).padBottom(45).center().row();
        fundo.add(botaoSair).width(600).height(100).padBottom(45).center().row();

        GerenciadorAcessibilidade.configurarNavegacao(stage, botaoRanking, botaoConfig, botaoSair);
    }

    private Button criarBotao(Texture icone, String texto, Runnable acao) {
        Button.ButtonStyle estiloDinamico = new Button.ButtonStyle();
        Color corBorda = GerenciadorAcessibilidade.getCorBordaForte();

        estiloDinamico.up = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corBorda, 8, 2);
        estiloDinamico.over = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corBorda, 8, 2);
        estiloDinamico.down = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corBorda, 8, 2);

        Button botao = new Button(estiloDinamico);
        botao.add(new Image(icone)).size(55, 55).padLeft(15).padRight(10);

        Label.LabelStyle estiloLabel = new Label.LabelStyle(Estilos.estiloTextoNormal);
        estiloLabel.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();

        Label labelTexto = new Label(texto, estiloLabel);

        float escalaBase = 2f / Estilos.MULTIPLICADOR_HD;
        labelTexto.setFontScale(escalaBase * GerenciadorAcessibilidade.getEscalaFonteUsuario());

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
        if (texRanking != null) texRanking.dispose();
        if (texConfig != null) texConfig.dispose();
        if (texSair != null) texSair.dispose();
    }
}
