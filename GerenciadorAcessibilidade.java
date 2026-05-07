package com.pidomino;

import com.badlogic.gdx.graphics.Color;

public class GerenciadorAcessibilidade {

    public enum ModoVisao {
        PADRAO,
        ALTO_CONTRASTE,
        PROTANOPIA_DEUTERANOPIA,
        TRITANOPIA
    }

    public static ModoVisao modoVisaoAtual = ModoVisao.PADRAO;

    //cor de fundo de tela
    public static Color getCorFundoTela() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA:
            case TRITANOPIA:
                return Color.valueOf("EAEAEA"); //cinza claro
            default:
                return Color.valueOf("F5F5F5"); //branco
        }
    }

    public static Color getCorFundoCartao() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.BLACK : Color.WHITE;
    }

    public static Color getCorFundoCaixaDestaqueErro() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("FFE8D6");
            case TRITANOPIA: return Color.valueOf("FFD9D9");
            default: return Color.valueOf("FEE1E1");
        }
    }

    //cor das bordas e separadores
    public static Color getCorBordaCartao() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.WHITE : Color.valueOf("D1D5DB");
    }

    public static Color getCorBordaForte() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.YELLOW : Color.valueOf("333333");
    }

    //cor do botao
    public static Color getCorFundoBotaoNormal() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.BLACK : Color.WHITE;
    }

    public static Color getCorFundoBotaoHover() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.valueOf("333333") : Color.valueOf("E8ECEF");
    }

    public static Color getCorFundoBotaoDown() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.valueOf("555555") : Color.valueOf("D1D5DB");
    }

    //cores de texto
    public static Color getCorTextoTitulo() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.YELLOW : Color.BLACK;
    }

    public static Color getCorTextoPadrao() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.valueOf("FFFF00"); //amarelo brilhante
            default:
                return Color.valueOf("333333"); //cinza
        }
    }

    public static Color getCorDestaqueErro() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.RED;
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("D55E00"); //laranja
            case TRITANOPIA:
                return Color.valueOf("CC0000"); //vermelho
            default:
                return Color.valueOf("7D0000"); //vinho
        }
    }

    public static Color getCorDestaqueSucesso() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.GREEN;
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("0072B2"); //azul claro
            case TRITANOPIA:
                return Color.valueOf("009E73"); //ciano
            default:
                return Color.valueOf("2E7D32"); //verde
        }
    }
}
