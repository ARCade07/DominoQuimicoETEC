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

    public boolean enviarCodigo(String email) {

        String codigoGerado = String.format("%06d", new Random().nextInt(1000000));
        boolean sucesso = u.salvarTokenRecuperacao(email, codigoGerado);

        if (sucesso) {
            e.emailRecuperacao(email, codigoGerado);
            return true;
        }

        return false;
    }

    public boolean confirmarNovaSenha(String email, String novaSenha, String confirmacaoSenha) {
        if (novaSenha.equals(confirmacaoSenha)) {
            return u.redefinirSenha(email, novaSenha);
        }
        return false;
    }
}
