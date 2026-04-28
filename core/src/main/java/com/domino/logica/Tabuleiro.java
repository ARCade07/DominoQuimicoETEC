package com.domino.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Tabuleiro {
    //Perdemos memória para ganhar desempenho ao utilizar HashSet para validar duplicatas e ArrayList para controle de peças
    private final Set pecasNoTabuleiroVerificacao = new HashSet();
    private final List<Peca> pecasNoTabuleiro = new ArrayList<>();

    public List<Peca> getPecasNoTabuleiro() {
        return pecasNoTabuleiro;
    }

    public boolean colocarPeca(Peca peca, boolean noFinal){
        if (validarPeca(peca, noFinal)){
            if (!pecasNoTabuleiroVerificacao.add(peca)){
                System.out.println("Essa mensagem nunca deve aparecer na sua tela. Se isso acontecer, largue tudo e fuja para as montanhas.");
                return false;
            }
            if (noFinal) pecasNoTabuleiro.addLast(peca);
            else pecasNoTabuleiro.addFirst(peca);
            System.out.println(String.format("Peça colocada: [ %s | %s ]", peca.getInfo1(), peca.getInfo2()));
            System.out.println(this.getPecasNoTabuleiro());

            return true;
        }

        return false;
    }

    private boolean validarPeca(Peca peca, boolean noFinal){
        if (pecasNoTabuleiro.isEmpty()){
            return true;
        }
        List<Tipo> tiposCompativeis = new ArrayList<>();
        if (noFinal){
            // Informações da última peça do tabuleiro
            Peca ultimaPeca =  pecasNoTabuleiro.getLast();

            if (!ultimaPeca.isLado1Ocupado()){
                Tipo tipoCompativelLado1 = ultimaPeca.getConexoes1();
                tiposCompativeis.add(tipoCompativelLado1);
            }
            if (!ultimaPeca.isLado2Ocupado()){
                Tipo tipoCompativelLado2 = ultimaPeca.getConexoes2();
                tiposCompativeis.add(tipoCompativelLado2);
            }

            // Informações da peça que vai ser colocada no tabuleiro
            Tipo tipo1Peca = peca.getTipo1();
            Tipo tipo2Peca = peca.getTipo2();

            if (tiposCompativeis.contains(tipo1Peca) && tiposCompativeis.contains(tipo2Peca)){
                // Peça encaixa em qualquer lado
                // TODO
            }
            else if (tiposCompativeis.contains(tipo1Peca)){
                // Lado 1 está disponível
                // (false) [ 2 | 1 ] (true)
                ultimaPeca.setLado1Ocupado(true);
            }
            else if (tiposCompativeis.contains(tipo2Peca)){
                // Lado 2 está disponível
                // (false) [ 1 | 2 ] (true)
                ultimaPeca.setLado2Ocupado(true);
                //TODO
            }

            if (!pecasNoTabuleiro.getLast().isLado2Ocupado()) {
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
        var primeiraPeca =  pecasNoTabuleiro.getFirst();
        if (!pecasNoTabuleiro.getFirst().isLado1Ocupado()) {
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
