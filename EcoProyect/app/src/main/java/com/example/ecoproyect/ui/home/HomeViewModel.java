package com.example.ecoproyect.ui.home;

import android.util.Log;
import android.widget.Chronometer;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeViewModel extends ViewModel {
    
    private boolean estadoViaje;
    private String tipoViaje;
    private long elapsedRealtime;
    private Chronometer chronometer;

    /*private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }*/

    public Chronometer getChronometer() {
        return chronometer;
    }

    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    public boolean isEstadoViaje() {
        return estadoViaje;
    }

    public void setEstadoViaje(boolean estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public String getTipoViaje() {
        return tipoViaje;
    }

    public void setTipoViaje(String tipoViaje) {
        this.tipoViaje = tipoViaje;
    }

    public List<LatLng> decodePoly(String encoded){
        List <LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        //Log.d("Polylines", "Encoded Polyline: " + encoded);

        while (index < len){
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index);
                b = b - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
                index++;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~ (result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index) - 63;
                result |= (b & 0x1f) << shift;
                shift +=5;
                index++;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double)lat / 1E5)), (((double) lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public long extraerMinutos(String duracion){
        long totalMinutos = 0;

        Log.d("totalMinutosAntes", duracion);

        // Usar expresiones regulares para buscar diferentes unidades de tiempo
        Pattern pattern = Pattern.compile("(\\d+)\\s*días");
        Matcher matcher = pattern.matcher(duracion);

        while (matcher.find()) {
            long dias = Integer.parseInt(matcher.group(1));
            totalMinutos += dias * 24 * 60; // Convertir días a minutos
        }

        pattern = Pattern.compile("(\\d+)\\s*horas");
        matcher = pattern.matcher(duracion);

        while (matcher.find()) {
            long horas = Integer.parseInt(matcher.group(1));
            totalMinutos += horas * 60; // Convertir horas a minutos
        }

        pattern = Pattern.compile("(\\d+)\\s*min");
        matcher = pattern.matcher(duracion);

        while (matcher.find()) {
            long minutos = Integer.parseInt(matcher.group(1));
            totalMinutos += minutos; // Sumar los minutos directamente
        }

        Log.d("Total minutos despues", String.valueOf(totalMinutos));

        return totalMinutos;
    }

    public double extraerKM(String distancia) {
        Log.d("totaldistanciaAntes", distancia);
        // Usar una expresión regular para buscar el número decimal con o sin decimales seguido de "km"
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*km");
        Matcher matcher = pattern.matcher(distancia);

        if (matcher.find()) {
            String distanciaStr = matcher.group(1); // Obtener el número (con o sin decimales) como una cadena
            double totalDistancia = Double.parseDouble(distanciaStr); // Convertir la cadena a un valor double
            Log.d("totaldistanciaDespués", String.valueOf(totalDistancia));
            return totalDistancia;
        } else {
            throw new IllegalArgumentException("Formato de cadena incorrecto: " + distancia);
        }
    }
}