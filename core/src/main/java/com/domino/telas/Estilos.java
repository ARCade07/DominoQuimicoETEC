package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Estilos {
    public static final float MULTIPLICADOR_HD = 3.0f;

    // Fontes
    public static BitmapFont fonteNormal;
    public static BitmapFont fonteNegrito;
    public static BitmapFont fonteCampoTexto;

    // Estilos
    public static Label.LabelStyle estiloTextoNormal;
    public static Label.LabelStyle estiloTextoNegrito;
    public static Button.ButtonStyle estiloBotaoGrupo;
    public static TextButton.TextButtonStyle estiloBotaoEntrar;
    public static TextField.TextFieldStyle estiloCampoTexto;
    public static TextField.TextFieldStyle estiloCampoSemFundo;

    // Texturas reaproveitáveis
    public static TextureRegionDrawable fundoGradiente;
    public static NinePatchDrawable fundoArredondadoEscuro;
    public static NinePatchDrawable fundoArredondadoClaro;

    public static void inicializar() {
        // 1. Gerar Fontes
        fonteNormal = gerarFonte("Inter_24pt-Medium.ttf", 24 * MULTIPLICADOR_HD);
        fonteNegrito = gerarFonte("Inter_24pt-Bold.ttf", 24 * MULTIPLICADOR_HD);
        fonteCampoTexto = gerarFonte("Inter_24pt-Medium.ttf", 24);

        // 2. Criar Estilos de Texto
        estiloTextoNormal = new Label.LabelStyle(fonteNormal, Color.WHITE);
        estiloTextoNegrito = new Label.LabelStyle(fonteNegrito, Color.WHITE);

        // 3. Criar Texturas Base Globais
        fundoGradiente = criarTexturaGradiente(Color.valueOf("4A0000"), Color.valueOf("0D0202"));
        TextureRegionDrawable texturaCursor = criarTexturaCor(Color.BLACK);
        texturaCursor.setMinWidth(2f);

        fundoArredondadoEscuro = criarBordaArredondadaTextura(Color.valueOf("1A0404"), Color.valueOf("500000"), 8, 2);
        fundoArredondadoClaro = criarBordaArredondadaTextura(Color.valueOf("F4E7E7"), Color.valueOf("7D0000"), 8, 2);

        // 4. Estilos de Botões
        estiloBotaoGrupo = new Button.ButtonStyle();
        estiloBotaoGrupo.up = fundoArredondadoEscuro;
        estiloBotaoGrupo.over = criarBordaArredondadaTextura(Color.valueOf("271818"), Color.valueOf("500000"), 8, 2);
        estiloBotaoGrupo.down = criarBordaArredondadaTextura(Color.valueOf("080505"), Color.valueOf("500000"), 8, 2);

        estiloBotaoEntrar = new TextButton.TextButtonStyle();
        estiloBotaoEntrar.font = fonteNormal;
        estiloBotaoEntrar.fontColor = Color.valueOf("F4E7E7");
        estiloBotaoEntrar.up = criarBordaArredondadaTextura(Color.valueOf("7D0000"), Color.valueOf("7D0000"), 8, 2);
        estiloBotaoEntrar.over = criarBordaArredondadaTextura(Color.valueOf("957474"), Color.valueOf("957474"), 8, 2);
        estiloBotaoEntrar.down = criarBordaArredondadaTextura(Color.valueOf("500000"), Color.valueOf("500000"), 8, 2);

        // 5. Estilo de Campo de Texto
        estiloCampoTexto = new TextField.TextFieldStyle();
        estiloCampoTexto.font = fonteCampoTexto;
        estiloCampoTexto.fontColor = Color.valueOf("7D0000");
        estiloCampoTexto.background = fundoArredondadoClaro;
        estiloCampoTexto.cursor = texturaCursor;

        estiloCampoSemFundo = new TextField.TextFieldStyle(estiloCampoTexto);
        estiloCampoSemFundo.background = criarTexturaCor(new Color(0, 0, 0, 0));
    }

    public static BitmapFont gerarFonte(String arquivo, float tamanho) {
        return gerarFonte(arquivo, tamanho, 0, 0);
    }

    public static BitmapFont gerarFonte(String arquivo, float tamanho, float pad, float borderWidth) {
        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal(arquivo));
        FreeTypeFontGenerator.FreeTypeFontParameter parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametro.size = (int) tamanho;
        parametro.color = Color.WHITE;
        parametro.genMipMaps = true;
        parametro.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parametro.magFilter = Texture.TextureFilter.Linear;
        parametro.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚãõÃÕâêîôûÂÊÎÔÛçÇ↔←";

        if (borderWidth > 0) {
            parametro.borderWidth = borderWidth;
            parametro.borderColor = new Color(0, 0, 0, 0);
        }
        if (pad > 0) {
            parametro.padTop = (int) pad;
            parametro.padBottom = (int) pad;
            parametro.padLeft = (int) pad;
            parametro.padRight = (int) pad;
            parametro.spaceX = (int) pad;
            parametro.spaceY = (int) pad;
        }

        BitmapFont fonte = gerador.generateFont(parametro);
        fonte.setUseIntegerPositions(false);
        gerador.dispose();
        return fonte;
    }

    public static NinePatchDrawable criarBordaArredondadaTextura(Color corFundo, Color corBorda, int raio, int tamanhoBorda) {
        int escala = 4;
        int tamanho = 100 * escala;
        int r = raio * escala;
        int b = tamanhoBorda * escala;

        Pixmap mapaPixels = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        mapaPixels.setBlending(Pixmap.Blending.None);

        mapaPixels.setColor(corBorda);
        mapaPixels.fillCircle(r, r, r);
        mapaPixels.fillCircle(tamanho - r - 1, r, r);
        mapaPixels.fillCircle(r, tamanho - r - 1, r);
        mapaPixels.fillCircle(tamanho - r - 1, tamanho - r - 1, r);
        mapaPixels.fillRectangle(r, 0, tamanho - 2 * r, tamanho);
        mapaPixels.fillRectangle(0, r, tamanho, tamanho - 2 * r);

        mapaPixels.setColor(corFundo);
        int raioInterno = r - b;
        if (raioInterno < 0) raioInterno = 0;
        mapaPixels.fillCircle(r, r, raioInterno);
        mapaPixels.fillCircle(tamanho - r - 1, r, raioInterno);
        mapaPixels.fillCircle(r, tamanho - r - 1, raioInterno);
        mapaPixels.fillCircle(tamanho - r - 1, tamanho - r - 1, raioInterno);
        mapaPixels.fillRectangle(r, b, tamanho - 2 * r, tamanho - 2 * b);
        mapaPixels.fillRectangle(b, r, tamanho - 2 * b, tamanho - 2 * r);

        Texture textura = new Texture(mapaPixels, true);
        textura.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();

        NinePatch remendo = new NinePatch(textura, r, r, r, r);
        remendo.scale(1f / escala, 1f / escala);

        return new NinePatchDrawable(remendo);
    }

    public static NinePatchDrawable criarBotao3D(Color corCorpo, Color corSombra, int raio, int profundidade) {
        int escala = 4, t = 100 * escala, r = raio * escala, p = profundidade * escala;
        Pixmap pix = new Pixmap(t, t, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(corSombra);
        pix.fillCircle(r, t - r - 1, r);
        pix.fillCircle(t - r - 1, t - r - 1, r);
        pix.fillRectangle(r, t - 2 * r, t - 2 * r, 2 * r);
        pix.fillRectangle(0, t - r - 1 - p, t, p);

        pix.setColor(corCorpo);
        pix.fillCircle(r, r, r);
        pix.fillCircle(t - r - 1, r, r);
        pix.fillCircle(r, t - r - 1 - p, r);
        pix.fillCircle(t - r - 1, t - r - 1 - p, r);
        pix.fillRectangle(r, 0, t - 2 * r, t - p);
        pix.fillRectangle(0, r, t, t - 2 * r - p);

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        NinePatch np = new NinePatch(tex, r, r, r, r + p);
        np.scale(1f / escala, 1f / escala);
        return new NinePatchDrawable(np);
    }

    public static TextureRegionDrawable criarTexturaGradiente(Color corTopo, Color corBase) {
        int altura = 512;
        Pixmap mapaPixels = new Pixmap(1, altura, Pixmap.Format.RGBA8888);

        for (int y = 0; y < altura; y++) {
            float ratio = (float) y / (altura - 1);

            Color corAtual = new Color(corTopo).lerp(corBase, ratio);

            mapaPixels.setColor(corAtual);
            mapaPixels.drawPixel(0, y);
        }

        Texture textura = new Texture(mapaPixels);
        textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();

        return new TextureRegionDrawable(textura);
    }

    public static TextureRegionDrawable criarTexturaCor(Color cor) {
        Pixmap mapaPixels = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        mapaPixels.setColor(cor);
        mapaPixels.fill();
        Texture textura = new Texture(mapaPixels);
        textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mapaPixels.dispose();
        return new TextureRegionDrawable(textura);
    }

    public static TextureRegionDrawable criarIconeOlho(Color cor, boolean fechado, Color corErro) {
        int tamanho = 64;
        Pixmap pix = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);

        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(0, 0, 0, 0);
        pix.fill();

        pix.setColor(cor);
        pix.fillTriangle(6, 32, 32, 12, 58, 32);
        pix.fillTriangle(6, 32, 32, 52, 58, 32);

        pix.setColor(0, 0, 0, 0);
        pix.fillTriangle(12, 32, 32, 17, 52, 32);
        pix.fillTriangle(12, 32, 32, 47, 52, 32);

        pix.setBlending(Pixmap.Blending.SourceOver);

        pix.setColor(cor);
        pix.fillCircle(32, 32, 9);

        if (fechado && corErro != null) {
            pix.setColor(corErro);
            for (int i = -3; i <= 3; i++) {
                pix.drawLine(12 + i, 52, 52 + i, 12);
            }
        }

        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }

    public static TextureRegionDrawable criarCoroaTextura(Color cor) {
        int w = 60;
        int h = 45;
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setBlending(Pixmap.Blending.None);
        pix.setColor(cor);
        pix.fillRectangle(6, 30, 48, 9);
        pix.fillTriangle(6, 30, 21, 30, 3, 6);
        pix.fillTriangle(21, 30, 39, 30, 30, 0);
        pix.fillTriangle(39, 30, 54, 30, 57, 6);
        Texture tex = new Texture(pix, true);
        tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        pix.dispose();
        return new TextureRegionDrawable(tex);
    }


    public static void dispose() {
        if (fonteNormal != null) fonteNormal.dispose();
        if (fonteNegrito != null) fonteNegrito.dispose();
        if (fonteCampoTexto != null) fonteCampoTexto.dispose();
    }
}
