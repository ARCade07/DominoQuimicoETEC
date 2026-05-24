package com.domino.servicos;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EnviarEmail {

    private final String usuarioEmail;
    private final String senhaEmail;
    private Session sessao;

    public EnviarEmail(){
        Dotenv dotenv = Dotenv.load();
        this.usuarioEmail = dotenv.get("SMTP_EMAIL");
        this.senhaEmail = dotenv.get("SMTP_SENHA");
    }
}
