package com.domino.logica;

public enum Tipo {

    ACIDO("Ácido"){
        @Override
        public Tipo getConexoes(){
            return Tipo.ACIDO;
        }
    },
    BASE("Base"){
        @Override
        public Tipo getConexoes(){
            return Tipo.BASE;
        }
    },
    OXIDO("Óxido"){
        @Override
        public Tipo getConexoes(){
            return Tipo.OXIDO;
        }
    },
    SAL("Sal"){
        @Override
        public Tipo getConexoes(){
            return Tipo.SAL;
        }
    };

    private final String nome;

    Tipo(String nome){
        this.nome = nome;
    }

    public static Tipo fromString(String tipo1) {
        return Tipo.valueOf(tipo1.toUpperCase());
    }

    public String getNome(){
        return this.nome;
    }

    public abstract Tipo getConexoes();
}
