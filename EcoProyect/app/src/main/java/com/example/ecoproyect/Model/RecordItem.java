package com.example.ecoproyect.Model;

public class RecordItem {
    private int idViaje;
    private int idUser;
    private String duracion;
    private String distancia;
    private String origen;
    private String destino;
    private int puntos;
    private int imageModeTravel;
    private int viajeCompletado;
    private String fecha_creacion;
    private double velocidad_media;

    public RecordItem(int idViaje, int idUser, String duracion, String distancia, String origen,
                      String destino,int puntos, int imageModeTravel, int viajeCompletado, String fecha_creacion, double velocidad_media) {
        this.idViaje = idViaje;
        this.idUser = idUser;
        this.duracion = duracion;
        this.distancia = distancia;
        this.origen = origen;
        this.destino = destino;
        this.imageModeTravel = imageModeTravel;
        this.puntos = puntos;
        this.viajeCompletado = viajeCompletado;
        this.fecha_creacion = fecha_creacion;
        this.velocidad_media = velocidad_media;
    }

    public double getVelocidad_media() {
        return velocidad_media;
    }

    public void setVelocidad_media(double velocidad_media) {
        this.velocidad_media = velocidad_media;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getViajeCompletado() {
        return viajeCompletado;
    }

    public void setViajeCompletado(int viajeCompletado) {
        this.viajeCompletado = viajeCompletado;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getImageModeTravel() {
        return imageModeTravel;
    }
}
