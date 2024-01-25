package com.example.ecoproyect.Model;

public class ActivateCouponItem extends CouponItem{
    int id;
    String codigo;
    int canjeable;
    String nombreUsuario;
    String email;

    public ActivateCouponItem(int id, String codigo, int canjeable, String nombreTienda, String descripcion, int puntos, String nombreUsuario, String email, int descuento, int idcoupon) {
        super(nombreTienda, descripcion, descuento, puntos, idcoupon);
        this.id = id;
        this.codigo = codigo;
        this.canjeable = canjeable;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCanjeable() {
        return canjeable;
    }

    public void setCanjeable(int canjeable) {
        this.canjeable = canjeable;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
