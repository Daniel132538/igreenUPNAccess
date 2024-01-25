package com.example.ecoproyect.Model;

import android.content.SharedPreferences;

import com.example.ecoproyect.R;

public class Weather {
    String nombre;
    String fecha;
    String temperaturaActual;
    String temperaturaMax;
    String temperaturaMin;
    String humedad;
    String viento;
    String lluvia;
    String description;
    Double UMBRAL_PUNTOS_LLUVIA;
    Double UMBRAL_PUNTOS_LLUVIA_Y_BAJA_TEMPERATURA;
    Double UMBRAL_PUNTOS_DESPEJADO;
    Double UMBRAL_PUNTOS_DESPEJADO_Y_BAJA_TEMPERATURA;

    public Weather(String nombre, String fecha, String temperaturaActual, String temperaturaMax, String temperaturaMin, String humedad, String viento, String lluvia, String description, double[] valores) {
        UMBRAL_PUNTOS_LLUVIA = valores[0];
        UMBRAL_PUNTOS_LLUVIA_Y_BAJA_TEMPERATURA = valores[1];
        UMBRAL_PUNTOS_DESPEJADO = valores[2];
        UMBRAL_PUNTOS_DESPEJADO_Y_BAJA_TEMPERATURA = valores[3];

        this.nombre = nombre;
        this.fecha = fecha;
        this.temperaturaActual = temperaturaActual;
        this.temperaturaMax = temperaturaMax;
        this.temperaturaMin = temperaturaMin;
        this.humedad = humedad;
        this.viento = viento;
        this.description = description;
        if (lluvia.equals("")){
            this.lluvia = "0";
        }else {
            this.lluvia = lluvia;
        }
    }

    public int getWeatherPhoto(){
        if (description.equals("")){
            return R.drawable.cloud;
        } else {
            if (description.contains("Despejado")){
                return R.drawable.sun;
            } else if (description.contains("lluvia")){
                //R.drawable.baseline_thunderstorm_24
                //R.drawable.baseline_nightlight_24
                return R.drawable.raining;
            } else if (description.contains("Nubes altas") || description.contains("Poco nuboso")){
                return R.drawable.cloud_sun;
            }
            else {
                return R.drawable.cloud;
            }
        }
    }

    public double getPuntosWeather(){
        if (description.contains("lluvia")){
            if (Integer.valueOf(temperaturaActual) > 8){
                return UMBRAL_PUNTOS_LLUVIA;
            } else {
                return UMBRAL_PUNTOS_LLUVIA_Y_BAJA_TEMPERATURA;
            }
        } else {
            if (Integer.valueOf(temperaturaActual) > 8){
                return UMBRAL_PUNTOS_DESPEJADO;
            } else {
                return UMBRAL_PUNTOS_DESPEJADO_Y_BAJA_TEMPERATURA;
            }
        }
    }

    public void printFeatures(){
        System.out.println("Nombre: " + nombre);
        System.out.println("Fecha: " + fecha);
        System.out.println("Temperatura Actual: " + temperaturaActual);
        System.out.println("Temperatura Máxima: " + temperaturaMax);
        System.out.println("Temperatura Mínima: " + temperaturaMin);
        System.out.println("Humedad: " + humedad);
        System.out.println("Viento: " + viento);
        System.out.println("Lluvia: " + lluvia);
        System.out.println("description: " + description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTemperaturaActual() {
        return temperaturaActual;
    }

    public void setTemperaturaActual(String temperaturaActual) {
        this.temperaturaActual = temperaturaActual;
    }

    public String getTemperaturaMax() {
        return temperaturaMax;
    }

    public void setTemperaturaMax(String temperaturaMax) {
        this.temperaturaMax = temperaturaMax;
    }

    public String getTemperaturaMin() {
        return temperaturaMin;
    }

    public void setTemperaturaMin(String temperaturaMin) {
        this.temperaturaMin = temperaturaMin;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getViento() {
        return viento;
    }

    public void setViento(String viento) {
        this.viento = viento;
    }

    public String getLluvia() {
        return lluvia;
    }

    public void setLluvia(String lluvia) {
        this.lluvia = lluvia;
    }
}
