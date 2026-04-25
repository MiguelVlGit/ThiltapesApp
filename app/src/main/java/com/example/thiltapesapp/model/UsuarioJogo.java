package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class UsuarioJogo {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("id_jogo")
    private int idJogo;

    private int pontuacao;

    @SerializedName("entrou_em")
    private Date entrouEm;

    public UsuarioJogo() {}

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdJogo() {
        return idJogo;
    }

    public void setIdJogo(int idJogo) {
        this.idJogo = idJogo;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Date getEntrouEm() {
        return entrouEm;
    }

    public void setEntrouEm(Date entrouEm) {
        this.entrouEm = entrouEm;
    }
}