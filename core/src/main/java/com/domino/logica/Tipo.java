package com.domino.logica;

import java.util.List;

public enum Tipo {

    ACIDO("Ácido"){
        @Override
        public List<Tipo> getConexoes(){
            return List.of(Tipo.ACIDO, Tipo.AGUA);
        }
    },
    BASE("Base"){
        @Override
        public List<Tipo> getConexoes(){
            return List.of(Tipo.BASE, Tipo.AGUA);
        }
    },
    OXIDO("Óxido"){
        @Override
        public List<Tipo> getConexoes(){
            return List.of(Tipo.OXIDO, Tipo.AGUA);
        }
    },
    SAL("Sal"){
        @Override
        public List<Tipo> getConexoes(){
            return List.of(Tipo.SAL, Tipo.AGUA);
        }
    },
    AGUA("Água"){
        @Override
        public List<Tipo> getConexoes(){
            return List.of(Tipo.ACIDO, Tipo.BASE, Tipo.OXIDO, Tipo.SAL, Tipo.AGUA);
        }
    };

    private final String nome;

    Tipo(String nome){
        this.nome = nome;
    }

    public static Tipo fromString(String tipo1) {
        return Tipo.valueOf(tipo1.toUpperCase());
    }

    public abstract List<Tipo> getConexoes();
}
