package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;

public class ControladorRegistro {

    private UsuarioDao usuarioDao;

    public ControladorRegistro(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean registrarUsuario(Usuario u) {
        String email = u.getEmail().toLowerCase();

        if (email.endsWith("@aluno.cps.sp.gov.br")) {
            u.setRole("ALUNO");
        } else if (email.endsWith("@cps.sp.gov.br")) {
            u.setRole("PROFESSOR");
        } else {
            System.out.println("Erro: O cadastro exige um e-mail institucional da ETEC/CPS");
            return false;
        }

        return usuarioDao.registrarUsuario(u);
    }
}
