package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorRecuperacao;
import com.domino.dao.UsuarioDao;

public class CodeScreen extends BaseScreen {

    private UsuarioDao usuarioDao;
    private String email;

    public CodeScreen(String email) {
        super();
        this.email = email;

        montarTela();
    }

    private void montarTela() {
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de código
        Table cartaoCodigo = new Table();
        cartaoCodigo.setBackground(Estilos.fundoArredondadoEscuro);
        cartaoCodigo.pad(60, 80, 60, 80);

        //Label explicação
        Label explicacao = new Label("Enviamos um código para o seu e-mail, insira-o para poder redefinir sua senha.", Estilos.estiloTextoNormal);
        explicacao.setFontScale(1.3f / Estilos.MULTIPLICADOR_HD);
        explicacao.setWrap(true);
        explicacao.setAlignment(Align.center);
        cartaoCodigo.add(explicacao).width(440).center().padBottom(40).row();

        //Label Código
        Label codigo = new Label("Código", Estilos.estiloTextoNormal);
        codigo.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoCodigo.add(codigo).left().padBottom(10).row();

        //Campo Código
        TextField campoCodigo = new TextField("", Estilos.estiloCampoTexto);
        cartaoCodigo.add(campoCodigo).width(440).height(50).padBottom(40).row();

        //Botão Enviar
        TextButton botaoEnviar = new TextButton("Enviar", Estilos.estiloBotaoEntrar);
        botaoEnviar.getLabel().setFontScale(1.2f / Estilos.MULTIPLICADOR_HD);
        botaoEnviar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //Implementar verificação de código

                ((Game) Gdx.app.getApplicationListener()).setScreen(new ResetPasswordScreen());
            }
        });
        cartaoCodigo.add(botaoEnviar).width(180).height(60).padBottom(20).row();

        //Label Cancelar
        Label linkCancelar = new Label("Cancelar", Estilos.estiloTextoNegrito);
        linkCancelar.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        linkCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
            }
        });
        cartaoCodigo.add(linkCancelar).center().padBottom(20).row();

        fundo.add(cartaoCodigo).width(600).height(550).center();
    }

    @Override
    public void render(float delta) {
        // Substitui a cor de fundo original da tela de esqueci senha antes de desenhar o stage
        com.badlogic.gdx.utils.ScreenUtils.clear(new Color(0.15f, 0.15f, 0.15f, 1f));
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
