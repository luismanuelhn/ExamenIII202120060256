package com.example.exameniii202120060256.Config;

public class Entrevista2 {
    private String descripcion;
    private String periodista;
    private String fecha;
    private String imagen;

    // Constructor vacío requerido para Firebase

    public Entrevista2() {
    }

    public Entrevista2(String descripcion, String periodista, String fecha, String imagen) {

        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.imagen = imagen;

    }

    // Métodos getter y setter





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




}

