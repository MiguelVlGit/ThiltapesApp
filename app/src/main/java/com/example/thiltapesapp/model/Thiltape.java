package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Thiltape {
    private int id;

    @SerializedName("id_jogo")
    private int idJogo;

    private String nome;

    @SerializedName("imagem_url")
    private String imagemUrl;

    // Campos separados para representar o Geometry(Point)
    private double latitude;
    private double longitude;

    private String status;

    @SerializedName("criado_em")
    private Date criadoEm;

    public Thiltape() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdJogo() {
        return idJogo;
    }

    public void setIdJogo(int idJogo) {
        this.idJogo = idJogo;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}