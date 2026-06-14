package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ResetPasswordScreen extends BaseScreen {

    private Texture texSenha;

    public ResetPasswordScreen() {
        super();
        //Textura específica tela de resetar senha
        texSenha = new Texture(Gdx.files.internal("Cadeado.png"));

        montarTela();
    }

    private void montarTela(){
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de esqueci senha
        Table cartaoRedefinirSenha = new Table();
        cartaoRedefinirSenha.setBackground(Estilos.fundoArredondadoEscuro);
        cartaoRedefinirSenha.pad(60, 80, 60, 80);

        //Título
        Label titulo = new Label("Redefinir Senha", Estilos.estiloTextoNormal);
        titulo.setFontScale(1.8f / Estilos.MULTIPLICADOR_HD);
        cartaoRedefinirSenha.add(titulo).center().padBottom(20).row();

        //Label Senha
        Label senha = new Label("Senha", Estilos.estiloTextoNormal);
        senha.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoRedefinirSenha.add(senha).left().padBottom(10).row();

        //Campo Senha
        Table grupoSenha = new Table();
        grupoSenha.setBackground(Estilos.fundoArredondadoClaro);
        grupoSenha.add(new Image(texSenha)).size(24, 24).padLeft(5).padRight(10);
        TextField campoSenha = new TextField("", Estilos.estiloCampoSemFundo);
        campoSenha.setPasswordMode(true);
        campoSenha.setPasswordCharacter('*');
        grupoSenha.add(campoSenha).expandX().fillX().padRight(15);
        cartaoRedefinirSenha.add(grupoSenha).width(440).height(50).padBottom(10).row();

        //Label Confirmar Senha
        Label confirmarSenha = new Label("Confirmar senha", Estilos.estiloTextoNormal);
        confirmarSenha.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoRedefinirSenha.add(confirmarSenha).left().padBottom(10).row();

        //Campo Confirmar Senha
        Table grupoConfirmarSenha = new Table();
        grupoConfirmarSenha.setBackground(Estilos.fundoArredondadoClaro);
        grupoConfirmarSenha.add(new Image(texSenha)).size(24, 24).padLeft(5).padRight(10);
        TextField campoConfirmarSenha = new TextField("", Estilos.estiloCampoSemFundo);
        campoConfirmarSenha.setPasswordMode(true);
        campoConfirmarSenha.setPasswordCharacter('*');
        grupoConfirmarSenha.add(campoConfirmarSenha).expandX().fillX().padRight(15);
        cartaoRedefinirSenha.add(grupoConfirmarSenha).width(440).height(50).padBottom(40).row();

        //Botão Redefinir senha
        TextButton botaoRedefinirSenha = new TextButton("Redefinir Senha", Estilos.estiloBotaoEntrar);
        botaoRedefinirSenha.getLabel().setFontScale(1.2f / Estilos.MULTIPLICADOR_HD);
        botaoRedefinirSenha.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                PopUpMensagem popUp = new PopUpMensagem(stage);
                //Adicionar função para verificar se os dois campos possuem o mesmo conteúdo
                popUp.showErro("Os campos de senham devem ser iguais!");
                //Adicionar função para atualizar senha do usuário no banco
                popUp.showSucesso("Senha redefinida!");
                //Caso dê erro ao redefinir senha
                popUp.showErro("Erro ao redefinir senha!");
            }
        });
        cartaoRedefinirSenha.add(botaoRedefinirSenha).width(240).height(60).padBottom(20).row();

        //Label Cancelar
        Label linkCancelar = new Label("Cancelar", Estilos.estiloTextoNegrito);
        linkCancelar.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        linkCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Cancelar");
                //Função para trocar de tela
            }
        });
        cartaoRedefinirSenha.add(linkCancelar).center().padBottom(20).row();

        fundo.add(cartaoRedefinirSenha).width(600).height(550).center();
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
