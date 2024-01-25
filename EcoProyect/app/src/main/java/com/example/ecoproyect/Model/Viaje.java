package com.example.ecoproyect.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class Viaje {
    private Polyline polyline;
    private long duracion;
    private double distancia;
    private String origen;
    private String destino;
    private LatLng LatLngOrigen;
    private LatLng LatLngDestino;
    private String travelMode;
    private boolean viajeCompletado = false;

    public Viaje(String travelMode) {
        this.travelMode = travelMode;
    }

    public boolean isViajeCompletado() {
        return viajeCompletado;
    }

    public void setViajeCompletado(boolean viajeCompletado) {
        this.viajeCompletado = viajeCompletado;
    }

    public LatLng getLatLngOrigen() {
        return LatLngOrigen;
    }

    public void setLatLngOrigen(LatLng latLngOrigen) {
        LatLngOrigen = latLngOrigen;
    }

    public LatLng getLatLngDestino() {
        return LatLngDestino;
    }

    public void setLatLngDestino(LatLng latLngDestino) {
        LatLngDestino = latLngDestino;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public long getDuracion() {
        return duracion;
    }

    public void setDuracion(long duracion) {
        this.duracion = duracion;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }
}
