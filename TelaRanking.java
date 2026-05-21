package com.pidomino;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class TelaRanking implements Screen {

    private Stage palco;
    private Skin tema;
    private static final float MULTIPLICADOR_HD = 3.0f;

    //medalhas mantidas fixas por representarem materiais fisicos
    private static final Color COR_OURO              = Color.valueOf("FFD700");
    private static final Color COR_PRATA             = Color.valueOf("B0B0B0");
    private static final Color COR_BRONZE            = Color.valueOf("CD7F32");

    //estrutura de dados
    public static class EntradaRanking {
        public final int    posicao;
        public final String nome;
        public final int    pontuacao;
        public final boolean voce;

        public EntradaRanking(int posicao, String nome, int pontuacao, boolean voce) {
            this.posicao   = posicao;
            this.nome      = nome;
            this.pontuacao = pontuacao;
            this.voce      = voce;
        }
        public EntradaRanking(int posicao, String nome, int pontuacao) {
            this(posicao, nome, pontuacao, false);
        }
    }

    private static EntradaRanking[] gerarDadosTeste() {
        EntradaRanking[] dados = new EntradaRanking[25];
        dados[0] = new EntradaRanking(1, "Pom", 1250400);
        dados[1] = new EntradaRanking(2, "Paulu B.",   1180900);
        dados[2] = new EntradaRanking(3, "Pamonha Plays",   1095500);
        dados[3] = new EntradaRanking(4, "Zezinho",    985300);
        dados[4] = new EntradaRanking(5, "CacetaPlays",     875300);
        dados[5] = new EntradaRanking(6, "Thomas T.",   785000);

        for(int i = 6; i < 25; i++) {
            dados[i] = new EntradaRanking(i + 1, "Jogador_" + (i + 1), 580000 - (i * 10000));
        }
        return dados;
    }

    private static final EntradaRanking[] DADOS_PADRAO = gerarDadosTeste();

    private final EntradaRanking   jogadorAtual;
    private final EntradaRanking[] entradas;

    public TelaRanking() {
        this(new EntradaRanking(152, "Você", 650200, true), DADOS_PADRAO);
    }

    public TelaRanking(EntradaRanking jogador, EntradaRanking[] lista) {
        this.jogadorAtual = jogador;
        this.entradas     = lista;
    }

    //show
    @Override
    public void show() {
        palco = new Stage(new ExtendViewport(1920, 1080));
        Gdx.input.setInputProcessor(palco);
        tema = new Skin();

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
        parametroNormal.padTop = 4;
        parametroNormal.padBottom = 4;
        parametroNormal.padLeft = 4;
        parametroNormal.padRight = 4;
        parametroNormal.spaceX = 4;
        parametroNormal.spaceY = 4;
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
        parametroNegrito.padTop = 8;
        parametroNegrito.padBottom = 8;
        parametroNegrito.padLeft = 8;
        parametroNegrito.padRight = 8;
        parametroNegrito.spaceX = 8;
        parametroNegrito.spaceY = 8;
        fonteNegrito.setUseIntegerPositions(false);
        geradorNegrito.dispose();

        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;

        Color corFundo      = GerenciadorAcessibilidade.getCorFundoTela();
        Color corBordaForte = GerenciadorAcessibilidade.getCorBordaForte();

        Color corTextoPadrao = Color.WHITE;
        Color corTextoTitulo = Color.WHITE;

        Label.LabelStyle sBranco    = new Label.LabelStyle(fonteNormal,  corTextoPadrao);
        Label.LabelStyle sBrancoNeg = new Label.LabelStyle(fonteNegrito, corTextoPadrao);
        Label.LabelStyle sTitulo    = new Label.LabelStyle(fonteNegrito, corTextoTitulo);
        Label.LabelStyle sFraco     = new Label.LabelStyle(fonteNormal,  Color.LIGHT_GRAY);

        Label.LabelStyle sOuro      = new Label.LabelStyle(fonteNegrito, altoContraste ? Color.YELLOW : COR_OURO);
        Label.LabelStyle sPrata     = new Label.LabelStyle(fonteNegrito, altoContraste ? Color.LIGHT_GRAY : COR_PRATA);
        Label.LabelStyle sBronze    = new Label.LabelStyle(fonteNegrito, altoContraste ? Color.valueOf("CC8800") : COR_BRONZE);

        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.top().left();

        if (altoContraste) {
            raiz.setBackground(criarTexturaCor(corFundo));
        } else {
            Color corTopo = Color.valueOf("4A0000");
            Color corBase = Color.valueOf("0D0202");
            raiz.setBackground(GerenciadorAcessibilidade.criarTexturaGradiente(corTopo, corBase));
        }
        palco.addActor(raiz);

        // --- ADIÇÃO DO BOTÃO VOLTAR ---
        Color corSombraBtn   = altoContraste ? Color.DARK_GRAY : Color.valueOf("4D0000");
        Color corBotaoNormal = GerenciadorAcessibilidade.getCorFundoBotaoNormal();
        Color corBotaoHover  = GerenciadorAcessibilidade.getCorFundoBotaoHover();
        Color corBotaoDown   = GerenciadorAcessibilidade.getCorFundoBotaoDown();

        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font             = fonteNegrito;
        estiloBotao.fontColor        = Color.WHITE;
        estiloBotao.up               = criarBotao3D(corBotaoNormal, corSombraBtn, 18, 9);
        estiloBotao.over             = criarBotao3D(corBotaoHover, corSombraBtn, 18, 9);
        estiloBotao.down             = criarBotao3D(corBotaoDown, corSombraBtn, 18, 3);
        estiloBotao.focused          = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 18, 9);
        estiloBotao.focusedFontColor = Color.BLACK;

        TextButton btnVoltar = new TextButton("← VOLTAR", estiloBotao);
        btnVoltar.getLabel().setFontScale(1f / MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);
        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnVoltar.addAction(Actions.sequence(
                    Actions.scaleTo(0.95f, 0.95f, 0.05f),
                    Actions.scaleTo(1.0f,  1.0f,  0.05f)));
                ((Game) Gdx.app.getApplicationListener())
                    .setScreen(new TelaTutorial());
            }
        });

        Table layerSuperior = new Table();
        layerSuperior.setFillParent(true);
        layerSuperior.top().left();
        layerSuperior.add(btnVoltar).width(270).height(98).pad(30);
        palco.addActor(layerSuperior);
        // --------------------------------

        Table painelJogador = criarPainelJogador(
            jogadorAtual, sBrancoNeg, sBranco, sFraco, sBrancoNeg,
            Color.valueOf("140505"), corBordaForte);

        Table areaConteudo = new Table();
        areaConteudo.top().left();

        Label lblTitulo = criarRotulo("RANKING", sTitulo, 2.8f);
        areaConteudo.add(lblTitulo).center().padTop(0).padBottom(40).row();

        if (entradas.length >= 3) {
            Table podio = criarPodio(
                entradas[0], entradas[1], entradas[2],
                sOuro, sPrata, sBronze, sBrancoNeg);
            areaConteudo.add(podio).center().padBottom(40).row();
        }

        Table cabecalho = criarCabecalhoLista(sFraco);
        areaConteudo.add(cabecalho).growX().padBottom(6).row();

        if (entradas.length > 3) {
            Table lista = new Table();
            lista.top();
            int limite = Math.min(entradas.length, 20);

            for (int i = 3; i < limite; i++) {
                Table linha = criarLinhaRanking(
                    entradas[i], sBrancoNeg, sBranco, sBrancoNeg, sFraco);
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

        raiz.add(painelJogador)
            .width(430).growY()
            .padTop(200).padBottom(30).padLeft(30).padRight(30);
        raiz.add(areaConteudo)
            .grow()
            .padTop(200).padBottom(30).padRight(100).padLeft(30);

        GerenciadorAcessibilidade.configurarNavegacao(palco, btnVoltar);
    }

    private Table criarPainelJogador(EntradaRanking jogador,
                                     Label.LabelStyle sNeg, Label.LabelStyle sNormal,
                                     Label.LabelStyle sFraco, Label.LabelStyle sDestaque,
                                     Color corCartao, Color corBorda) {
        Table painel = new Table();
        painel.setBackground(criarBordaArredondadaTextura(corCartao, corBorda, 24, 3));

        // AQUI ESTÁ A MUDANÇA: pad(topo, esquerda, baixo, direita)
        // Aumentamos as laterais de 25 para 45 para afastar os cartões menores da borda
        painel.pad(25, 30, 25, 30);

        Table conteudoCentral = new Table();
        conteudoCentral.center();

        Label lblNome = criarRotulo("Você", sNeg, 1.4f);
        lblNome.setAlignment(Align.center);
        conteudoCentral.add(lblNome).growX().center().padBottom(40).row();

        // Variáveis de ajuste fino
        float escalaValor = 1.05f;
        float escalaTexto = 0.75f;
        int espacoEntreCaixas = 28;

        // 1. Caixa de Posição
        Table caixaPos = new Table();
        caixaPos.setBackground(criarBordaArredondadaTextura(Color.valueOf("2B0505"), Color.CLEAR, 12, 0));
        caixaPos.pad(15, 25, 15, 25);
        caixaPos.add(criarRotulo("SUA POSIÇÃO", sFraco, escalaTexto)).expandX().left();
        caixaPos.add(criarRotulo(jogador.posicao + "º", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPos).growX().padBottom(espacoEntreCaixas).row();

        // 2. Caixa de Pontuação
        Table caixaPts = new Table();
        caixaPts.setBackground(criarBordaArredondadaTextura(Color.valueOf("2B0505"), Color.CLEAR, 12, 0));
        caixaPts.pad(15, 20, 15, 20);
        caixaPts.add(criarRotulo("PONTUAÇÃO", sFraco, escalaTexto)).expandX().left().padRight(10);
        caixaPts.add(criarRotulo(formatarPontuacao(jogador.pontuacao), sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPts).growX().padBottom(espacoEntreCaixas).row();

        // 3. Caixa de Partidas Jogadas
        Table caixaPartidas = new Table();
        caixaPartidas.setBackground(criarBordaArredondadaTextura(Color.valueOf("2B0505"), Color.CLEAR, 12, 0));
        caixaPartidas.pad(15, 20, 15, 20);
        caixaPartidas.add(criarRotulo("PARTIDAS", sFraco, escalaTexto)).expandX().left();
        caixaPartidas.add(criarRotulo("42", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaPartidas).growX().padBottom(espacoEntreCaixas).row();

        // 4. Caixa de Taxa de Vitória
        Table caixaVitorias = new Table();
        caixaVitorias.setBackground(criarBordaArredondadaTextura(Color.valueOf("2B0505"), Color.CLEAR, 12, 0));
        caixaVitorias.pad(15, 20, 15, 20);
        caixaVitorias.add(criarRotulo("VITÓRIAS", sFraco, escalaTexto)).expandX().left();
        caixaVitorias.add(criarRotulo("65%", sNeg, escalaValor)).right();
        conteudoCentral.add(caixaVitorias).growX().row();

        // Expandindo para o centro
        painel.add(conteudoCentral).expand().fillX().center();

        return painel;
    }

    private Table criarPodio(EntradaRanking primeiro, EntradaRanking segundo, EntradaRanking terceiro,
                             Label.LabelStyle sOuro, Label.LabelStyle sPrata, Label.LabelStyle sBronze,
                             Label.LabelStyle sBrancoNeg) {
        Table podio = new Table();
        podio.bottom();

        Table card2 = criarCartaoPodio(segundo,  2, COR_PRATA,  sPrata,  sBrancoNeg);
        Table card1 = criarCartaoPodio(primeiro, 1, COR_OURO,   sOuro,   sBrancoNeg);
        Table card3 = criarCartaoPodio(terceiro, 3, COR_BRONZE, sBronze, sBrancoNeg);

        // Removi os '.height()' que estavam fixos para deixar o Stack calcular sozinho
        // e respeitar os paddings que definem a linha horizontal certinha.
        podio.add(card2).width(310).bottom().padRight(25);
        podio.add(card1).width(360).bottom().padRight(25);
        podio.add(card3).width(310).bottom();

        return podio;
    }

    private Table criarCartaoPodio(EntradaRanking entrada, int posicao, Color corMedalha,
                                   Label.LabelStyle sMedalha, Label.LabelStyle sBrancoNeg) {

        Table enveloper = new Table();
        Stack stack = new Stack();

        float cardHeight = posicao == 1 ? 220f : 180f;
        float circleRadius = 30f;

        // 1. O fundo com bordas arredondadas e os textos (caixa)
        Table cardTable = new Table();
        cardTable.bottom();

        Table conteudoBox = new Table();
        conteudoBox.setBackground(criarBordaArredondadaTextura(Color.valueOf("1A0000"), corMedalha, 20, 4));
        conteudoBox.pad(15);
        conteudoBox.padTop(circleRadius + 15); // Espaço extra pra respirar abaixo do circulo

        Label lblNome = criarRotulo(entrada.nome, sBrancoNeg, 1f);
        lblNome.setEllipsis(true);
        lblNome.setAlignment(Align.center);
        conteudoBox.add(lblNome).growX().center().padBottom(18).row();

        Label lblPontos = criarRotulo(formatarPontuacao(entrada.pontuacao) + " pts", sMedalha, 1f);
        lblPontos.setAlignment(Align.center);
        conteudoBox.add(lblPontos).expandX().center();

        cardTable.add(conteudoBox).growX().height(cardHeight);

        // 2. A estrutura do topo: Bolinha e Coroa
        Table badgeLayer = new Table();
        badgeLayer.bottom(); // Alinhamos esse container base para calcularmos do chao para cima

        Table badgeGroup = new Table();
        badgeGroup.bottom();
        if (posicao == 1) {
            Image imgCoroa = new Image(criarCoroaTextura(corMedalha));
            badgeGroup.add(imgCoroa).size(50, 32).center().padBottom(-3).row(); // Coroa
        }

        Table circulo = new Table();
        circulo.setBackground(criarCirculoComBorda((int)circleRadius, corMedalha, corMedalha, 2));
        Label lblNum = criarRotulo(String.valueOf(posicao), sBrancoNeg, 1f);
        if (posicao == 1) lblNum.setColor(Color.BLACK);
        lblNum.setAlignment(Align.center);
        circulo.add(lblNum).expand().center().padBottom(6);

        badgeGroup.add(circulo).size(circleRadius * 2, circleRadius * 2).center();

        // CÁLCULO EXATO DA INTERSECÇÃO:
        // Se a camada está alinhada por baixo, e a caixa tem a altura 'cardHeight'
        // damos um padding inferior de (cardHeight - circleRadius)
        // Isso faz com que a base da bolinha fique na marca de cardHeight menos o seu próprio raio,
        // posicionando O CENTRO da bolinha matematicamente exato no topo da linha da caixa!
        badgeLayer.add(badgeGroup).expandX().center().padBottom(cardHeight - circleRadius);

        stack.add(cardTable);
        stack.add(badgeLayer);

        enveloper.add(stack).grow();

        return enveloper;
    }

    private Table criarCabecalhoLista(Label.LabelStyle sFraco) {
        Table cab = new Table();
        Color corFundo = Color.BLACK;
        cab.setBackground(criarBordaArredondadaTextura(corFundo, corFundo, 0, 0));
        cab.pad(15, 27, 15, 27);

        cab.add(criarRotulo("RANK", sFraco, 0.85f)).width(82).left();
        cab.add(criarRotulo("JOGADOR", sFraco, 0.85f)).expandX().left().padLeft(50);
        cab.add(criarRotulo("PONTUAÇÃO", sFraco, 0.85f)).right();

        return cab;
    }

    private Table criarLinhaRanking(EntradaRanking entrada,
                                    Label.LabelStyle sNeg, Label.LabelStyle sNormal,
                                    Label.LabelStyle sDestaque, Label.LabelStyle sFraco) {

        Color corFundo = Color.valueOf("110202");
        Color corBorda = Color.valueOf("350A0A");

        Table linha = new Table();
        linha.setBackground(criarBordaArredondadaTextura(corFundo, corBorda, 15, 2));
        linha.pad(18, 27, 18, 27);

        Label lblPos = criarRotulo(String.valueOf(entrada.posicao), sDestaque, 1.15f);
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

    //utilitarios

    private String formatarPontuacao(int pontos) {
        String s   = String.valueOf(pontos);
        StringBuilder sb = new StringBuilder();
        int inicio = s.length() % 3;
        if (inicio > 0) sb.append(s, 0, inicio);
        for (int i = inicio; i < s.length(); i += 3) {
            if (sb.length() > 0) sb.append('.');
            sb.append(s, i, i + 3);
        }
        return sb.toString();
    }

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escalaDesejada / MULTIPLICADOR_HD);
        return rotulo;
    }

    private TextureRegionDrawable criarTexturaCor(Color cor) {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(cor);
        pix.fill();
        Texture tex = new Texture(pix);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextureRegionDrawable criarCirculoComBorda(int raio, Color corCentro, Color corBorda, int espessura) {
        int escala = 4;
        int d = raio * escala * 2;
        int esp = espessura * escala;
        Pixmap pix = new Pixmap(d, d, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(0, 0, 0, 0);
        pix.fill();
        pix.setColor(corBorda);
        pix.fillCircle(d / 2, d / 2, (d / 2) - 1);
        pix.setColor(corCentro);
        pix.fillCircle(d / 2, d / 2, (d / 2) - 1 - esp);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextureRegionDrawable criarCoroaTextura(Color cor) {
        int w = 60;
        int h = 45;
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(cor);
        pix.fillRectangle(6, 30, 48, 9);
        pix.fillTriangle(6, 30, 21, 30, 3, 6);
        pix.fillTriangle(21, 30, 39, 30, 30, 0);
        pix.fillTriangle(39, 30, 54, 30, 57, 6);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private NinePatchDrawable criarBotao3D(Color corCorpo, Color corSombra, int raio, int profundidade) {
        int escala = 4, t = 100 * escala, r = raio * escala, p = profundidade * escala;
        Pixmap pix = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(corSombra);
        pix.fillCircle(r, t - r - 1, r);     pix.fillCircle(t - r - 1, t - r - 1, r);
        pix.fillRectangle(r, t - 2 * r, t - 2 * r, 2 * r);
        pix.fillRectangle(0, t - r - 1 - p, t, p);
        pix.setColor(corCorpo);
        pix.fillCircle(r, r, r);              pix.fillCircle(t - r - 1, r, r);
        pix.fillCircle(r, t - r - 1 - p, r); pix.fillCircle(t - r - 1, t - r - 1 - p, r);
        pix.fillRectangle(r, 0, t - 2 * r, t - p);
        pix.fillRectangle(0, r, t, t - 2 * r - p);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r + p);
        np.scale(1f / escala, 1f / escala);
        return new NinePatchDrawable(np);
    }

    private NinePatchDrawable criarBordaArredondadaTextura(Color corFundo, Color corBorda,
                                                           int raio, int tamanhoBorda) {
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

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        palco.dispose();
        tema.dispose();
    }
}
