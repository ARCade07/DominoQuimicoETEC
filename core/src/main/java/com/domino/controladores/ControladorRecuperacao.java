package com.domino.controladores;

import com.domino.dao.UsuarioDao;
import com.domino.servicos.EnviarEmail;

import java.util.Random;

public class ControladorRecuperacao {

    private UsuarioDao u;
    private EnviarEmail e;

    public ControladorRecuperacao(UsuarioDao u) {
        this.u = u;
        this.e = new EnviarEmail();
    }
}
