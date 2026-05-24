package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;

public class ControladorRegistro {

    private UsuarioDao usuarioDao;

    public ControladorRegistro(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean registrarUsuario(Usuario u) {
        String email = u.getEmail();

        if (!email.endsWith("@etec.sp.gov.br")) {
            System.out.println("Ero: O cadastro exige um e-mail institucional da ETEC/CPS");
            return false;
        }

        return usuarioDao.registrarUsuario(u);
    }
}
