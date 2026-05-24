package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;

public class ControladorLogin {
    private UsuarioDao u;

    private Usuario usuarioLogado;

    public ControladorLogin(UsuarioDao usuarioDao) {
        this.u = usuarioDao;
    }
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }
}
