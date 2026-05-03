package com.domino.modelos;

import org.bson.types.ObjectId;

public class Usuario {

    private ObjectId id;
    private String nome;
    private String email;
    private String senha;
    private String role;
    private Estatisticas estat;

    // Construtor vazio para o objeto ser recriado quando foi lido do bd
    public Usuario(){
        // Criano o sub-documento de estatísticas dentro do doc de Usuário no bd
        this.estat = new Estatisticas();
    }

    // O próprio bd gerá o id
    public Usuario(String nome, String email, String senha, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.estat = new Estatisticas();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Estatisticas getEstat() {
        return estat;
    }

    public void setEstat(Estatisticas estat) {
        this.estat = estat;
    }
}
