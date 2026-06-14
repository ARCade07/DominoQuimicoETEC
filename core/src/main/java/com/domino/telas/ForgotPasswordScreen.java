package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorRecuperacao;
import com.domino.controladores.ControladorRegistro;
import com.domino.dao.UsuarioDao;

public class ForgotPasswordScreen extends BaseScreen {

    private UsuarioDao usuarioDao;
    private ControladorRecuperacao recuperador;

    public ForgotPasswordScreen() {
        super();

        montarTela();
    }

    private void montarTela(){
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de esqueci senha
        Table cartaoEsqueciSenha = new Table();
        cartaoEsqueciSenha.setBackground(Estilos.fundoArredondadoEscuro);
        cartaoEsqueciSenha.pad(60, 80, 60, 80);

        //Título
        Label titulo = new Label("Esqueci minha senha", Estilos.estiloTextoNormal);
        titulo.setFontScale(1.8f / Estilos.MULTIPLICADOR_HD);
        cartaoEsqueciSenha.add(titulo).center().padBottom(20).row();

        //Label explicação
        Label explicacao = new Label("Para redefinir sua senha, informe o e-mail cadastrado na sua conta e lhe enviaremos um link com as instruções.", Estilos.estiloTextoNormal);
        explicacao.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        explicacao.setWrap(true);
        explicacao.setAlignment(Align.center);
        cartaoEsqueciSenha.add(explicacao).width(440).center().padBottom(40).row();

        //Label email
        Label email = new Label("E-mail", Estilos.estiloTextoNormal);
        email.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoEsqueciSenha.add(email).left().padBottom(10).row();

        //Campo Email
        TextField campoEmail = new TextField("", Estilos.estiloCampoTexto);
        cartaoEsqueciSenha.add(campoEmail).width(440).height(50).padBottom(50).row();

        //Botão Enviar
        TextButton botaoEnviar = new TextButton("Enviar", Estilos.estiloBotaoEntrar);
        botaoEnviar.getLabel().setFontScale(1.2f / Estilos.MULTIPLICADOR_HD);
        botaoEnviar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Enviar!");
                //Adicionar função para enviar e-mail para usuário
            }
        });
        cartaoEsqueciSenha.add(botaoEnviar).width(180).height(60).padBottom(20).row();

        //Label Cancelar
        Label linkCancelar = new Label("Cancelar", Estilos.estiloTextoNegrito);
        linkCancelar.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        linkCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Cancelar");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
                //Função para trocar de tela
            }
        });
        cartaoEsqueciSenha.add(linkCancelar).center().padBottom(20).row();

        fundo.add(cartaoEsqueciSenha).width(600).height(550).center();
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
