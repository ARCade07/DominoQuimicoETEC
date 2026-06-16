package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorLogin;
import com.domino.controladores.ControladorRecuperacao;
import com.domino.controladores.ControladorRegistro;
import com.domino.dao.UsuarioDao;
import com.domino.modelos.Sessao;
import com.domino.modelos.Usuario;

import javax.swing.*;

public class LoginScreen extends BaseScreen {

    private Texture texUsuario;
    private Texture texSenha;
    private UsuarioDao usuarioDao;
    private ControladorLogin login;

    public LoginScreen() {
        super();

        //Texturas específicas tela de login
        texUsuario = new Texture(Gdx.files.internal("User.png"));
        texSenha = new Texture(Gdx.files.internal("Cadeado.png"));

        ConnectionFactory conexao = ConnectionFactory.getInstance();

        // Verificar conexão com o banco de dados
        if (!conexao.isConnected()) {
            System.err.println("⚠ Aviso: Conexão com o banco de dados pode estar indisponível");
            System.err.println("Status: " + conexao.getStatus());
        }

        this.usuarioDao = new UsuarioDao(conexao);
        this.login = new ControladorLogin(this.usuarioDao);

        montarTela();
    }

    private void montarTela() {
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        fundo.setBackground(Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de login
        Table cartaoLogin = new Table();
        cartaoLogin.setBackground(Estilos.fundoArredondadoEscuro);
        cartaoLogin.pad(60, 80, 60, 80);

        //Imagem User
        NinePatchDrawable fundoUsuario = Estilos.criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 45, 2);
        Stack stackUsuario = new Stack();
        stackUsuario.add(new Image(fundoUsuario));
        Table tabelaIcone = new Table();
        tabelaIcone.add(new Image(texUsuario)).size(60, 60);
        stackUsuario.add(tabelaIcone);
        cartaoLogin.add(stackUsuario).width(90).height(90).center().padBottom(20).row();

        //Título
        Label titulo = new Label("Login", Estilos.estiloTextoNormal);
        titulo.setFontScale(1.8f / Estilos.MULTIPLICADOR_HD);
        cartaoLogin.add(titulo).left().padBottom(40).row();

        //Campo Username
        Table grupoUsername = new Table();
        grupoUsername.setBackground(Estilos.fundoArredondadoClaro);
        grupoUsername.add(new Image(texUsuario)).size(24, 24).padLeft(5).padRight(10);
        TextField campoUsername = new TextField("", Estilos.estiloCampoSemFundo);
        grupoUsername.add(campoUsername).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoUsername).width(340).height(50).padBottom(40).row();

        //Campo Senha
        Table grupoSenha = new Table();
        grupoSenha.setBackground(Estilos.fundoArredondadoClaro);
        grupoSenha.add(new Image(texSenha)).size(24, 24).padLeft(5).padRight(10);
        TextField campoSenha = new TextField("", Estilos.estiloCampoSemFundo);
        campoSenha.setPasswordMode(true);
        campoSenha.setPasswordCharacter('*');
        grupoSenha.add(campoSenha).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoSenha).width(340).height(50).padBottom(10).row();

        //Label Esqueceu senha
        Label linkEsqueceuSenha = new Label("Esqueceu a senha?", Estilos.estiloTextoNegrito);
        linkEsqueceuSenha.setFontScale(0.6f / Estilos.MULTIPLICADOR_HD);
        linkEsqueceuSenha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ForgotPasswordScreen());
            }
        });
        cartaoLogin.add(linkEsqueceuSenha).right().padBottom(20).row();

        //Botão Entrar
        TextButton botaoEntrar = new TextButton("Entrar", Estilos.estiloBotaoEntrar);
        botaoEntrar.getLabel().setFontScale(1.2f / Estilos.MULTIPLICADOR_HD);
        botaoEntrar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String emailDigitado = campoUsername.getText();
                String senhaDigitada = campoSenha.getText();
                PopUpMensagem popUp = new PopUpMensagem(stage);

                // Verificar conexão antes de tentar login
                ConnectionFactory conexao = ConnectionFactory.getInstance();
                if (!conexao.isConnected()) {
                    popUp.showErro("Erro: Sem conexão com o servidor.\nTente novamente mais tarde.");
                    System.err.println("❌ Tentativa de login sem conexão com BD");
                    return;
                }

                try {
                    boolean sucesso = login.fazerLogin(emailDigitado, senhaDigitada);
                    System.out.println("Clicou em Entrar!");

                    if (sucesso) {
                        Usuario usuarioLogado = login.getUsuarioLogado();
                        Sessao.setUsuario(usuarioLogado);
                        String papel = usuarioLogado.getRole();

                        System.out.println("✓ Login efetuado com sucesso como " + papel);
                        popUp.showSucesso("Login efetuado com sucesso como " + papel);
                        if (papel != null && (papel.equalsIgnoreCase("Professor"))) {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TeacherScreen());
                        } else {
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new StartScreen());
                        }
                    } else {
                        popUp.showErro("Erro: E-mail ou senha incorretos!");
                        System.out.println("❌ Erro: E-mail ou senha incorretos!");
                        campoSenha.setText("");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erro inesperado durante login: " + e.getMessage());
                    e.printStackTrace();
                    popUp.showErro("Erro inesperado. Tente novamente.");
                    campoSenha.setText("");
                }
            }
        });
        cartaoLogin.add(botaoEntrar).width(180).height(60).padBottom(15).center().row();

        //Label Cadastre-se
        Label linkCadastreSe = new Label("Cadastrar-se", Estilos.estiloTextoNegrito);
        linkCadastreSe.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        linkCadastreSe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Teste para ver se funcionou o clicker (tirar depois)
                System.out.println("Clicou em Cadastrar-se!");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CadastroScreen());
                //Função para trocar de tela
            }
        });
        cartaoLogin.add(linkCadastreSe).center().padBottom(20).row();

        fundo.add(cartaoLogin).expand().center();
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
