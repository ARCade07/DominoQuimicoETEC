package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.domino.modelos.Sessao;
import com.domino.modelos.Usuario;

public class RankingScreen extends BaseScreen {

    //cores medalhas
    private static final Color COR_OURO = Color.valueOf("FFD700");
    private static final Color COR_PRATA = Color.valueOf("B0B0B0");
    private static final Color COR_BRONZE = Color.valueOf("CD7F32");

    //estrutura de dados para armazenar informacoes de cada linha do ranking
    public static class EntradaRanking {
        public final int posicao;
        public final String nome;
        public final int pontuacao;
        public final boolean voce;

        public EntradaRanking(int posicao, String nome, int pontuacao, boolean voce) {
            this.posicao = posicao;
            this.nome = nome;
            this.pontuacao = pontuacao;
            this.voce = voce;
        }

        public EntradaRanking(int posicao, String nome, int pontuacao) {
            this(posicao, nome, pontuacao, false);
        }
    }

    private static EntradaRanking[] gerarDadosTeste() {
        EntradaRanking[] dados = new EntradaRanking[25];
        dados[0] = new EntradaRanking(1, "Pom", 1250400);
        dados[1] = new EntradaRanking(2, "Paulu B.", 1180900);
        dados[2] = new EntradaRanking(3, "Pamonha Plays", 1095500);
        dados[3] = new EntradaRanking(4, "Zezinho", 985300);
        dados[4] = new EntradaRanking(5, "CacetaPlays", 875300);
        dados[5] = new EntradaRanking(6, "Thomas T.", 785000);

        for (int i = 6; i < 25; i++) {
            dados[i] = new EntradaRanking(i + 1, "Jogador_" + (i + 1), 580000 - (i * 10000));
        }
        return dados;
    }

    private static final EntradaRanking[] DADOS_PADRAO = gerarDadosTeste();

    private final EntradaRanking jogadorAtual;
    private final EntradaRanking[] entradas;

    public RankingScreen() {
        this(new EntradaRanking(152, "Você", 650200, true), DADOS_PADRAO);
    }

    public RankingScreen(EntradaRanking jogador, EntradaRanking[] lista) {
        super();
        this.jogadorAtual = jogador;
        this.entradas = lista;
        montarTela();
    }

    private void montarTela() {
        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean prota = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        Color corFundo = GerenciadorAcessibilidade.getCorFundoTela();
        Color corCartao = GerenciadorAcessibilidade.getCorFundoCartao();
        Color corBorda = GerenciadorAcessibilidade.getCorBordaCartao();
        Color corTextoPadrao = GerenciadorAcessibilidade.getCorTextoPadrao();
        Color corTextoTitulo = GerenciadorAcessibilidade.getCorTextoTitulo();

        float tb = 36 * Estilos.MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario();
        com.badlogic.gdx.graphics.g2d.BitmapFont fNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", tb, 4, 2f);
        com.badlogic.gdx.graphics.g2d.BitmapFont fNegrito = Estilos.gerarFonte("Inter_24pt-Bold.ttf", tb, 8, 2f);

        Label.LabelStyle sBranco = new Label.LabelStyle(fNormal, corTextoPadrao);
        Label.LabelStyle sBrancoNeg = new Label.LabelStyle(fNegrito, corTextoPadrao);
        Label.LabelStyle sTitulo = new Label.LabelStyle(fNegrito, corTextoTitulo);
        Label.LabelStyle sFraco = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorTextoFraco());

        Label.LabelStyle sOuro = new Label.LabelStyle(fNegrito, ac ? Color.YELLOW : COR_OURO);
        Label.LabelStyle sPrata = new Label.LabelStyle(fNegrito, ac ? Color.WHITE : COR_PRATA);
        Label.LabelStyle sBronze = new Label.LabelStyle(fNegrito, ac ? Color.valueOf("FF9900") : COR_BRONZE);

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.top().left();

        if (ac) {
            raiz.setBackground(Estilos.criarTexturaCor(corFundo));
        } else {
            Color corTopo = Color.valueOf(prota ? "0A1428" : "4A0000");
            Color corBase = Color.valueOf(prota ? "02050A" : "0D0202");
            raiz.setBackground(Estilos.criarTexturaGradiente(corTopo, corBase));
        }
        stage.addActor(raiz);

        Color corSombraBtn = ac ? Color.DARK_GRAY : (prota ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fNegrito;
        estiloBotao.fontColor = Color.WHITE;
        estiloBotao.up = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombraBtn, 18, 9);
        estiloBotao.over = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombraBtn, 18, 9);
        estiloBotao.down = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombraBtn, 18, 3);
        estiloBotao.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 18, 9);
        estiloBotao.focusedFontColor = Color.BLACK;

        TextButton btnVoltar = new TextButton("← VOLTAR", estiloBotao);
        btnVoltar.getLabel().setFontScale(1f / Estilos.MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);
        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnVoltar.addAction(Actions.sequence(
                    Actions.scaleTo(0.95f, 0.95f, 0.05f),
                    Actions.scaleTo(1.0f, 1.0f, 0.05f)));
                if (Sessao.isLogado()) {
                    Usuario u = Sessao.getUsuario();
                    String papel = u.getRole();
                    if (papel != null && papel.equalsIgnoreCase("Professor")) {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new TeacherScreen());
                    } else {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new StartScreen());
                    }
                } else {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
                }
            }
        });

        Table layerSuperior = new Table();
        layerSuperior.setFillParent(true);
        layerSuperior.top().left();
        layerSuperior.add(btnVoltar).width(270).height(98).pad(30);
        stage.addActor(layerSuperior);

        Table painelJogador = criarPainelJogador(jogadorAtual, sBrancoNeg, sFraco, corCartao, corBorda);

        Table areaConteudo = new Table();
        areaConteudo.top().left();

        Label lblTitulo = criarRotulo("RANKING", sTitulo, 2.8f);
        areaConteudo.add(lblTitulo).center().padTop(0).padBottom(40).row();

        if (entradas.length >= 3) {
            Table podio = criarPodio(entradas[0], entradas[1], entradas[2], sOuro, sPrata, sBronze, sBrancoNeg);
            areaConteudo.add(podio).center().padBottom(40).row();
        }

        Table cabecalho = criarCabecalhoLista(sFraco);
        areaConteudo.add(cabecalho).growX().padBottom(6).row();

        if (entradas.length > 3) {
            Table lista = new Table();
            lista.top();
            int limite = Math.min(entradas.length, 20);

            for (int i = 3; i < limite; i++) {
                Table linha = criarLinhaRanking(entradas[i], sBrancoNeg, sBranco, sFraco);
                lista.add(linha).growX().padBottom(8).row();
            }
            lista.add().height(15).row();

            ScrollPane.ScrollPaneStyle estiloScroll = new ScrollPane.ScrollPaneStyle();
            ScrollPane scroll = new ScrollPane(lista, estiloScroll);
            scroll.setFadeScrollBars(false);
            scroll.setScrollingDisabled(true, false);
            scroll.setOverscroll(false, false);

            areaConteudo.add(scroll).grow();
        }

        raiz.add(painelJogador).width(430).growY().padTop(200).padBottom(30).padLeft(30).padRight(30);
        raiz.add(areaConteudo).grow().padTop(200).padBottom(30).padRight(100).padLeft(30);

        GerenciadorAcessibilidade.configurarNavegacao(stage, btnVoltar);
    }

    private Table criarPainelJogador(EntradaRanking jogador, Label.LabelStyle sNeg, Label.LabelStyle sFraco, Color corCartao, Color corBorda) {
        Table painel = new Table();
        painel.setBackground(Estilos.criarBordaArredondadaTextura(corCartao, corBorda, 24, 3));
        painel.pad(25, 30, 25, 30);

        Table conteudoCentral = new Table();
        conteudoCentral.center();

        Label lblNome = criarRotulo("Você", sNeg, 1.4f);
        lblNome.setAlignment(Align.center);
        conteudoCentral.add(lblNome).growX().center().padBottom(40).row();

        float escalaValor = 1.05f;
        float escalaTexto = 0.75f;
        int espacoEntreCaixas = 28;

        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;

        Color corFundoCaixa = GerenciadorAcessibilidade.getCorFundoCaixaRanking();
        Color corBordaCaixa = ac ? Color.WHITE : Color.CLEAR;
        int espessuraBorda = ac ? 2 : 0;

        // Caixa Posição
        Table caixaPos = new Table();
        caixaPos.setBackground(Estilos.criarBordaArredondadaTextura(corFundoCaixa, corBordaCaixa, 12, espessuraBorda));
        caixaPos.pad(15, 25, 15, 25);
        caixaPos.add(criarRotulo("SUA POSIÇÃO", sFraco, escalaTexto)).expandX().left();
        caixaPos.add(criarRotulo(jogador.posicao + "º", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPos).growX().padBottom(espacoEntreCaixas).row();

        // Caixa Pontuação
        Table caixaPts = new Table();
        caixaPts.setBackground(Estilos.criarBordaArredondadaTextura(corFundoCaixa, corBordaCaixa, 12, espessuraBorda));
        caixaPts.pad(15, 20, 15, 20);
        caixaPts.add(criarRotulo("PONTUAÇÃO", sFraco, escalaTexto)).expandX().left().padRight(10);
        caixaPts.add(criarRotulo(formatarPontuacao(jogador.pontuacao), sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPts).growX().padBottom(espacoEntreCaixas).row();

        // Caixa Partidas
        Table caixaPartidas = new Table();
        caixaPartidas.setBackground(Estilos.criarBordaArredondadaTextura(corFundoCaixa, corBordaCaixa, 12, espessuraBorda));
        caixaPartidas.pad(15, 20, 15, 20);
        caixaPartidas.add(criarRotulo("PARTIDAS", sFraco, escalaTexto)).expandX().left();
        caixaPartidas.add(criarRotulo("42", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPartidas).growX().padBottom(espacoEntreCaixas).row();

        // Caixa Vitórias
        Table caixaVitorias = new Table();
        caixaVitorias.setBackground(Estilos.criarBordaArredondadaTextura(corFundoCaixa, corBordaCaixa, 12, espessuraBorda));
        caixaVitorias.pad(15, 20, 15, 20);
        caixaVitorias.add(criarRotulo("VITÓRIAS", sFraco, escalaTexto)).expandX().left();
        caixaVitorias.add(criarRotulo("65%", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaVitorias).growX().row();

        painel.add(conteudoCentral).expand().fillX().center();
        return painel;
    }

    private Table criarPodio(EntradaRanking primeiro, EntradaRanking segundo, EntradaRanking terceiro,
                             Label.LabelStyle sOuro, Label.LabelStyle sPrata, Label.LabelStyle sBronze, Label.LabelStyle sBrancoNeg) {

        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        Color cOuro = ac ? Color.YELLOW : COR_OURO;
        Color cPrata = ac ? Color.WHITE : COR_PRATA;
        Color cBronze = ac ? Color.valueOf("FF9900") : COR_BRONZE;

        Table podio = new Table();
        podio.bottom();

        Table card2 = criarCartaoPodio(segundo, 2, cPrata, sPrata, sBrancoNeg);
        Table card1 = criarCartaoPodio(primeiro, 1, cOuro, sOuro, sBrancoNeg);
        Table card3 = criarCartaoPodio(terceiro, 3, cBronze, sBronze, sBrancoNeg);

        podio.add(card2).width(310).bottom().padRight(25);
        podio.add(card1).width(360).bottom().padRight(25);
        podio.add(card3).width(310).bottom();

        return podio;
    }

    private Table criarCartaoPodio(EntradaRanking entrada, int posicao, Color corMedalha, Label.LabelStyle sMedalha, Label.LabelStyle sBrancoNeg) {
        Table enveloper = new Table();
        Stack stack = new Stack();

        float cardHeight = posicao == 1 ? 220f : 180f;
        float circleRadius = 30f;

        Table cardTable = new Table();
        cardTable.bottom();

        Table conteudoBox = new Table();
        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean prota = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        Color corFundoBox = ac ? Color.BLACK : (prota ? Color.valueOf("070E1A") : Color.valueOf("1A0000"));

        conteudoBox.setBackground(Estilos.criarBordaArredondadaTextura(corFundoBox, corMedalha, 20, 4));
        conteudoBox.pad(15).padTop(circleRadius + 15);

        Label lblNome = criarRotulo(entrada.nome, sBrancoNeg, 1f);
        lblNome.setEllipsis(true);
        lblNome.setAlignment(Align.center);
        conteudoBox.add(lblNome).growX().center().padBottom(18).row();

        Label lblPontos = criarRotulo(formatarPontuacao(entrada.pontuacao) + " pts", sMedalha, 1f);
        lblPontos.setAlignment(Align.center);
        conteudoBox.add(lblPontos).expandX().center();

        cardTable.add(conteudoBox).growX().height(cardHeight);

        Table badgeLayer = new Table();
        badgeLayer.bottom();
        Table badgeGroup = new Table();
        badgeGroup.bottom();

        if (posicao == 1) {
            Image imgCoroa = new Image(Estilos.criarCoroaTextura(corMedalha));
            badgeGroup.add(imgCoroa).size(50, 32).center().padBottom(12).row();
        }

        Table circulo = new Table();
        circulo.setBackground(criarCirculoComBorda((int) circleRadius, corMedalha, corMedalha, 2));
        Label lblNum = criarRotulo(String.valueOf(posicao), sBrancoNeg, 1f);
        if (posicao == 1 || (posicao == 2 && ac)) lblNum.setColor(Color.BLACK);
        lblNum.setAlignment(Align.center);
        circulo.add(lblNum).expand().center().padBottom(6);

        badgeGroup.add(circulo).size(circleRadius * 2, circleRadius * 2).center();
        badgeLayer.add(badgeGroup).expandX().center().padBottom(cardHeight - circleRadius);

        stack.add(cardTable);
        stack.add(badgeLayer);
        enveloper.add(stack).grow();

        return enveloper;
    }

    private Table criarCabecalhoLista(Label.LabelStyle sFraco) {
        Table cab = new Table();
        cab.setBackground(Estilos.criarBordaArredondadaTextura(Color.BLACK, Color.BLACK, 0, 0));
        cab.pad(15, 27, 15, 27);
        cab.add(criarRotulo("RANK", sFraco, 0.85f)).width(82).left();
        cab.add(criarRotulo("JOGADOR", sFraco, 0.85f)).expandX().left().padLeft(50);
        cab.add(criarRotulo("PONTUAÇÃO", sFraco, 0.85f)).right();
        return cab;
    }

    private Table criarLinhaRanking(EntradaRanking entrada, Label.LabelStyle sNeg, Label.LabelStyle sNormal, Label.LabelStyle sFraco) {
        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean prota = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        Color corFundo = ac ? Color.BLACK : (prota ? Color.valueOf("03060C") : Color.valueOf("110202"));
        Color corBorda = GerenciadorAcessibilidade.getCorBordaLinhaRanking();

        Table linha = new Table();
        linha.setBackground(Estilos.criarBordaArredondadaTextura(corFundo, corBorda, 15, 2));
        linha.pad(18, 27, 18, 27);

        Label lblPos = criarRotulo(String.valueOf(entrada.posicao), sNeg, 1.15f);
        lblPos.setAlignment(Align.center);
        linha.add(lblPos).width(82).left();

        Label lblNome = criarRotulo(entrada.nome, sNormal, 0.95f);
        lblNome.setAlignment(Align.left);
        linha.add(lblNome).expandX().left().padLeft(50);

        Label lblPontos = criarRotulo(formatarPontuacao(entrada.pontuacao), sNeg, 0.95f);
        lblPontos.setAlignment(Align.right);
        linha.add(lblPontos).right();

        return linha;
    }

    private String formatarPontuacao(int pontos) {
        String s = String.valueOf(pontos);
        StringBuilder sb = new StringBuilder();
        int inicio = s.length() % 3;
        if (inicio > 0) sb.append(s, 0, inicio);
        for (int i = inicio; i < s.length(); i += 3) {
            if (sb.length() > 0) sb.append('.');
            sb.append(s, i, i + 3);
        }
        return sb.toString();
    }

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escala) {
        Label r = new Label(texto, estilo);
        r.setFontScale(escala / Estilos.MULTIPLICADOR_HD);
        return r;
    }

    private TextureRegionDrawable criarCirculoComBorda(int raio, Color corCentro, Color corBorda, int espessura) {
        int escala = 4, d = raio * escala * 2, esp = espessura * escala;
        com.badlogic.gdx.graphics.Pixmap pix = new com.badlogic.gdx.graphics.Pixmap(d, d, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pix.setBlending(com.badlogic.gdx.graphics.Pixmap.Blending.None);
        pix.setColor(0, 0, 0, 0);
        pix.fill();
        pix.setColor(corBorda);
        pix.fillCircle(d / 2, d / 2, (d / 2) - 1);
        pix.setColor(corCentro);
        pix.fillCircle(d / 2, d / 2, (d / 2) - 1 - esp);
        com.badlogic.gdx.graphics.Texture tex = new com.badlogic.gdx.graphics.Texture(pix, true);
        tex.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearLinear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }
}
