package com.example.ecoproyect.Model;

import java.io.Serializable;

public class CouponItem implements Serializable {
    private int id;
    private int descuento;
    private String tienda;
    private int imagen;
    private int puntos;
    private String descripcion;

    public CouponItem(String tienda, String descripcion,int descuento,int puntuacionNecesaria, int id) {
        this.id = id;
        this.descuento = descuento;
        this.tienda = tienda;
        this.puntos = puntuacionNecesaria;
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public int getPuntos() {
        return puntos;
    }
    public int getDescuento() {
        return descuento;
    }

    public String getTienda() {
        return tienda;
    }

    public int getImagen() {
        return imagen;
    }

    public int getId() { return id; }
}
