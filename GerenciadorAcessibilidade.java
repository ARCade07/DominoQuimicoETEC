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

    public static Color obterCorFundoPadrao() {
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

    public static Color obterCorTextoPadrao() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.valueOf("FFFF00"); //amarelo brilhante
            default:
                return Color.valueOf("333333"); //cinza
        }
    }

    public static Color obterCorDestaqueErro() {
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

    public static Color obterCorDestaqueSucesso() {
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
