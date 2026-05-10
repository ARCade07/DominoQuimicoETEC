package com.pidomino;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class GerenciadorAcessibilidade {

    public enum ModoVisao {
        PADRAO,
        ALTO_CONTRASTE,
        PROTANOPIA_DEUTERANOPIA,
        TRITANOPIA
    }

    public static ModoVisao modoVisaoAtual = ModoVisao.PADRAO;

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

    public enum TamanhoFonte {
        PEQUENO(0.75f),
        MEDIO(0.85f),
        GRANDE(1.0f);

        public final float fator;
        TamanhoFonte(float fator) { this.fator = fator; }
    }

    public static TamanhoFonte tamanhoFonteAtual = TamanhoFonte.GRANDE;

    public static float getEscalaFonteUsuario() {
        return tamanhoFonteAtual.fator;
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
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.valueOf("FFFF00") : Color.valueOf("333333");
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

    //navegacao e foco pelo teclado
    public static Color getCorDestaqueFoco() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.YELLOW; //contraste máximo
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("0072B2"); // Azul forte
            case TRITANOPIA: return Color.valueOf("009E73"); // Ciano forte
            default: return Color.valueOf("FFD700"); //dourado padrão
        }
    }

    //aplica acessibilidade motora
    public static void aplicarFoco(Actor ator) {
        //garante que se o botao crescer ele vai crescer a partir do centro e nao do canto
        ator.setOrigin(Align.center);
        ator.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    //o botao da um leve zoom
                    actor.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));

                    //fallback de cor apenas se nao for um botao
                    if (!(actor instanceof Button)) {
                        actor.setColor(getCorDestaqueFoco());
                    }

                } else {
                    //volta o tamanho ao normal
                    actor.addAction(Actions.scaleTo(1f, 1f, 0.1f));

                    if (!(actor instanceof Button)) {
                        actor.setColor(Color.WHITE);
                    }
                }
            }
        });
    }

    //config do sistema pra aceitar teclas para navegacao e clique
    public static void configurarNavegacao(Stage palco, Actor... atoresFocaveis) {
        if (atoresFocaveis.length == 0) return;

        palco.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                Actor focoAtual = palco.getKeyboardFocus();

                //acao de clicar com enter ou espaco
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.NUMPAD_ENTER) {
                    if (focoAtual != null) {
                        //procura os cliques do botao focado e executa
                        for (com.badlogic.gdx.scenes.scene2d.EventListener listener : focoAtual.getListeners()) {
                            if (listener instanceof com.badlogic.gdx.scenes.scene2d.utils.ClickListener) {
                                ((com.badlogic.gdx.scenes.scene2d.utils.ClickListener) listener).clicked(new InputEvent(), 0, 0);
                            }
                        }
                        return true;
                    }
                }

                //navegacao para frente com shift+tab, tab ou setas
                boolean irParaFrente = (keycode == Input.Keys.TAB && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    || keycode == Input.Keys.RIGHT
                    || keycode == Input.Keys.DOWN;

                //navegacao para tras com shift+tab, tab ou setas
                boolean irParaTras = (keycode == Input.Keys.TAB && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    || keycode == Input.Keys.LEFT
                    || keycode == Input.Keys.UP;

                if (irParaFrente || irParaTras) {
                    int indiceAtual = -1;

                    //descobre qual botao ta focado no momento
                    for (int i = 0; i < atoresFocaveis.length; i++) {
                        if (atoresFocaveis[i] == focoAtual) {
                            indiceAtual = i;
                            break;
                        }
                    }

                    int proximoIndice;
                    if (irParaFrente) {
                        proximoIndice = (indiceAtual + 1) % atoresFocaveis.length; //vai pro proximo
                    } else {
                        proximoIndice = (indiceAtual - 1); //vai pro anterior
                        if (proximoIndice < 0) proximoIndice = atoresFocaveis.length - 1; //se passar de zero vai pro ultimo
                    }

                    //muda o foco e avisa o stage
                    palco.setKeyboardFocus(atoresFocaveis[proximoIndice]);
                    return true;
                }
                return false;
            }
        });
    }
}
