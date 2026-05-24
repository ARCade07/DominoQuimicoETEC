package com.domino.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.domino.bd.ConnectionFactory;
import com.domino.modelos.Estatisticas;
import com.domino.modelos.Usuario;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UsuarioDao {
    // Onde os documentos BSON serão guardados:
    private final MongoCollection<Document> docsUsuarios;

    // Realiza conexão e seleciona a coleção
    public UsuarioDao(ConnectionFactory connection) {

        this.docsUsuarios = connection.getDatabase().getCollection("usuarios");

        // Criação de um índice único email (para não haver repetições)
        IndexOptions opcoes = new IndexOptions().unique(true);
        this.docsUsuarios.createIndex(Indexes.ascending("email"), opcoes);
    }

    public boolean registrarUsuario(Usuario usuario) {
        // Hash para proteger a senha antes de salvar no banco de dados
        String hashSenha = BCrypt.withDefaults().hashToString(12, usuario.getSenha().toCharArray());

        // Cria o formato documentos (em BSON) para as estatísticas
        Document docEstatisticas = new Document("partidasJogadas", 0)
            .append("partidasGanhas", 0)
            .append("partidasPerdidas", 0)
            .append("erros", 0)
            .append("acertos", 0);

        // Documento para o Usuário
        Document doc = new Document("nome", usuario.getNome())
            .append("email", usuario.getEmail())
            .append("senha", hashSenha)
            .append("role", usuario.getRole())
            .append("estatisticas", docEstatisticas);

        try {
            // Salvo o documento inteiro no banco
            docsUsuarios.insertOne(doc);
            // Geração do ID e atribuição ao usuário (objeto java)
            usuario.setId(doc.getObjectId("_id"));
            usuario.setSenha(hashSenha);
            // Cadastro ocorreu sem problemas
            return true;

        } catch (MongoWriteException e){
            if (e.getError().getCode() == 11000) {
                System.out.println("O e-mail " + usuario.getEmail() + " já está em uso.");
                return false;
            }
            throw e;
        }
    }

    public Usuario realizarLogin(String email, String senhaDigitada) {

        Usuario usuario = buscarPorEmail(email);

        if (usuario != null) {
            BCrypt.Result senhaVerificada = BCrypt.verifyer().verify(senhaDigitada.toCharArray(), usuario.getSenha());

            if(senhaVerificada.verified){
                // Se a senha estiver correta gera um Token único
                String novoToken = UUID.randomUUID().toString();

                // Token antigo é substituido pelo novo
                docsUsuarios.updateOne(
                    Filters.eq("_id", usuario.getId()),
                    Updates.set("tokenSessao", novoToken)
                );

                usuario.setTokenSessao(novoToken);

                return usuario;
            }
        }
        // email não existe ou a senha não bate com a do banco
        return null;
    }

    public boolean redefinirSenha(String email, String novaSenhaDigitada) {
        String hashSenha = BCrypt.withDefaults().hashToString(12, novaSenhaDigitada.toCharArray());

        var atualizacoes = Updates.combine(
            Updates.set("senha", hashSenha),
            Updates.unset("tokenRecuperacao"),
            Updates.unset("tokenExpiracao")
        );

        UpdateResult resultado = docsUsuarios.updateOne(Filters.eq("email", email), atualizacoes);
        
        return resultado.getModifiedCount() > 0;
    }

    public Usuario buscarPorEmail(String email) {
        Document doc = docsUsuarios.find(Filters.eq("email", email)).first();

        if (doc == null){
            return null;
        }

        return converterDocumentoParaUsuario(doc);
    }

    public boolean salvarTokenRecuperacao(String email, String tokenRecuperacao){
        long tempo = TimeUnit.MINUTES.toMillis(15);
        long expiracao = System.currentTimeMillis() + tempo;

        var atualizacoes = Updates.combine(
            Updates.set("tokenRecuperacao", tokenRecuperacao),
            Updates.set("tokenExpiracao", expiracao)
        );

        UpdateResult resultado = docsUsuarios.updateOne(Filters.eq("email", email), atualizacoes);

        return resultado.getMatchedCount() > 0;
    }

    public boolean validarToken(String email, String tokenDigitado) {
        Usuario u = buscarPorEmail(email);

        if (u != null && tokenDigitado.equals(u.getTokenRecuperacao())) {
            return System.currentTimeMillis() <= u.getTokenExpiracao();
        }
        return false;
    }

    public void registrarPartida(ObjectId idUsuario, boolean ganhou, int quantAcertos, int quantErros) {
        var atualizacoes = Updates.combine(
            Updates.inc("estatisticas.partidasJogadas", 1),
            Updates.inc("estatisticas.acertos", quantAcertos),
            Updates.inc("estatisticas.erros", quantErros)
        );

        if (ganhou) {
            atualizacoes = Updates.combine(atualizacoes, Updates.inc("estatisticas.partidasGanhas", 1));
        }
        else {
            atualizacoes = Updates.combine(atualizacoes, Updates.inc("estatisticas.partidasPerdidas", 1));
        }

        docsUsuarios.updateOne(Filters.eq("_id", idUsuario), atualizacoes);
    }

    // metodo para converter BSON (Documento) para Objeto java
    private Usuario converterDocumentoParaUsuario(Document doc) {
        Usuario u = new Usuario();

        // Pega os campos do bd
        u.setId(doc.getObjectId("_id"));
        u.setNome(doc.getString("nome"));
        u.setEmail(doc.getString("email"));
        u.setSenha(doc.getString("senha"));
        u.setTokenSessao(doc.getString("tokenSessao"));

        // Pega o sub-documento de estatísticas do bd
        Document docEstat = (Document) doc.get("estatisticas");

        if(docEstat != null){
            Estatisticas estat = new Estatisticas();
            // Pega atributos de estat. O valor padrao é 0 (caso não tenha outro valor).
            estat.setPartidasJogadas(docEstat.getInteger("partidasJogadas", 0));
            estat.setPartidasGanhas(docEstat.getInteger("partidasGanhas", 0));
            estat.setPartidasPerdidas(docEstat.getInteger("partidasPerdidas", 0));
            estat.setErros(docEstat.getInteger("erros", 0));
            estat.setAcertos(docEstat.getInteger("acertos", 0));

            u.setEstat(estat);
        }
        return u;
    }
}
