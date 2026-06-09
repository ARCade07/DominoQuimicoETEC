package com.domino.atores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.domino.logica.Peca;

// Herdar de Group (e não mais de Image) permite empilhar múltiplos atores (Fundo + Textos)
public class PecaVisual extends Group {

    // Guarda a referência da lógica purista para ser consultada pelo GameScreen depois
    private final Peca pecaLogica;

    public PecaVisual(Peca peca, Texture texturaBase, BitmapFont fonte) {
        this.pecaLogica = peca;

        // 1. Cria a base branca do dominó e adiciona ao Grupo
        Image fundo = new Image(texturaBase);
        this.addActor(fundo);

        // 2. O tamanho do Grupo (área clicável) deve ser exatamente o tamanho da imagem de fundo
        this.setSize(fundo.getWidth(), fundo.getHeight());

        // Define o centro de rotação no meio da peça (importante para quando a peça deitar no tabuleiro)
        this.setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);

        // 3. Define a cor da fonte (preto para contrastar com o fundo branco)
        Label.LabelStyle estiloTexto = new Label.LabelStyle(fonte, Color.BLACK);

        // 4. Cria os textos
        Label textoCima = new Label(peca.getInfo1(), estiloTexto);
        Label textoBaixo = new Label(peca.getInfo2(), estiloTexto);

        // --- INÍCIO DO FIX DE TAMANHO ---

        // Define a escala (se 0.85f ainda ficar grande, reduza para 0.7f, etc.)
        float escala = 0.85f;
        textoCima.setFontScale(escala);
        textoBaixo.setFontScale(escala);

        // Habilita a quebra de linha automática para strings muito compridas
        textoCima.setWrap(true);
        textoBaixo.setWrap(true);

        // Cria uma margem de segurança de 10 pixels (5px de cada lado)
        // Isso força o texto a ficar contido em 90px de largura
        float larguraUtil = this.getWidth() - 10f;

        // 5. Posiciona e centraliza o texto de CIMA
        textoCima.setWidth(larguraUtil);
        textoCima.setX(5f); // Empurra 5px para a direita para centralizar
        textoCima.setAlignment(Align.center);
        // O - (texto.getHeight() / 2f) garante que o centro do texto fique exatamente em 75% da peça
        textoCima.setY((this.getHeight() * 0.75f) - (textoCima.getHeight() / 2f));

        // 6. Posiciona e centraliza o texto de BAIXO
        textoBaixo.setWidth(larguraUtil);
        textoBaixo.setX(5f);
        textoBaixo.setAlignment(Align.center);
        textoBaixo.setY((this.getHeight() * 0.25f) - (textoBaixo.getHeight() / 2f));

        // 7. Adiciona os textos na hierarquia (como foram adicionados por último, ficam por cima)
        this.addActor(textoCima);
        this.addActor(textoBaixo);
    }

    // Método essencial para o DragAndDrop e para validações do tabuleiro
    public Peca getPecaLogica() {
        return this.pecaLogica;
    }
}
