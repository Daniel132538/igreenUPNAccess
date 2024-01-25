package com.example.ecoproyect.Model;

public class Statistics {
    String viajesAndando;
    String viajesBici;
    String viajesBiciEléctrica;
    String viajesPatineteEléctrico;
    String viajesBus;
    String viajesTotales;

    public Statistics(String viajesAndando, String viajesBici, String viajesBiciEléctrica, String viajesPatineteEléctrico, String viajesBus, String viajesTotales) {
        this.viajesAndando = viajesAndando;
        this.viajesBici = viajesBici;
        this.viajesBiciEléctrica = viajesBiciEléctrica;
        this.viajesPatineteEléctrico = viajesPatineteEléctrico;
        this.viajesBus = viajesBus;
        this.viajesTotales = viajesTotales;
    }

    public String getViajesAndando() {
        return viajesAndando;
    }

    public void setViajesAndando(String viajesAndando) {
        this.viajesAndando = viajesAndando;
    }

    public String getViajesBici() {
        return viajesBici;
    }

    public void setViajesBici(String viajesBici) {
        this.viajesBici = viajesBici;
    }

    public String getViajesBiciEléctrica() {
        return viajesBiciEléctrica;
    }

    public void setViajesBiciEléctrica(String viajesBiciEléctrica) {
        this.viajesBiciEléctrica = viajesBiciEléctrica;
    }

    public String getViajesPatineteEléctrico() {
        return viajesPatineteEléctrico;
    }

    public void setViajesPatineteEléctrico(String viajesPatineteEléctrico) {
        this.viajesPatineteEléctrico = viajesPatineteEléctrico;
    }

    public String getViajesBus() {
        return viajesBus;
    }

    public void setViajesBus(String viajesBus) {
        this.viajesBus = viajesBus;
    }

    public String getViajesTotales() {
        return viajesTotales;
    }

    public void setViajesTotales(String viajesTotales) {
        this.viajesTotales = viajesTotales;
    }
}
