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
            return estaDeitada ? 0 : (pecaSolta.getWidth() / 2f);
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getWidth(): (pecaSolta.getHeight() / 4f);
        }

        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {
            pecaSolta.setPosition(alvo.getX() + deslocamentoX, alvo.getY() + deslocamentoY);

            float alturaVisual = (pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90) ? pecaSolta.getWidth() : pecaSolta.getHeight();
            alvo.setPosition(alvo.getX(), alvo.getY() + alturaVisual);
        }
    },
    BAIXO{
        @Override
        public float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? pecaSolta.getHeight() : pecaSolta.getWidth();
        }
        @Override
        public float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? 0 : (pecaSolta.getWidth() / 2f);
        }
        @Override
        public float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada){
            return estaDeitada ? 0 : -(pecaSolta.getHeight() / 4f);
        }

        @Override
        public void calcularCoordenadas(ZonaDeSoltarPeca alvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {
            float alturaVisual = (pecaSolta.getRotation() == 90 || pecaSolta.getRotation() == -90) ? pecaSolta.getWidth() : pecaSolta.getHeight();

            pecaSolta.setPosition(alvo.getX() + pecaSolta.getWidth() + 20f, alvo.getY() + deslocamentoY);
            alvo.setPosition(alvo.getX(), alvo.getY() - alturaVisual);
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
        public void calcularCoordenadas(ZonaDeSoltarPeca alvoDireita, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY){
            // Move a peça
            pecaSolta.setPosition(alvoDireita.getX() + deslocamentoX, alvoDireita.getY() + deslocamentoY + 100);
            // Move a zona
            alvoDireita.setPosition(alvoDireita.getX() + larguraVisual, alvoDireita.getY());
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
        public void calcularCoordenadas(ZonaDeSoltarPeca alvoEsquerda, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY) {
            pecaSolta.setPosition(alvoEsquerda.getX() + deslocamentoX + 20f, alvoEsquerda.getY() + deslocamentoY + 100);
            alvoEsquerda.setPosition(alvoEsquerda.getX() - larguraVisual, alvoEsquerda.getY());
        }
    };

    public abstract float calcularLarguraVisual(PecaVisual pecaSolta, boolean estaDeitada);
    public abstract float calcularDeslocamentoX(PecaVisual pecaSolta, boolean estaDeitada);
    public abstract float calcularDeslocamentoY(PecaVisual pecaSolta, boolean estaDeitada);

    public abstract void calcularCoordenadas(ZonaDeSoltarPeca alvo, PecaVisual pecaSolta, float larguraVisual, float deslocamentoX, float deslocamentoY);
}
