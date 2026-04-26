package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Jogo {
    private int id;
    private String nome;

    @SerializedName("imagem_url")
    private String imagemUrl;

    private String status;

    @SerializedName("data_inicio")
    private Date dataInicio;

    @SerializedName("data_fim")
    private Date dataFim;

    public Jogo() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
}
