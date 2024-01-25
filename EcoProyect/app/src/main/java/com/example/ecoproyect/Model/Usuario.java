package com.example.ecoproyect.Model;

public class Usuario {
    private int identification;
    private String name;
    private String email;
    private String prefixNumber;
    private String phoneNumber;
    private String puntuacion;
    private String password;
    private String fecha_creacion;

    public Usuario(int identification, String name, String email, String prefixNumber,
                   String phoneNumber, String puntuacion, String password, String fecha_creacion) {
        this.identification = identification;
        this.name = name;
        this.email = email;
        this.prefixNumber = prefixNumber;
        this.phoneNumber = phoneNumber;
        this.puntuacion = puntuacion;
        this.password = password;
        this.fecha_creacion = fecha_creacion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getIdentification() {
        return identification;
    }

    public void setIdentification(int identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrefixNumber() {
        return prefixNumber;
    }

    public void setPrefixNumber(String prefixNumber) {
        this.prefixNumber = prefixNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
