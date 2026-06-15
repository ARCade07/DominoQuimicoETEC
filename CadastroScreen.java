package com.domino.telas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.domino.bd.ConnectionFactory;
import com.domino.controladores.ControladorLogin;
import com.domino.controladores.ControladorRegistro;
import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;

public class CadastroScreen extends BaseScreen {

    private Texture texUsuario;
    private Texture texSenha;
    private Texture texEmail;

    private UsuarioDao usuarioDao;
    private ControladorRegistro registro;

    private BitmapFont fonteTitulo, fonteNormal, fonteBotao;
    private Label.LabelStyle estiloTitulo;
    private TextField.TextFieldStyle estiloCampoAcessivel;
    private TextButton.TextButtonStyle estiloBotaoAcessivel;
    private Array<Actor> ordemNavegacao = new Array<>();

    public CadastroScreen() {
        super();
        //Texturas específicas tela de cadastro
        texUsuario = new Texture(Gdx.files.internal("User.png"));
        texSenha = new Texture(Gdx.files.internal("Cadeado.png"));
        texEmail = new Texture(Gdx.files.internal("Email.png"));

        ConnectionFactory conexao = ConnectionFactory.getInstance();
        this.usuarioDao = new UsuarioDao(conexao);
        this.registro = new ControladorRegistro(this.usuarioDao);

        inicializarEstilosAcessiveis();
        montarTela();
    }

    private void inicializarEstilosAcessiveis() {
        float escala = GerenciadorAcessibilidade.getEscalaFonteUsuario();

        fonteTitulo = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 36 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteTitulo.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        fonteNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", 20 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteNormal.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        fonteBotao = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 24 * Estilos.MULTIPLICADOR_HD * escala, 4, 2f);
        fonteBotao.getData().setScale(1f / Estilos.MULTIPLICADOR_HD);

        estiloTitulo = new Label.LabelStyle(fonteTitulo, GerenciadorAcessibilidade.getCorTextoTitulo());

        estiloCampoAcessivel = new TextField.TextFieldStyle();
        estiloCampoAcessivel.font = fonteNormal;
        estiloCampoAcessivel.fontColor = GerenciadorAcessibilidade.getCorTextoPadrao();
        estiloCampoAcessivel.cursor = Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorTextoPadrao());
        estiloCampoAcessivel.background = Estilos.estiloCampoSemFundo.background;

        estiloBotaoAcessivel = new TextButton.TextButtonStyle();
        estiloBotaoAcessivel.font = fonteBotao;
        estiloBotaoAcessivel.fontColor = Color.WHITE;
        estiloBotaoAcessivel.focusedFontColor = Color.BLACK;

        Color corFundoBtn = GerenciadorAcessibilidade.getCorDestaqueSucesso();
        Color corSombraBtn = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE ? Color.DARK_GRAY : new Color(corFundoBtn.r * 0.5f, corFundoBtn.g * 0.5f, corFundoBtn.b * 0.5f, 1f);

        estiloBotaoAcessivel.up = Estilos.criarBotao3D(corFundoBtn, corSombraBtn, 12, 6);
        estiloBotaoAcessivel.down = Estilos.criarBotao3D(corFundoBtn, corSombraBtn, 12, 2);
        estiloBotaoAcessivel.focused = Estilos.criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.BLACK, 12, 6);
    }

    private void montarTela() {
        //Cria fundo gradiente
        Table fundo = new Table();
        fundo.setFillParent(true);
        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        fundo.setBackground(altoContraste ? Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()) : Estilos.fundoGradiente);
        stage.addActor(fundo);

        //Cria cartão de cadastro
        Table cartaoCadastro = new Table();
        cartaoCadastro.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 2));
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
                String usernameDigitado = campoUsername.getText();
                String emailDigitado = campoEmail.getText();
                String senhaDigitada = campoSenha.getText();
                Usuario u = new Usuario(usernameDigitado, emailDigitado, senhaDigitada);
                //Adicionar função para verificar se os campos de senha e confirmar senha possuem o mesmo conteúdo
                boolean sucesso = registro.registrarUsuario(u);
                PopUpMensagem popUp = new PopUpMensagem(stage);
                if (sucesso) {
                    System.out.println("Usuário registrado com sucesso.");
                    popUp.showSucesso("Cadastrado com sucesso!");
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
                } else {
                    popUp.showErro("Erro ao cadastrar");
                    System.out.println("Não foi possível realizar o registro");
                }
                //Adicionar função para adicionar usuario e senha no banco
                //Caso algo der errado ao adicionar usuario e senha no banco
            }
        });
        cartaoCadastro.add(botaoCadastrar).width(180).height(60).padBottom(15).center().row();

        //Label Cancelar
        Label linkCancelar = new Label("Cancelar", Estilos.estiloTextoNegrito);
        linkCancelar.setFontScale(0.8f / Estilos.MULTIPLICADOR_HD);
        linkCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
            }
        });
        cartaoCadastro.add(linkCancelar).center().padBottom(20).row();

        fundo.add(cartaoCadastro).expand().center();

        ordemNavegacao.add(campoUsername, campoEmail, campoSenha, botaoCadastrar);
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
        if (fonteBotao != null) fonteBotao.dispose();
    }

}
