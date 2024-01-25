package com.example.ecoproyect.Model;

public class RankingItem {
    private String nombreUsuario;
    private String numeroViajes;

    public RankingItem(String title, String description) {
        this.nombreUsuario = title;
        this.numeroViajes = description;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNumeroViajes() {
        return numeroViajes;
    }
}
