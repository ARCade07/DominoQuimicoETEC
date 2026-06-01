package com.domino.logica;

public enum Tipo {

    ACIDO("Ácido") {
        @Override
        public Tipo getConexoes() {
            return Tipo.ACIDO;
        }
    },
    BASE("Base") {
        @Override
        public Tipo getConexoes() {
            return Tipo.BASE;
        }
    },
    OXIDO("Óxido") {
        @Override
        public Tipo getConexoes() {
            return Tipo.OXIDO;
        }
    },
    SAL("Sal") {
        @Override
        public Tipo getConexoes() {
            return Tipo.SAL;
        }
    };

    private final String nome;

    Tipo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public abstract Tipo getConexoes();

    /**
     * Converte a String vinda do MongoDB para o Enum oficial correspondente.
     * Busca correspondência tanto pelo nome da constante quanto pelo nome amigável.
     */
    public static Tipo fromString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo vindo do banco de dados está nulo ou vazio.");
        }

        String textoLimpo = texto.trim();

        for (Tipo tipo : Tipo.values()) {
            // Verifica se o banco enviou o identificador exato (ex: "ACIDO")
            // ou a string amigável com acento (ex: "Ácido"), ignorando maiúsculas/minúsculas.
            if (tipo.name().equalsIgnoreCase(textoLimpo) || tipo.getNome().equalsIgnoreCase(textoLimpo)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo químico desconhecido encontrado no banco: " + texto);
    }
}
