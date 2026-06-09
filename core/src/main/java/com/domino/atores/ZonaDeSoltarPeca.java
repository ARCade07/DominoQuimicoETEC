package com.domino.atores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.domino.logica.Direcao;

public class ZonaDeSoltarPeca extends Image {
    private final boolean isLadoDireito;

    public Direcao direcao;

    public ZonaDeSoltarPeca(boolean isLadoDireito) {
        this.isLadoDireito = isLadoDireito;
        this.setSize(220, 320); // Um pouco maior que a peça
        if (isLadoDireito) this.direcao = Direcao.NORMAL;
        else this.direcao = Direcao.INVERTIDO;
    }

    public boolean isLadoDireito() {
        return isLadoDireito;
    }

    /**
     * Gera uma textura de 1x1 pixel na memória e a pinta de verde translúcido.
     * Isso evita a necessidade de criar um arquivo .png só para a zona de drop.
     */
    public static Texture criarTextura() {
        // Cria um mapa de pixels vazio de tamanho 1x1 suportando canal Alpha (transparência)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        // Define a cor: Verde (R=0, G=1, B=0) com 30% de opacidade (A=0.3f)
        pixmap.setColor(new Color(0f, 1f, 0f, 0.3f));
        pixmap.fill(); // Preenche o pixmap com a cor

        // Converte o pixmap para uma Textura utilizável pelo LibGDX
        Texture texturaGerada = new Texture(pixmap);

        // Limpa o pixmap da memória RAM, pois ele já foi transferido para a placa de vídeo
        pixmap.dispose();

        return texturaGerada;
    }
}
