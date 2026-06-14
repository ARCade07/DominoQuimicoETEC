package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorLogin;
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

        montarTela();
    }

    private void montarTela() {
        //Fundo
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
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
        fundo.add(criarBotao(texRanking, "Ranking", () -> {
            System.out.println("Lógica para ir para a tela de Ranking");})).width(600).height(100).padBottom(45).center().row();
        fundo.add(criarBotao(texConfig, "Configuracoes",() -> {
            System.out.println("Lógica para abrir Configurações");
        })).width(600).height(100).padBottom(45).center().row();
        fundo.add(criarBotao(texSair, "Sair", () -> {
            System.out.println("Fechando o jogo...");
            Gdx.app.exit();
        })).width(600).height(100).padBottom(45).center().row();
    }

    private Button criarBotao(Texture icone, String texto, Runnable acao) {
        Button botao = new Button(Estilos.estiloBotaoGrupo);
        botao.add(new Image(icone)).size(55, 55).padLeft(15).padRight(10);

        Label labelTexto = new Label(texto, Estilos.estiloTextoNormal);
        labelTexto.setFontScale(2f / Estilos.MULTIPLICADOR_HD);
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
