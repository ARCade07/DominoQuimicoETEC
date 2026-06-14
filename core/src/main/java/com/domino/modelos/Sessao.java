package com.domino.modelos;

public class Sessao {

    private static Usuario usuarioAtual;

    public static void setUsuario(Usuario usuario) {
        usuarioAtual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioAtual;
    }

    public static void limparSessao() {
        usuarioAtual = null;
    }
}
