package com.domino.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Tabuleiro {
    //Perdemos memória para ganhar desempenho ao utilizar HashSet para validar duplicatas e ArrayList para controle de peças?
    private Set pecasNoTabuleiro = new HashSet();
    private List<Peca> pecasNoTabuleir = new ArrayList<>();

    public List<Peca> getPecasNoTabuleiro() {
        return pecasNoTabuleir;
    }

    public boolean colocarPeca(Peca peca, boolean noFinal){
        if (!pecasNoTabuleiro.add(peca)){
            System.out.println("Essa mensagem nunca deve aparecer na sua tela. Se isso acontecer, largue tudo e fuja para as montanhas.");
            return false;
        }

        if (validarPeca(peca, noFinal)){
            if (noFinal) pecasNoTabuleir.addLast(peca);
            else pecasNoTabuleir.addFirst(peca);
            return true;
        }

        return false;
    }

    private boolean validarPeca(Peca peca, boolean noFinal){
        if (pecasNoTabuleir.isEmpty()){
            return true;
        }
        if (noFinal){
            var ultimaPeca =  pecasNoTabuleir.getLast();
            if (!pecasNoTabuleir.getLast().isLado2Ocupado()) {
                var tipoCompativel = ultimaPeca.getConexoes2();

                if (peca.getTipo1() == tipoCompativel) {
                    ultimaPeca.setLado2Ocupado(true);
                    peca.setLado1Ocupado(true);
                    return true;
                }
                if (peca.getTipo2() == tipoCompativel) {
                    ultimaPeca.setLado2Ocupado(true);
                    peca.setLado2Ocupado(true);
                    return true;
                }
            }
        }
        var primeiraPeca =  pecasNoTabuleir.getFirst();
        if (!pecasNoTabuleir.getFirst().isLado1Ocupado()) {
            var tipoCompativel = primeiraPeca.getConexoes1();

            if (peca.getTipo1() == tipoCompativel) {
                primeiraPeca.setLado1Ocupado(true);
                peca.setLado1Ocupado(true);
                return true;
            }
            if (peca.getTipo2() == tipoCompativel) {
                primeiraPeca.setLado1Ocupado(true);
                peca.setLado2Ocupado(true);
                return true;
            }
        }
        return false;
    }
}
