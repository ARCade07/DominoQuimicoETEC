package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class CadastroScreen extends BaseScreen {

    private Texture texUsuario;
    private Texture texSenha;
    private Texture texEmail;

    public CadastroScreen() {
        super();

        //Texturas específicas tela de login
        texUsuario = new Texture(Gdx.files.internal("User.png"));
        texSenha = new Texture(Gdx.files.internal("Cadeado.png"));
        texEmail = new Texture(Gdx.files.internal("Email.png"));

        montarTela();
    }

    private void montarTela() {
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de cadastro
        Table cartaoCadastro = new Table();
        cartaoCadastro.setBackground(Estilos.fundoArredondadoEscuro);
        cartaoCadastro.pad(60, 80, 60, 80);

        //Título
        Label titulo = new Label("Cadastro", Estilos.estiloTextoNormal);
        titulo.setFontScale(1.8f / Estilos.MULTIPLICADOR_HD);
        cartaoCadastro.add(titulo).center().padBottom(40).row();

        //Label Username
        Label username = new Label("Username", Estilos.estiloTextoNormal);
        username.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoCadastro.add(username).left().padBottom(10).row();

        //Campo Username
        Table grupoUsername = new Table();
        grupoUsername.setBackground(Estilos.fundoArredondadoClaro);
        grupoUsername.add(new Image(texUsuario)).size(24, 24).padLeft(5).padRight(10);
        TextField campoUsername = new TextField("", Estilos.estiloCampoSemFundo);
        grupoUsername.add(campoUsername).expandX().fillX().padRight(15);
        cartaoCadastro.add(grupoUsername).width(340).height(50).padBottom(20).row();

        //Label email
        Label email = new Label("E-mail", Estilos.estiloTextoNormal);
        email.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoCadastro.add(email).left().padBottom(10).row();

        //Campo Email
        Table grupoEmail = new Table();
        grupoEmail.setBackground(Estilos.fundoArredondadoClaro);
        grupoEmail.add(new Image(texEmail)).size(24, 24).padLeft(5).padRight(10);
        TextField campoEmail = new TextField("", Estilos.estiloCampoSemFundo);
        grupoEmail.add(campoEmail).expandX().fillX().padRight(15);
        cartaoCadastro.add(grupoEmail).width(340).height(50).padBottom(20).row();

        //Label Senha
        Label senha = new Label("Senha", Estilos.estiloTextoNormal);
        senha.setFontScale(1 / Estilos.MULTIPLICADOR_HD);
        cartaoCadastro.add(senha).left().padBottom(10).row();

        //Campo Senha
        Table grupoSenha = new Table();
        grupoSenha.setBackground(Estilos.fundoArredondadoClaro);
        grupoSenha.add(new Image(texSenha)).size(24, 24).padLeft(5).padRight(10);
        TextField campoSenha = new TextField("", Estilos.estiloCampoSemFundo);
        campoSenha.setPasswordMode(true);
        campoSenha.setPasswordCharacter('*');
        grupoSenha.add(campoSenha).expandX().fillX().padRight(15);
        cartaoCadastro.add(grupoSenha).width(340).height(50).padBottom(40).row();

        //Botão Cadastrar
        TextButton botaoCadastrar = new TextButton("Cadastrar", Estilos.estiloBotaoEntrar);
        botaoCadastrar.getLabel().setFontScale(1.2f / Estilos.MULTIPLICADOR_HD);
        botaoCadastrar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Cadastrar");
                //Adicionar função para verificar se os campos de senha e confirmar senha possuem o mesmo conteúdo
                //Adicionar função para adicionar usuario e senha no banco
            }
        });
        cartaoCadastro.add(botaoCadastrar).width(180).height(60).padBottom(15).center().row();

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
        cartaoCadastro.add(linkCancelar).center().padBottom(20).row();

        fundo.add(cartaoCadastro).expand().center();
    }

    @Override
    public void render(float delta) {
        // Substitui a cor de fundo original da tela de login antes de desenhar o stage
        com.badlogic.gdx.utils.ScreenUtils.clear(new Color(0.15f, 0.15f, 0.15f, 1f));
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        // Limpa apenas as texturas locais
        if (texUsuario != null) texUsuario.dispose();
        if (texSenha != null) texSenha.dispose();
    }

}
