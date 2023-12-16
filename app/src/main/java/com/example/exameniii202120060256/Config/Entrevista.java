package com.example.exameniii202120060256.Config;

import java.util.Date;

public class Entrevista {
    private String ipOrden;
    private String descripcion;
    private String periodista;
    private String fecha;
    private String imagen;
    private String audio;

    // Constructor vacío requerido para Firebase
    public Entrevista() {
    }

    public Entrevista(String ipOrden, String descripcion, String periodista, String fecha, String imagen, String audio) {
        this.ipOrden = ipOrden;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.imagen = imagen;
        this.audio = audio;
    }

    // Métodos getter y setter

    public String getIpOrden() {
        return ipOrden;
    }

    public void setIpOrden(String ipOrden) {
        this.ipOrden = ipOrden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}

