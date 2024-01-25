package com.example.ecoproyect.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecoproyect.Model.Weather;
import com.example.ecoproyect.R;

public class WeatherDialog {

    Weather weather;
    Dialog dialog;

    TextView textViewNombre, textViewFecha, textViewTemperaturaActual, textViewTemperaturaMinima, textViewTemperaturaMaxima, textViewLluvia, textViewHumedad, textViewViento;
    ImageView imageView, btnClose;
    public WeatherDialog(Weather weather, Dialog dialog) {
        this.weather = weather;
        this.dialog = dialog;

        textViewNombre = dialog.findViewById(R.id.nombreCiudad);
        textViewFecha = dialog.findViewById(R.id.fechaWeather);
        textViewTemperaturaActual = dialog.findViewById(R.id.temperatura_actual);
        textViewTemperaturaMinima = dialog.findViewById(R.id.maxTemperature);
        textViewTemperaturaMaxima = dialog.findViewById(R.id.minTemperature);
        textViewLluvia = dialog.findViewById(R.id.lluvia);
        textViewHumedad = dialog.findViewById(R.id.humedad);
        textViewViento = dialog.findViewById(R.id.viento);
        imageView = dialog.findViewById(R.id.imageWeather);
        btnClose = dialog.findViewById(R.id.btnClose);

        textViewNombre.setText(weather.getNombre());
        textViewFecha.setText(weather.getFecha());
        textViewTemperaturaActual.setText(weather.getTemperaturaActual() + "º");
        textViewTemperaturaMaxima.setText(weather.getTemperaturaMax() + "º");
        textViewTemperaturaMinima.setText(weather.getTemperaturaMin() + "º");
        textViewLluvia.setText("Prob. lluvia: " + weather.getLluvia() + "%");
        textViewHumedad.setText("Humedad: " + weather.getHumedad() + "%");
        textViewViento.setText("Viento: " + weather.getViento() +"km/h");

        textViewTemperaturaMinima.setTextColor(Color.BLUE);
        textViewTemperaturaMaxima.setTextColor(Color.RED);

        imageView.setImageResource(weather.getWeatherPhoto());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
}
