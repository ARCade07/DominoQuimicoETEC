package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private BitmapFont fonteTitulo, fonteNormal, fontePequena, fonteBotao;
    private Label.LabelStyle estiloTitulo, estiloLink;
    private TextField.TextFieldStyle estiloCampoAcessivel;
    private TextButton.TextButtonStyle estiloBotaoEntrarAcessivel;
    private com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.Actor> ordemNavegacao = new com.badlogic.gdx.utils.Array<>();

    public LoginScreen() {
        super();

        //Texturas específicas tela de login
        texUsuario = new Texture(Gdx.files.internal("User.png"));
        texSenha = new Texture(Gdx.files.internal("Cadeado.png"));

        ConnectionFactory conexao = ConnectionFactory.getInstance();
        this.usuarioDao = new UsuarioDao(conexao);
        this.login = new ControladorLogin(this.usuarioDao);

        inicializarEstilosAcessiveis();
        montarTela();
    }

    @Override
    public void show() {
        super.show();
        GerenciadorAcessibilidade.configurarNavegacao(stage, ordemNavegacao.toArray(com.badlogic.gdx.scenes.scene2d.Actor.class));
    }

    private void inicializarEstilosAcessiveis() {
        float escala = GerenciadorAcessibilidade.getEscalaFonteUsuario();

        fonteTitulo = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 36 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteTitulo.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        fonteNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", 20 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteNormal.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        fontePequena = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 14 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fontePequena.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        fonteBotao = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 24 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteBotao.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        estiloTitulo = new Label.LabelStyle(fonteTitulo, GerenciadorAcessibilidade.getCorTextoTitulo());
        estiloLink = new Label.LabelStyle(fontePequena, GerenciadorAcessibilidade.getCorTextoPadrao());

        estiloCampoAcessivel = new TextField.TextFieldStyle();
        estiloCampoAcessivel.font = fonteNormal;
        estiloCampoAcessivel.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        estiloCampoAcessivel.cursor = Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorTextoPadrao());
        estiloCampoAcessivel.cursor.setMinWidth(2);
        estiloCampoAcessivel.background = Estilos.estiloCampoSemFundo.background;

        estiloBotaoEntrarAcessivel = new TextButton.TextButtonStyle();
        estiloBotaoEntrarAcessivel.font = fonteBotao;
        estiloBotaoEntrarAcessivel.fontColor = Color.WHITE;
        estiloBotaoEntrarAcessivel.focusedFontColor = Color.BLACK;

        Color corFundoBtn = GerenciadorAcessibilidade.getCorDestaqueSucesso();
        Color corSombraBtn = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE ? Color.DARK_GRAY : new Color(corFundoBtn.r * 0.5f, corFundoBtn.g * 0.5f, corFundoBtn.b * 0.5f, 1f);

        estiloBotaoEntrarAcessivel.up = Estilos.criarBotao3D(corFundoBtn, corSombraBtn, 12, 6);
        estiloBotaoEntrarAcessivel.down = Estilos.criarBotao3D(corFundoBtn, corSombraBtn, 12, 2);
        estiloBotaoEntrarAcessivel.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.BLACK, 12, 6);
    }

    private void montarTela() {
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        fundo.setBackground(altoContraste ? Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()) : Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de login
        Table cartaoLogin = new Table();
        cartaoLogin.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 2));
        cartaoLogin.pad(60, 80, 60, 80);

        //Imagem User
        NinePatchDrawable fundoUsuario = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorBordaCartao(), 45, 2);
        Stack stackUsuario = new Stack();
        stackUsuario.add(new Image(fundoUsuario));
        Table tabelaIcone = new Table();
        tabelaIcone.add(new Image(texUsuario)).size(60, 60);
        stackUsuario.add(tabelaIcone);
        cartaoLogin.add(stackUsuario).width(90).height(90).center().padBottom(20).row();

        //Título
        Label titulo = new Label("Login", estiloTitulo);
        cartaoLogin.add(titulo).left().padBottom(40).row();

        NinePatchDrawable bgInput = Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoTela(), GerenciadorAcessibilidade.getCorBordaCartao(), 8, 2);

        //Campo Username
        Table grupoUsername = new Table();
        grupoUsername.setBackground(bgInput); // [MODIFICADO]
        grupoUsername.add(new Image(texUsuario)).size(24, 24).padLeft(5).padRight(10);
        TextField campoUsername = new TextField("", estiloCampoAcessivel); // [MODIFICADO]
        grupoUsername.add(campoUsername).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoUsername).width(340).height(50).padBottom(40).row();

        //Campo Senha
        Table grupoSenha = new Table();
        grupoSenha.setBackground(bgInput); // [MODIFICADO]
        grupoSenha.add(new Image(texSenha)).size(24, 24).padLeft(5).padRight(10);
        TextField campoSenha = new TextField("", estiloCampoAcessivel); // [MODIFICADO]
        campoSenha.setPasswordMode(true);
        campoSenha.setPasswordCharacter('*');
        grupoSenha.add(campoSenha).expandX().fillX().padRight(15);
        cartaoLogin.add(grupoSenha).width(340).height(50).padBottom(10).row();

        //Label Esqueceu senha
        Label linkEsqueceuSenha = new Label("Esqueceu a senha?", estiloLink);
        linkEsqueceuSenha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ForgotPasswordScreen());
            }
        });
        cartaoLogin.add(linkEsqueceuSenha).right().padBottom(20).row();

        //Botão Entrar
        TextButton botaoEntrar = new TextButton("Entrar", estiloBotaoEntrarAcessivel);
        botaoEntrar.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String emailDigitado = campoUsername.getText();
                String senhaDigitada = campoSenha.getText();

                boolean sucesso = login.fazerLogin(emailDigitado, senhaDigitada);

                //Adicionar função para verificar usuario e senha no banco
                PopUpMensagem popUp = new PopUpMensagem(stage);
                if (sucesso) {

                    Usuario usuarioLogado = login.getUsuarioLogado();
                    Sessao.setUsuario(usuarioLogado);
                    String papel = usuarioLogado.getRole();

                    System.out.println("Login efetuado com sucesso como " + papel);
                    popUp.showSucesso("Login efetuado com sucesso como " + papel);
                    if (papel != null && (papel.equalsIgnoreCase("Professor"))){
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new TeacherScreen());
                    } else {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new StartScreen());
                    }
                } else {
                    popUp.showErro("Erro: E-mail ou senha incorretos!");
                    System.out.println("Erro: E-mail ou senha incorretos!");
                    campoSenha.setText("");
                }

            }
        });
        cartaoLogin.add(botaoEntrar).width(180).height(60).padBottom(15).center().row();

        Label linkCadastreSe = new Label("Cadastrar-se", estiloLink);
        linkCadastreSe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CadastroScreen());
                //Função para trocar de tela
            }
        });
        cartaoLogin.add(linkCadastreSe).center().padBottom(20).row();

        fundo.add(cartaoLogin).expand().center();

        ordemNavegacao.add(campoUsername);
        ordemNavegacao.add(campoSenha);
        ordemNavegacao.add(botaoEntrar);

        GerenciadorAcessibilidade.aplicarFoco(campoUsername);
        stage.setKeyboardFocus(campoUsername);
    }

    @Override
    public void render(float delta) {
        // Substitui a cor de fundo original da tela de login antes de desenhar o stage
        com.badlogic.gdx.utils.ScreenUtils.clear(GerenciadorAcessibilidade.getCorFundoTela());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        // Limpa apenas as texturas locais
        if (texUsuario != null) texUsuario.dispose();
        if (texSenha != null) texSenha.dispose();
        if (fonteTitulo != null) fonteTitulo.dispose();
        if (fonteNormal != null) fonteNormal.dispose();
        if (fontePequena != null) fontePequena.dispose();
        if (fonteBotao != null) fonteBotao.dispose();
    }

}
