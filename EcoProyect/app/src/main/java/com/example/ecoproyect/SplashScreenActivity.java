package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.agent;
import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Model.Umbral;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);
        recogerDatosUmbrales(this, getApplicationContext());
        weatherDescription();

        //((MyApplication) this.getApplication()).setValorInicial("hola");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private void recogerDatosUmbrales(Activity activity, Context context){
        ArrayList<Umbral> umbrals = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        String url = urlServidor + "/select-umbrales.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.equals("Error")) {
                        //Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray umbral = jsonArray.getJSONArray(i);

                            String nombre = umbral.getString(1);
                            double  valor = Double.parseDouble(umbral.getString(2));

                            Log.d("Umbrales: ", nombre + ":" + valor);
                            editor.putString(nombre, umbral.getString(2));

                            umbrals.add(new Umbral(nombre, valor));
                        }
                        ((MyApplication) getApplication()).setUmbrals(umbrals);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }
    public void weatherDescription() {
        new WeatherDescription().execute();
    }

    private class WeatherDescription extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject  doInBackground(Void... voids) {
            try {
                String url = "https://www.el-tiempo.net/api/json/v2/provincias/31/municipios/31201";
                Log.d(BonusPackHelper.LOG_TAG, "Tiempo.net: " + url);
                String result = BonusPackHelper.requestStringFromUrl(url, agent);
                return new JSONObject(result);
            } catch (Exception e) {
                Log.e(BonusPackHelper.LOG_TAG, "Error al procesar la respuesta JSON", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                Log.e(BonusPackHelper.LOG_TAG, "Tiempo.net: request failed.");
            } else {
                try {
                    //Log.d(BonusPackHelper.LOG_TAG, json.toString());

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Extraer datos generales
                    String nombre = json.getJSONObject("municipio").getString("NOMBRE");
                    String fecha = json.getString("fecha");
                    String temperaturaActual = json.getString("temperatura_actual");

                    // Extraer datos de temperaturas
                    String temperaturaMax = json.getJSONObject("temperaturas").getString("max");
                    String temperaturaMin = json.getJSONObject("temperaturas").getString("min");

                    // Extraer otros datos
                    String humedad = json.getString("humedad");
                    String viento = json.getString("viento");
                    String lluvia = json.getString("lluvia");

                    String description = json.getJSONObject("stateSky").getString("description");

                    editor.putString("nombre", nombre);
                    editor.putString("fecha", fecha);
                    editor.putString("temperaturaActual", temperaturaActual);
                    editor.putString("temperaturaMax", temperaturaMax);
                    editor.putString("temperaturaMin", temperaturaMin);
                    editor.putString("humedad", humedad);
                    editor.putString("viento", viento);
                    editor.putString("lluvia", lluvia);
                    editor.putString("description", description);

                    editor.apply();

                    // Extraer datos del pronóstico
                    JSONObject pronosticoHoy = json.getJSONObject("pronostico").getJSONObject("hoy");

                    // Extraer estados del cielo para hoy
                    JSONArray estadoCieloArray = pronosticoHoy.getJSONArray("estado_cielo_descripcion");

                    // Imprimir los datos
                    /*System.out.println("Nombre: " + nombre);
                    System.out.println("Fecha: " + fecha);
                    System.out.println("Temperatura Actual: " + temperaturaActual);
                    System.out.println("Temperatura Máxima: " + temperaturaMax);
                    System.out.println("Temperatura Mínima: " + temperaturaMin);
                    System.out.println("Humedad: " + humedad);
                    System.out.println("Viento: " + viento);
                    System.out.println("Lluvia: " + lluvia);*/
                    //System.out.println("Estados del cielo para hoy:" + estadoCieloArray.toString());
                    Log.d("Estados descripcion: ", estadoCieloArray.toString());
                    Log.d("size :", String.valueOf(estadoCieloArray.length()));
                    for (int i = 0; i < estadoCieloArray.length(); i++) {
                        String estadoCielo = estadoCieloArray.getString(i);
                        Log.d(BonusPackHelper.LOG_TAG, "- " + estadoCielo);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*private void checkWeather(Activity activity, Context context){
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        String url = "https://www.el-tiempo.net/api/json/v2/provincias/31/municipios/31201";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("Error")) {
                        //Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject json= new JSONObject(response);
                        Log.d(BonusPackHelper.LOG_TAG, json.toString());

                        //SharedPreferences.Editor editor = sharedPreferences.edit();

                        // Extraer datos generales
                        String nombre = json.getJSONObject("municipio").getString("NOMBRE");
                        String fecha = json.getString("fecha");
                        String temperaturaActual = json.getString("temperatura_actual");

                        // Extraer datos de temperaturas
                        String temperaturaMax = json.getJSONObject("temperaturas").getString("max");
                        String temperaturaMin = json.getJSONObject("temperaturas").getString("min");

                        // Extraer otros datos
                        String humedad = json.getString("humedad");
                        String viento = json.getString("viento");
                        String lluvia = json.getString("lluvia");

                        // Extraer datos del pronóstico
                        JSONObject pronosticoHoy = json.getJSONObject("pronostico").getJSONObject("hoy");

                        // Extraer estados del cielo para hoy
                        JSONArray estadoCieloArray = pronosticoHoy.getJSONArray("estado_cielo_descripcion");

                        // Imprimir los datos
                    System.out.println("Nombre: " + nombre);
                    System.out.println("Fecha: " + fecha);
                    System.out.println("Temperatura Actual: " + temperaturaActual);
                    System.out.println("Temperatura Máxima: " + temperaturaMax);
                    System.out.println("Temperatura Mínima: " + temperaturaMin);
                    System.out.println("Humedad: " + humedad);
                    System.out.println("Viento: " + viento);
                    System.out.println("Lluvia: " + lluvia);
                        //System.out.println("Estados del cielo para hoy:" + estadoCieloArray.toString());
                        Log.d("Estados descripcion: ", estadoCieloArray.toString());
                        Log.d("size :", String.valueOf(estadoCieloArray.length()));
                        for (int i = 0; i < estadoCieloArray.length(); i++) {
                            String estadoCielo = estadoCieloArray.getString(i);
                            Log.d(BonusPackHelper.LOG_TAG, "- " + estadoCielo);
                        }
                    }
                } catch (JSONException e) {
                    //Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }*/

    /*public void recogerDatosTiempoAtmosferico() {
        new FetchWeatherTask().execute();
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://www.el-tiempo.net/api/json/v2/provincias/31";
            Log.d(BonusPackHelper.LOG_TAG, "Tiempo.net: " + url);
            return BonusPackHelper.requestStringFromUrl(url, agent);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.e(BonusPackHelper.LOG_TAG, "Tiempo.net: request failed.");
            } else {
                try {
                    JSONObject json = new JSONObject(result);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Obtener datos de la ciudad con ID 31201 (Pamplona)
                    JSONArray ciudades = json.getJSONArray("ciudades");
                    for (int i = 0; i < ciudades.length(); i++) {
                        JSONObject ciudad = ciudades.getJSONObject(i);
                        if (ciudad.getString("id").equals("31201")) {
                            String nombreCiudad = ciudad.getString("name");
                            String temperaturaMax = ciudad.getJSONObject("temperatures").getString("max");
                            String temperaturaMin = ciudad.getJSONObject("temperatures").getString("min");
                            JSONObject stateSky = ciudad.getJSONObject("stateSky");
                            String estadoCieloDescripcion = stateSky.getString("description");

                            editor.putString("temperaturaMax", temperaturaMax);
                            editor.putString("temperaturaMin", temperaturaMin);
                            editor.putString("estadoCieloDescripcion", estadoCieloDescripcion);

                            editor.apply();

                            Log.d(BonusPackHelper.LOG_TAG, "Datos de Pamplona:");
                            Log.d(BonusPackHelper.LOG_TAG, "Nombre: " + nombreCiudad);
                            Log.d(BonusPackHelper.LOG_TAG, "Temperatura Máxima: " + temperaturaMax);
                            Log.d(BonusPackHelper.LOG_TAG, "Temperatura Mínima: " + temperaturaMin);
                            Log.d(BonusPackHelper.LOG_TAG, "Estado del Cielo: " + estadoCieloDescripcion);
                            break; // Salir del bucle una vez que se haya encontrado la ciudad
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

}