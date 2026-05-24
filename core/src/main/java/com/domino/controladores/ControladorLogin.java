package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;

public class ControladorLogin {
    private UsuarioDao u;

    private Usuario usuarioLogado;

    public ControladorLogin(UsuarioDao usuarioDao) {
        this.u = usuarioDao;
    }

    public boolean fazerLogin(String email, String senha) {
        Usuario usuario = u.realizarLogin(email, senha);

        if (usuario != null) {
            this.usuarioLogado = usuario;
            return true;
        }

        return false;
    }
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }
}
