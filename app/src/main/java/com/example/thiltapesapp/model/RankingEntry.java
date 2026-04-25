package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;

public class RankingEntry {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nome_usuario")
    private String nomeUsuario;

    @SerializedName("pontuacao")
    private int pontuacao;

    // Construtor vazio (obrigatório para GSON)
    public RankingEntry() {}

    // Getters e Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public int getPontuacao() { return pontuacao; }
    public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }
}