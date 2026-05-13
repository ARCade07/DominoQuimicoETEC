package com.domino.logica;

public class Peca {
    private Tipo tipo1;
    private Tipo tipo2;
    private boolean lado1Ocupado;
    private String info1;
    private String info2;
    private boolean lado2Ocupado;

    private boolean isBucha;

    private int rotacao;

    public Peca(){}

    public Peca(String info1, Tipo tipo1, String info2, Tipo tipo2) {
        this.info1 = info1;
        this.tipo1 = tipo1;
        this.info2 = info2;
        this.tipo2 = tipo2;

        if (info1.equals(info2)) this.isBucha = true;
    }

    public String getInfo1() {
        return this.info1;
    }
    public String getInfo2() {
        return this.info2;
    }

    public boolean isLado1Ocupado() {
        return lado1Ocupado;
    }
    public void setLado1Ocupado(boolean lado1Ocupado) {this.lado1Ocupado = lado1Ocupado;}

    public boolean isLado2Ocupado() {
        return lado2Ocupado;
    }
    public void setLado2Ocupado(boolean lado2Ocupado) {this.lado2Ocupado = lado2Ocupado;}

    public Tipo getTipo1(){
        return this.tipo1;
    }
    public Tipo getTipo2(){
        return this.tipo2;
    }
    public Tipo getConexoes1(){
        return this.tipo1.getConexoes();
    }
    public Tipo getConexoes2(){
        return this.tipo2.getConexoes();
    }

    public boolean isBucha(){
        return this.isBucha;
    }

    public int getRotacao(){
        return this.rotacao;
    }
    public void setRotacao(int rotacao){
        this.rotacao = rotacao;
    }

    public void setTipo1(Tipo tipo1) {
        this.tipo1 = tipo1;
    }

    public void setTipo2(Tipo tipo2) {
        this.tipo2 = tipo2;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    @Override
    public String toString(){
        return String.format("%s (%s)| %s (%s)", info1, isLado1Ocupado(), info2, isLado2Ocupado());
    }

}
