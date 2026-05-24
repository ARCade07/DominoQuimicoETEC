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
        configurarSessao();
    }


    private void configurarSessao() {
        Properties propriedades = new Properties();
        propriedades.put("mail.smtp.auth", "true");
        propriedades.put("mail.smtp.starttls.enable", "true");
        propriedades.put("mail.smtp.host", "smtp.gmail.com");
        propriedades.put("mail.smtp.port", "587");

        this.sessao = Session.getInstance(propriedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuarioEmail, senhaEmail);
            }
        });
    }
    }
}
