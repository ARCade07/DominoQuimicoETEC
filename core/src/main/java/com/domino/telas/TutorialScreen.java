package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.domino.modelos.Sessao;
import com.domino.modelos.Usuario;

public class TutorialScreen extends BaseScreen {

    public TutorialScreen() {
        super();
        montarTela();
    }

    private void montarTela() {
        float tb = 36 * Estilos.MULTIPLICADOR_HD * GerenciadorAcessibilidade.getEscalaFonteUsuario();
        com.badlogic.gdx.graphics.g2d.BitmapFont fNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", tb, 4, 2f);
        com.badlogic.gdx.graphics.g2d.BitmapFont fNegrito = Estilos.gerarFonte("Inter_24pt-Bold.ttf", tb, 8, 2f);

        boolean ac = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean prota = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;

        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloSubtitulo = new Label.LabelStyle(fNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        Label.LabelStyle estiloTexto = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorTextoPadrao());
        Label.LabelStyle estiloVerde = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorDestaqueSucesso());
        Label.LabelStyle estiloVermelho = new Label.LabelStyle(fNormal, GerenciadorAcessibilidade.getCorDestaqueErro());

        Table raiz = new Table();
        raiz.setFillParent(true);

        if (ac) {
            raiz.setBackground(Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()));
        } else {
            Color corTopo = Color.valueOf(prota ? "0A1428" : "4A0000");
            Color corBase = Color.valueOf(prota ? "02050A" : "0D0202");
            raiz.setBackground(Estilos.criarTexturaGradiente(corTopo, corBase));
        }
        stage.addActor(raiz);

        Color corSombra = ac ? Color.DARK_GRAY : (prota ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));
        TextButton.TextButtonStyle estiloBotao = new TextButton.TextButtonStyle();
        estiloBotao.font = fNegrito;
        estiloBotao.fontColor = Color.WHITE;
        estiloBotao.up = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombra, 18, 9);
        estiloBotao.over = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombra, 18, 9);
        estiloBotao.down = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombra, 18, 3);
        estiloBotao.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 18, 9);
        estiloBotao.focusedFontColor = Color.BLACK;

        TextButton btnVoltar = new TextButton("← VOLTAR", estiloBotao);
        btnVoltar.getLabel().setFontScale(1f / Estilos.MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnVoltar);
        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnVoltar.addAction(Actions.sequence(Actions.scaleTo(0.95f, 0.95f, 0.05f), Actions.scaleTo(1.0f, 1.0f, 0.05f)));
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
        layerSuperior.add().expandX();
        stage.addActor(layerSuperior);

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

        GerenciadorAcessibilidade.configurarNavegacao(stage, btnVoltar);
    }

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escalaDesejada / Estilos.MULTIPLICADOR_HD);
        return rotulo;
    }

    private Label criarRotuloComQuebra(String texto, Label.LabelStyle estilo, float escalaDesejada) {
        Label rotulo = criarRotulo(texto, estilo, escalaDesejada);
        rotulo.setWrap(true);
        return rotulo;
    }

    private Table criarCartaoTexto(String titulo, String texto, Label.LabelStyle estiloTitulo, Label.LabelStyle estiloTexto) {
        Table cartao = new Table();
        cartao.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
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
        caixaDestaque.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
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

    private Table criarDomino(String textoEsquerda, String textoDireita, Label.LabelStyle estilo) {
        Table domino = new Table();
        domino.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaCartao(), 18, 3));
        Label lblEsquerda = criarRotuloComQuebra(textoEsquerda, estilo, 0.70f);
        lblEsquerda.setAlignment(Align.center);
        domino.add(lblEsquerda).expand().fill().pad(7);
        Image separador = new Image(Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorBordaForte()));
        domino.add(separador).width(3).growY();
        Label lblDireita = criarRotuloComQuebra(textoDireita, estilo, 0.70f);
        lblDireita.setAlignment(Align.center);
        domino.add(lblDireita).expand().fill().pad(7);
        return domino;
    }
}
