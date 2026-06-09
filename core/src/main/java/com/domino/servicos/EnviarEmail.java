package com.domino.servicos;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EnviarEmail {

    private final String usuarioEmail;
    private final String senhaEmail;
    // mantém a conexao configurada com o servidor do gmail
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
    public boolean emailBase(String destinario, String assunto, String corpoEmail) {
        try {
            Message mensagem = new MimeMessage(this.sessao);
            mensagem.setFrom(new InternetAddress(usuarioEmail));
            mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinario));

            mensagem.setSubject(assunto);
            mensagem.setContent(corpoEmail, "text/html; charset=utf-8");

            Transport.send(mensagem);
            return true;

        } catch (MessagingException e) {
            System.err.println("Falhar ao enviar e-mail para " + destinario + ". Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void emailRecuperacao(String destinatario, String tokenRecuperacao) {
        String assunto = "Recuperação de Senha - CHEMDOM";
        String corpoEmail = "<h3>Recuperação de Senha</h3>"
            + "<p>Você solicitou a redefinição de senha.</p>"
            + "<p>Seu código de verificação é: <strong>" + tokenRecuperacao + "</strong></p>"
            + "<p>Este código expira em 15 minutos.</p>"
            + "<p>Caso não tenha solicitado, ignore essa mensagem</p>";

            boolean enviado = emailBase(destinatario, assunto, corpoEmail);

            if (enviado) System.out.println("E-mail enviado com sucesso para: " + destinatario);
    }
}
