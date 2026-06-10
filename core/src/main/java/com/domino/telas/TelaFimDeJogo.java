package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.domino.rede.Cliente;
import com.domino.rede.Servidor;
import com.domino.rede.packets.PacketResultadoJogo;
import com.domino.rede.packets.PacketResultadoJogador;
import com.domino.rede.packets.PacketVoltaProLobby;

public class TelaFimDeJogo extends BaseScreen {

    private BitmapFont fonteNormal;
    private BitmapFont fonteNegrito;

    private Label.LabelStyle estiloTitulo;
    private Label.LabelStyle estiloSubtitulo;
    private Label.LabelStyle estiloTexto;
    private Label.LabelStyle estiloDestaque;

    private Array<Actor> ordemNavegacao;
    private final ResultadoJogador[] resultados;
    private Servidor servidor;
    private Cliente cliente;

    private static class ResultadoJogador {
        String nome;
        int pontuacao;
        boolean isVoce;

        ResultadoJogador(String nome, int pontuacao, boolean isVoce) {
            this.nome = nome;
            this.pontuacao = pontuacao;
            this.isVoce = isVoce;
        }
    }

    public TelaFimDeJogo(PacketResultadoJogo resultadoRede, int meuId ,Cliente cliente, Servidor servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
        resultados = new ResultadoJogador[resultadoRede.resultadoFinal.size()];
        for (int i = 0; i < resultadoRede.resultadoFinal.size(); i++) {
            PacketResultadoJogador r = resultadoRede.resultadoFinal.get(i);
            boolean isVoce = (r.idJogador == meuId);
            resultados[i] = new ResultadoJogador(isVoce ? "Voce (Jogador " + r.idJogador + ")" : "Jogador " + r.idJogador, r.pontuacao, isVoce);
        }

        ordemNavegacao = new Array<>();

        carregarFontes();
        construirInterface();

        GerenciadorAcessibilidade.configurarNavegacao(stage, ordemNavegacao.toArray(Actor.class));
    }

    @Override
    public void render(float delta) {
        super.render(Math.min(delta, 1 / 30f));
    }

    //logica exclusiva dessa tela para o amarelo ambar funcionar com acessibilidade
    private Color getCorDestaqueVitoria() {
        switch (GerenciadorAcessibilidade.modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.GREEN; //verde puro
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("FFD700"); //amarelo
            case TRITANOPIA:
            case PADRAO:
            default:
                return Color.valueOf("F39C12"); //amarelo ambar
        }
    }

    private void carregarFontes() {
        fonteNormal = Estilos.gerarFonte("Inter_24pt-Medium.ttf", 36);
        fonteNegrito = Estilos.gerarFonte("Inter_24pt-Bold.ttf", 36);

        estiloTitulo = new Label.LabelStyle(fonteNegrito, GerenciadorAcessibilidade.getCorTextoTitulo());
        estiloSubtitulo = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoFraco());
        estiloTexto = new Label.LabelStyle(fonteNormal, GerenciadorAcessibilidade.getCorTextoPadrao());
        estiloDestaque = new Label.LabelStyle(fonteNegrito, getCorDestaqueVitoria());
    }

    private void construirInterface() {
        boolean altoContraste = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.ALTO_CONTRASTE;
        boolean protanopia = GerenciadorAcessibilidade.modoVisaoAtual == GerenciadorAcessibilidade.ModoVisao.PROTANOPIA_DEUTERANOPIA;
        boolean isVitoria = resultados[0].isVoce;

        Image imgFundo = new Image();
        if (altoContraste) {
            imgFundo.setDrawable(Estilos.criarTexturaCor(GerenciadorAcessibilidade.getCorFundoTela()));
        } else {
            Color corTopo = protanopia ? Color.valueOf("0A1428") : Color.valueOf("4A0000");
            Color corBase = protanopia ? Color.valueOf("02050A") : Color.valueOf("0D0202");
            imgFundo.setDrawable(GerenciadorAcessibilidade.criarTexturaGradiente(corTopo, corBase));
        }
        imgFundo.setFillParent(true);
        stage.addActor(imgFundo);

        if (!altoContraste) {
            if (isVitoria) {
                Image glow = new Image(criarGlowRadial(GerenciadorAcessibilidade.getCorTextoTitulo(), 600));
                glow.setPosition((1920 - 1200) / 2f, (1080 - 1200) / 2f + 50);
                stage.addActor(glow);
            }
            adicionarConfetes(isVitoria);
        }

        Table raiz = new Table();
        raiz.setFillParent(true);
        stage.addActor(raiz);

        Label lblTitulo = criarRotulo(isVitoria ? "VITÓRIA!" : "DERROTA", estiloTitulo, 2.2f);

        lblTitulo.setColor(isVitoria ? getCorDestaqueVitoria() : GerenciadorAcessibilidade.getCorDestaqueErro());
        lblTitulo.getColor().a = 0f;
        lblTitulo.addAction(Actions.sequence(
            Actions.moveBy(0, 50),
            Actions.parallel(Actions.fadeIn(0.8f), Actions.moveBy(0, -50, 0.8f, com.badlogic.gdx.math.Interpolation.bounceOut))
        ));
        raiz.add(lblTitulo).center().padTop(60).padBottom(40).row();

        Table painelResultados = new Table();
        painelResultados.setBackground(Estilos.criarBordaArredondadaTextura(GerenciadorAcessibilidade.getCorFundoCartao(), GerenciadorAcessibilidade.getCorBordaForte(), 16, 2));
        painelResultados.pad(40);

        painelResultados.add(criarRotulo("PONTUAÇÃO FINAL DA PARTIDA", estiloSubtitulo, 0.8f)).center().padBottom(30).row();

        for (int i = 0; i < resultados.length; i++) {
            Table linha = criarLinhaJogador(resultados[i], i == 0);
            linha.getColor().a = 0f;
            linha.addAction(Actions.delay(0.2f * i, Actions.fadeIn(0.5f)));
            painelResultados.add(linha).growX().height(80).padBottom(15).row();
        }

        raiz.add(painelResultados).width(Value.percentWidth(0.50f, raiz)).center().padBottom(50).row();

        Table areaBotoes = new Table();
        Color corSombraBase = altoContraste ? Color.DARK_GRAY : (protanopia ? Color.valueOf("001F4D") : Color.valueOf("4D0000"));

        TextButton.TextButtonStyle estiloSair = new TextButton.TextButtonStyle();
        estiloSair.font = fonteNegrito;
        estiloSair.fontColor = Color.WHITE;
        estiloSair.up = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoNormal(), corSombraBase, 16, 6);
        estiloSair.over = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoHover(), corSombraBase, 16, 6);
        estiloSair.down = criarBotao3D(GerenciadorAcessibilidade.getCorFundoBotaoDown(), corSombraBase, 16, 2);
        estiloSair.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), Color.valueOf("B8860B"), 16, 6);
        estiloSair.focusedFontColor = Color.BLACK;

        TextButton btnSair = new TextButton("SAIR DA SALA", estiloSair);
        btnSair.getLabel().setFontScale(0.85f / Estilos.MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnSair);
        ordemNavegacao.add(btnSair);
        btnSair.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new RankingScreen());
            }
        });

        Color corFundoBtnVitoria = getCorDestaqueVitoria();
        Color corSombraBtnVitoria = altoContraste ? Color.DARK_GRAY : new Color(corFundoBtnVitoria.r * 0.5f, corFundoBtnVitoria.g * 0.5f, corFundoBtnVitoria.b * 0.5f, 1f);

        TextButton.TextButtonStyle estiloJogarNovamente = new TextButton.TextButtonStyle();
        estiloJogarNovamente.font = fonteNegrito;
        estiloJogarNovamente.fontColor = Color.BLACK;
        estiloJogarNovamente.up = criarBotao3D(corFundoBtnVitoria, corSombraBtnVitoria, 16, 6);
        estiloJogarNovamente.over = criarBotao3D(new Color(corFundoBtnVitoria.r * 1.2f, corFundoBtnVitoria.g * 1.2f, corFundoBtnVitoria.b * 1.2f, 1f), corSombraBtnVitoria, 16, 6);
        estiloJogarNovamente.down = criarBotao3D(new Color(corFundoBtnVitoria.r * 0.8f, corFundoBtnVitoria.g * 0.8f, corFundoBtnVitoria.b * 0.8f, 1f), corSombraBtnVitoria, 16, 2);
        estiloJogarNovamente.focused = criarBotao3D(GerenciadorAcessibilidade.getCorDestaqueFoco(), corSombraBtnVitoria, 16, 6);
        estiloJogarNovamente.focusedFontColor = Color.BLACK;

        TextButton btnJogarNovamente = new TextButton("JOGAR NOVAMENTE", estiloJogarNovamente);
        btnJogarNovamente.getLabel().setFontScale(0.85f / Estilos.MULTIPLICADOR_HD);
        GerenciadorAcessibilidade.aplicarFoco(btnJogarNovamente);
        ordemNavegacao.add(btnJogarNovamente);

        if(servidor != null){

            btnJogarNovamente.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    servidor.botaoJogarNovamente();

                }
            });
        }


        areaBotoes.add(btnSair).width(350).height(80).padRight(30);
        areaBotoes.add(btnJogarNovamente).width(450).height(80);
        areaBotoes.getColor().a = 0f;
        areaBotoes.addAction(Actions.delay(1.5f, Actions.fadeIn(1f)));

        raiz.add(areaBotoes).center().row();
    }

    private Table criarLinhaJogador(final ResultadoJogador jogador, boolean isVencedor) {
        Table linha = new Table();
        Color bgCor = jogador.isVoce ? new Color(1f, 1f, 1f, 0.12f) : GerenciadorAcessibilidade.getCorFundoTela();
        Color bordaCor = jogador.isVoce ? GerenciadorAcessibilidade.getCorDestaqueFoco() : GerenciadorAcessibilidade.getCorBordaCartao();

        linha.setBackground(Estilos.criarBordaArredondadaTextura(bgCor, bordaCor, 18, jogador.isVoce ? 3 : 2));
        linha.padLeft(25).padRight(25);

        Table esquerda = new Table();
        if (isVencedor) esquerda.add(new Image(criarCoroaTextura(GerenciadorAcessibilidade.getCorTextoTitulo()))).size(36, 27).padRight(15);
        esquerda.add(criarRotulo(jogador.nome, jogador.isVoce ? estiloTitulo : estiloTexto, 0.9f));
        linha.add(esquerda).left().expandX();

        final Label lblPontos = criarRotulo("0 pts", estiloDestaque, 1.1f);
        if (!isVencedor) lblPontos.setColor(GerenciadorAcessibilidade.getCorTextoPadrao());

        lblPontos.addAction(Actions.delay(0.5f, new Action() {
            float t = 0;
            @Override
            public boolean act(float delta) {
                t += delta;
                float p = Math.min(t / 2.0f, 1f);
                float ease = 1f - (float)Math.pow(1f - p, 3);
                lblPontos.setText((p >= 1f ? jogador.pontuacao : (int)(jogador.pontuacao * ease)) + " pts");
                return p >= 1f;
            }
        }));

        linha.add(lblPontos).right();
        return linha;
    }

    //confetes do fundo
    private void adicionarConfetes(boolean isVitoria) {
        Color[] paleta = isVitoria ? new Color[]{getCorDestaqueVitoria(), GerenciadorAcessibilidade.getCorDestaqueFoco(), GerenciadorAcessibilidade.getCorDestaqueErro(), Color.WHITE}
            : new Color[]{Color.DARK_GRAY, Color.GRAY, new Color(0.3f, 0.3f, 0.3f, 1f)};

        int quantidade = isVitoria ? 60 : 20;

        for (int i = 0; i < quantidade; i++) {
            Image confete = new Image(criarConfeteTextura(paleta[MathUtils.random(paleta.length - 1)]));

            float escalaParallax = MathUtils.random(0.4f, 1.5f);
            float alpha = Math.max(0.3f, escalaParallax * 0.7f);

            confete.getColor().a = alpha;
            confete.setScale(0f);
            confete.setOrigin(Align.center);
            confete.addAction(Actions.scaleTo(escalaParallax, escalaParallax, 0.6f, com.badlogic.gdx.math.Interpolation.swingOut));

            float tempoGiro = MathUtils.random(0.3f, 0.9f);
            confete.addAction(Actions.forever(Actions.parallel(
                Actions.rotateBy(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? 1 : -1), 1f),
                Actions.sequence(
                    Actions.scaleTo(0f, escalaParallax, tempoGiro),
                    Actions.scaleTo(escalaParallax, escalaParallax, tempoGiro)
                )
            )));

            if (isVitoria) {
                float startX = 1920f / 2f;
                float startY = -100f;
                confete.setPosition(startX, startY);

                float destX = startX + MathUtils.random(-1200f, 1200f);
                float destY = MathUtils.random(600f, 1400f);

                float tempoSubida = MathUtils.random(1.0f, 2.5f);
                float tempoQueda = MathUtils.random(2.0f, 4.0f) * (2f - escalaParallax);

                confete.addAction(Actions.sequence(
                    Actions.moveTo(destX, destY, tempoSubida, com.badlogic.gdx.math.Interpolation.circleOut),
                    Actions.moveBy(MathUtils.random(-200f, 200f), -2000f, tempoQueda, com.badlogic.gdx.math.Interpolation.pow2In),
                    Actions.removeActor()
                ));
            } else {
                float startX = MathUtils.random(0f, 1920f);
                float startY = MathUtils.random(1100f, 1500f);
                confete.setPosition(startX, startY);

                float tempoQueda = MathUtils.random(4f, 8f) * (2f - escalaParallax);
                float balancoX = MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? 1 : -1);

                confete.addAction(Actions.forever(Actions.sequence(
                    Actions.moveBy(balancoX, 0, tempoQueda / 4, com.badlogic.gdx.math.Interpolation.sine),
                    Actions.moveBy(-balancoX, 0, tempoQueda / 4, com.badlogic.gdx.math.Interpolation.sine)
                )));

                confete.addAction(Actions.sequence(
                    Actions.moveBy(0, -2000f, tempoQueda),
                    Actions.removeActor()
                ));
            }
            stage.addActor(confete);
        }
    }

    private TextureRegionDrawable criarConfeteTextura(Color cor) {
        Pixmap pix = new Pixmap(12, 16, Pixmap.Format.RGBA8888);

        pix.setColor(cor);

        if (MathUtils.randomBoolean()) {
            pix.fillRectangle(0, 0, 12, 16);
        } else {
            pix.fillCircle(6, 8, 6);
        }

        Texture tex = new Texture(pix);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextureRegionDrawable criarGlowRadial(Color cor, int raio) {
        Pixmap pix = new Pixmap(raio * 2, raio * 2, Pixmap.Format.RGBA8888);
        for (int x = 0; x < raio * 2; x++) {
            for (int y = 0; y < raio * 2; y++) {
                float dist = (float) Math.hypot(x - raio, y - raio);
                if (dist <= raio) {
                    pix.setColor(cor.r, cor.g, cor.b, (1f - dist / raio) * 0.25f);
                    pix.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(pix);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private Label criarRotulo(String texto, Label.LabelStyle estilo, float escala) {
        Label rotulo = new Label(texto, estilo);
        rotulo.setFontScale(escala / Estilos.MULTIPLICADOR_HD);
        return rotulo;
    }

    private TextureRegionDrawable criarCoroaTextura(Color cor) {
        Pixmap pix = new Pixmap(60, 45, Pixmap.Format.RGBA8888);
        pix.setColor(cor);
        pix.fillRectangle(6, 30, 48, 9);
        pix.fillTriangle(6, 30, 21, 30, 3, 6);
        pix.fillTriangle(21, 30, 39, 30, 30, 0);
        pix.fillTriangle(39, 30, 54, 30, 57, 6);
        Texture tex = new Texture(pix);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    private NinePatchDrawable criarBotao3D(Color corpo, Color sombra, int raio, int p) {
        int r = raio * 4, p4 = p * 4;
        Pixmap pix = new Pixmap(400, 400, Pixmap.Format.RGBA8888);
        pix.setColor(sombra);
        pix.fillCircle(r, 399 - r, r); pix.fillCircle(399 - r, 399 - r, r);
        pix.fillRectangle(r, 400 - 2 * r, 400 - 2 * r, 2 * r); pix.fillRectangle(0, 399 - r - p4, 400, p4);
        pix.setColor(corpo);
        pix.fillCircle(r, r, r); pix.fillCircle(399 - r, r, r);
        pix.fillCircle(r, 399 - r - p4, r); pix.fillCircle(399 - r, 399 - r - p4, r);
        pix.fillRectangle(r, 0, 400 - 2 * r, 400 - p4); pix.fillRectangle(0, r, 400, 400 - 2 * r - p4);

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r + p4);
        np.scale(0.25f, 0.25f);
        return new NinePatchDrawable(np);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fonteNormal != null) fonteNormal.dispose();
        if (fonteNegrito != null) fonteNegrito.dispose();
    }
}
