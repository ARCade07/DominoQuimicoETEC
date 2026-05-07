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
            System.out.println(String.format("Peça colocada: [ %s | %s ]", peca.getInfo1(), peca.getInfo2()));
            System.out.println(this.getPecasNoTabuleiro());

            return true;
        }
        return false;
    }

    private boolean validarPeca(Peca peca, boolean noFinal){
        if (pecasNoTabuleiro.isEmpty()){
            if (!this.pecasNoTabuleiroVerificacao.add(peca)) return false;
            this.pecasNoTabuleiro.add(peca);

            if (!peca.isBucha()) peca.setRotacao(90);

            return true;
        }
        boolean primeiraJogada = false;
        if (this.primeiraJogada()) primeiraJogada = true;

        List<Tipo> tiposCompativeis = new ArrayList<>();
        if (noFinal){
            // Informações da última peça do tabuleiro
            Peca ultimaPeca =  pecasNoTabuleiro.getLast();
            Tipo tipoCompativelLado1 = ultimaPeca.getConexoes1();
            Tipo tipoCompativelLado2 = ultimaPeca.getConexoes2();

            if (!ultimaPeca.isLado1Ocupado()){
                tiposCompativeis.add(tipoCompativelLado1);
            }
            if (!ultimaPeca.isLado2Ocupado()){
                tiposCompativeis.add(tipoCompativelLado2);
            }

            if (primeiraJogada){
                // Só olha pro lado direito (final = true)
                tiposCompativeis.clear();
                tiposCompativeis.add(tipoCompativelLado2);
            }

            // Informações da peça que vai ser colocada no tabuleiro
            Tipo tipo1Peca = peca.getTipo1();
            Tipo tipo2Peca = peca.getTipo2();

            //if (tiposCompativeis.contains(tipo1Peca) && tiposCompativeis.contains(tipo2Peca)){}
            if (tiposCompativeis.contains(tipo1Peca)){
                // Peça é compatível, mas não sabemos o lado que está disponível

                if (!this.pecasNoTabuleiroVerificacao.add(peca)){
                    System.out.println("Erro: Peça repetida.");
                    return false;
                }

                // Ocupa os lados da peça
                peca.setLado1Ocupado(true);
                if (tipoCompativelLado1.equals(tipo1Peca)) ultimaPeca.setLado1Ocupado(true);
                else ultimaPeca.setLado2Ocupado(true);

                pecasNoTabuleiro.addLast(peca);

                // Girar a peça para encaixar visualmente
                if(!peca.isBucha()) peca.setRotacao(90);

                return true;
            }
            else if (tiposCompativeis.contains(tipo2Peca)){
                // Peça é compatível, mas não sabemos o lado que está disponível

                if (!this.pecasNoTabuleiroVerificacao.add(peca)){
                    System.out.println("Erro: Peça repetida.");
                    return false;
                }

                // Ocupa os lados da peça
                peca.setLado2Ocupado(true);
                if (tipoCompativelLado1.equals(tipo1Peca)) ultimaPeca.setLado1Ocupado(true);
                else ultimaPeca.setLado2Ocupado(true);

                pecasNoTabuleiro.addLast(peca);

                // Girar a peça para encaixar visualmente
                if(!peca.isBucha()) peca.setRotacao(-90);

                return true;
            }
        } else  /* No começo */ {
            // Informações da primeira peça do tabuleiro
            Peca primeiraPeca =  pecasNoTabuleiro.getFirst();
            Tipo tipoCompativelLado1 = primeiraPeca.getConexoes1();
            Tipo tipoCompativelLado2 = primeiraPeca.getConexoes2();

            if (!primeiraPeca.isLado1Ocupado()){
                tiposCompativeis.add(tipoCompativelLado1);
            }
            if (!primeiraPeca.isLado2Ocupado()){
                tiposCompativeis.add(tipoCompativelLado2);
            }

            if (primeiraJogada){
                // Só olha pro lado esquerdo (final = false)
                tiposCompativeis.clear();
                tiposCompativeis.add(tipoCompativelLado1);
            }

            // Informações da peça que vai ser colocada no tabuleiro
            Tipo tipo1Peca = peca.getTipo1();
            Tipo tipo2Peca = peca.getTipo2();

            if (tiposCompativeis.contains(tipo1Peca)){
                // Peça é compatível, mas não sabemos o lado que está disponível

                if (!this.pecasNoTabuleiroVerificacao.add(peca)){
                    System.out.println("Erro: Peça repetida.");
                    return false;
                }

                // Ocupa os lados da peça
                peca.setLado1Ocupado(true);
                if (tipoCompativelLado1.equals(tipo1Peca)) primeiraPeca.setLado1Ocupado(true);
                else primeiraPeca.setLado2Ocupado(true);

                pecasNoTabuleiro.addFirst(peca);

                // Girar a peça para encaixar visualmente
                if(!peca.isBucha()) peca.setRotacao(-90);

                return true;
            }
            else if (tiposCompativeis.contains(tipo2Peca)){
                // Peça é compatível, mas não sabemos o lado que está disponível

                if (!this.pecasNoTabuleiroVerificacao.add(peca)){
                    System.out.println("Erro: Peça repetida.");
                    return false;
                }

                // Ocupa os lados da peça
                peca.setLado2Ocupado(true);
                if (tipoCompativelLado1.equals(tipo1Peca)) primeiraPeca.setLado1Ocupado(true);
                else primeiraPeca.setLado2Ocupado(true);

                pecasNoTabuleiro.addFirst(peca);

                // Girar a peça para encaixar visualmente
                if(!peca.isBucha()) peca.setRotacao(90);

                return true;
            }
        }
        return false;
    }

    public boolean primeiraJogada(){
        return this.pecasNoTabuleiro.size() == 1;
    }
}
