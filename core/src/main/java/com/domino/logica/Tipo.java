package com.domino.logica;

public enum Tipo {

    ACIDO("Ácido"){
        @Override
        public Tipo getConexoes(){
            return Tipo.BASE;
        }
    },
    BASE("Base"){
        @Override
        public Tipo getConexoes(){
            return Tipo.ACIDO;
        }
    };

    private final String nome;

    Tipo(String nome){
        this.nome = nome;
    }

    public String getNome(){
        return this.nome;
    }

    public abstract Tipo getConexoes();
}
