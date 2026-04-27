package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;

public class Thiltape {
    private Integer id;

    @SerializedName("id_jogo")
    private Integer idJogo;

    private String nome;

    @SerializedName("imagem_url")
    private String imagemUrl;

    // Campos separados para representar o Geometry(Point)
    private double latitude;
    private double longitude;

    private String status;

    private boolean capturado;

    @SerializedName("criado_em")
    private String criadoEm;

    public Thiltape() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdJogo() {
        return idJogo;
    }

    public void setIdJogo(Integer idJogo) {
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

    public boolean isCapturado() {
        return capturado;
    }

    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }
}
