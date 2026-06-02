package com.domino.logica;

import com.domino.atores.PecaVisual;
import com.domino.atores.ZonaDeSoltarPeca;

/* AVISO IMPORTANTE:

    - REFERÊNCIA DAS DIREÇÕES:
1. CIMA = de baixo para cima, no canto direito da tela
2. BAIXO = de cima para baixo, no canto esquerdo da tela
3. NORMAL = indo da esquerda pra direia em linha reta
4. INVERTIDO = indo da direita para a esquerda em linha reta

INSIGHTS:
1. O "lado invertido" da zonaDireita, é o "lado normal" da zonaEsquerda
    Com isso, podemos reutilizar código

 */


public enum Direcao {
    CIMA{
        @Override
        public float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? (pecaSolta.getWidth() / 2f) : 0;
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);
        }

        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvo, float yOriginalAlvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {
            // Atualiza o y da peça
            pecaSolta.setPosition(alvo.getX(), alvo.getY());

        }
    },
    BAIXO{
        @Override
        public float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? (pecaSolta.getWidth() / 2f) : 0;
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);
        }

        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvo, float yOriginalAlvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {

        }
    },
    NORMAL{
        // Da esquerda pra direita
        // Se a peça estiver deitada, perde metade da altura e ganha metade da largura (100 x 200)
        @Override
        public float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? (pecaSolta.getWidth() / 2f) : 0;
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);
        }
        // Referência = alvo direito
        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvoDireita, float yOriginalAlvoDireita, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY){
            // Volta o alvo pro Y original pra calcular o Y da peça
            alvoDireita.setPosition(alvoDireita.getX(), yOriginalAlvoDireita);

            // Move a peça
            pecaSolta.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY);
            // Move a zona
            alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, yOriginalAlvoDireita - (alvoDireita.getHeight() / 3));
        }
    },
    INVERTIDO{
        // Direita pra esquerda, só muda o deslocamentoX em relação ao NORMAL
        @Override
        public float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? (pecaSolta.getWidth() / 2f) : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? -(pecaSolta.getWidth() / 2f) : -(pecaSolta.getHeight() / 4f);
        }

        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvoEsquerda, float yOriginalAlvoEsquerda, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {

        }
    };

    public abstract float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada);
    public abstract float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada);
    public abstract float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada);

    public abstract void calcularCoordenadas(ZonaDeSoltarPeca alvo, float yOriginalAlvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY);
}
