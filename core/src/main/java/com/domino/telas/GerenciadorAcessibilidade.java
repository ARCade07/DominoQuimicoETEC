package com.domino.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

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
                return Color.valueOf("050A14"); // Fundo Azul muito escuro
            case TRITANOPIA:
            case PADRAO:
            default:
                return Color.valueOf("0D0202"); // Fundo Vermelho muito escuro
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
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("0A1428"); // Cartão Azul Escuro
            case TRITANOPIA:
            case PADRAO:
            default:
                return Color.valueOf("140505"); // Cartão Vermelho Escuro
        }
    }

    public static Color getCorFundoCaixaDestaqueErro() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("2B1300"); // Marrom/Laranja escuro
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("330808");
        }
    }

    //cor das bordas e separadores
    public static Color getCorBordaCartao() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.WHITE;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("1E3A5F"); // Azul acinzentado
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("350A0A");
        }
    }

    public static Color getCorBordaForte() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.YELLOW;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("2A5285"); // Azul mais claro
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("4A0808");
        }
    }

    //cor do botao
    public static Color getCorFundoBotaoNormal() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("0055A4"); // Azul forte e legível
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("7D0000");
        }
    }

    public static Color getCorFundoBotaoHover() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.valueOf("333333");
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("3377BD"); // Hover azul
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("957474");
        }
    }

    public static Color getCorFundoBotaoDown() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.valueOf("555555");
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("003366"); // Clique azul escuro
            case TRITANOPIA:
            case PADRAO:
            default: return Color.valueOf("500000");
        }
    }

    //cores de texto
    public static Color getCorTextoTitulo() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.YELLOW : Color.WHITE;
    }

    public static Color getCorTextoPadrao() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.YELLOW : Color.WHITE;
    }

    public static Color getCorTextoFraco() {
        return modoVisaoAtual == ModoVisao.ALTO_CONTRASTE ? Color.WHITE : Color.LIGHT_GRAY;
    }

    public static Color getCorDestaqueErro() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.RED;
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("FF6B6B");
            case TRITANOPIA:
            case PADRAO:
            default:
                return Color.valueOf("FF5252");
        }
    }

    public static Color getCorDestaqueSucesso() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE:
                return Color.GREEN;
            case PROTANOPIA_DEUTERANOPIA:
                return Color.valueOf("FFD700");
            case TRITANOPIA:
            case PADRAO:
            default:
                return Color.valueOf("00BFA5");
        }
    }

    //navegacao e foco pelo teclado
    public static Color getCorDestaqueFoco() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.YELLOW;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("F0E442"); // Amarelo acessível
            case TRITANOPIA: return Color.valueOf("00FFFF"); // Ciano (já que amarelo é invisível para eles)
            case PADRAO:
            default: return Color.valueOf("FFD700");
        }
    }

    //aplica acessibilidade motora
    public static void aplicarFoco(Actor ator) {
        ator.setOrigin(Align.center);
        ator.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    actor.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
                    if (!(actor instanceof Button)) {
                        actor.setColor(getCorDestaqueFoco());
                    }
                } else {
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
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.NUMPAD_ENTER) {
                    if (focoAtual != null) {
                        for (com.badlogic.gdx.scenes.scene2d.EventListener listener : focoAtual.getListeners()) {
                            if (listener instanceof com.badlogic.gdx.scenes.scene2d.utils.ClickListener) {
                                ((com.badlogic.gdx.scenes.scene2d.utils.ClickListener) listener).clicked(new InputEvent(), 0, 0);
                            }
                        }
                        return true;
                    }
                }
                boolean irParaFrente = (keycode == Input.Keys.TAB && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    || keycode == Input.Keys.RIGHT
                    || keycode == Input.Keys.DOWN;
                boolean irParaTras = (keycode == Input.Keys.TAB && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    || keycode == Input.Keys.LEFT
                    || keycode == Input.Keys.UP;
                if (irParaFrente || irParaTras) {
                    int indiceAtual = -1;
                    for (int i = 0; i < atoresFocaveis.length; i++) {
                        if (atoresFocaveis[i] == focoAtual) {
                            indiceAtual = i;
                            break;
                        }
                    }
                    int proximoIndice;
                    if (irParaFrente) {
                        proximoIndice = (indiceAtual + 1) % atoresFocaveis.length;
                    } else {
                        proximoIndice = (indiceAtual - 1);
                        if (proximoIndice < 0) proximoIndice = atoresFocaveis.length - 1;
                    }
                    palco.setKeyboardFocus(atoresFocaveis[proximoIndice]);
                    return true;
                }
                return false;
            }
        });
    }

    public static Color getCorFundoCaixaRanking() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.BLACK;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("050A14"); // O azul que definimos
            default: return Color.valueOf("2B0505"); // O vermelho original
        }
    }

    public static Color getCorBordaLinhaRanking() {
        switch (modoVisaoAtual) {
            case ALTO_CONTRASTE: return Color.WHITE;
            case PROTANOPIA_DEUTERANOPIA: return Color.valueOf("0F1C33");
            default: return Color.valueOf("350A0A");
        }
    }
}
