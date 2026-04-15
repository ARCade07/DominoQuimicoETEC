package com.domino.logica;

public class TesteLogica {

    public static void main(String[] args) {
        Tabuleiro tabuleiro = new Tabuleiro();

        Peca peca1 = new Peca("A11", Tipo.ACIDO, "A12", Tipo.ACIDO);
        Peca peca2 = new Peca("A21", Tipo.ACIDO, "B22", Tipo.BASE);
        Peca peca3 = new Peca("A31", Tipo.ACIDO, "B32", Tipo.BASE);
        Peca peca4 = new Peca("B41", Tipo.BASE, "A42", Tipo.ACIDO);
        Peca peca5 = new Peca("B51", Tipo.BASE, "B52", Tipo.BASE);

        tabuleiro.colocarPeca(peca1, true);
        tabuleiro.colocarPeca(peca2, true);
        tabuleiro.colocarPeca(peca3, false);
        tabuleiro.colocarPeca(peca4, false);
        tabuleiro.colocarPeca(peca5, false);


        System.out.println(tabuleiro.getPecasNoTabuleiro());

    }
}
