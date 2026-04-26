package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class LogCaptura {
    private int id;

    @SerializedName("id_thiltape")
    private int idThiltape;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("lat_usuario")
    private double latUsuario;

    @SerializedName("lon_usuario")
    private double lonUsuario;

    @SerializedName("capturado_em")
    private Date capturadoEm;

    public LogCaptura() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdThiltape() {
        return idThiltape;
    }

    public void setIdThiltape(int idThiltape) {
        this.idThiltape = idThiltape;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getLatUsuario() {
        return latUsuario;
    }

    public void setLatUsuario(double latUsuario) {
        this.latUsuario = latUsuario;
    }

    public double getLonUsuario() {
        return lonUsuario;
    }

    public void setLonUsuario(double lonUsuario) {
        this.lonUsuario = lonUsuario;
    }

    public Date getCapturadoEm() {
        return capturadoEm;
    }

    public void setCapturadoEm(Date capturadoEm) {
        this.capturadoEm = capturadoEm;
    }
}
