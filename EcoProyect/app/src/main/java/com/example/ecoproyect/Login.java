package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Adapter.RankingAdapter;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.Model.RankingItem;
import com.example.ecoproyect.Model.RecordItem;
import com.example.ecoproyect.Model.Statistics;
import com.example.ecoproyect.Model.Usuario;
import com.example.ecoproyect.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Login extends AppCompatActivity {
    Button buttonSubmit;
    TextView textViewNewPassword, textViewRegister, textViewError;
    TextInputEditText textInputEditTextPassword, textInputEditTextEmail;
    GifImageView progressBar;
    boolean passwordVisible;
    String email, password, name, apiKey, puntuacion, id, rol;
    SharedPreferences sharedPreferences;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonSubmit = findViewById(R.id.submit);

        progressBar = findViewById(R.id.progressBar);

        textViewError = findViewById(R.id.error);
        textViewRegister = findViewById(R.id.register);

        //Log.d("Valor Inicial: ", ((MyApplication) this.getApplication()).getValorInicial());


        binding.forgotPassoword.setPaintFlags(binding.forgotPassoword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputEditTextEmail = findViewById(R.id.email);

        passwordVisible = false;

        sharedPreferences = getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        if (sharedPreferences.getString("logged", "false").equals("true")){
            Usuario usuario = new Usuario(sharedPreferences.getInt("id", 0), sharedPreferences.getString("name", ""),
                    sharedPreferences.getString("email", ""), sharedPreferences.getString("prefijo", ""),
                    sharedPreferences.getString("numeroTelef", ""), sharedPreferences.getString("puntuacion", ""),
                    sharedPreferences.getString("password", ""), sharedPreferences.getString("fecha_creacion", ""));

            ((MyApplication)getApplication()).setUsuario(usuario);
            recogerEstadísticas();
            recogerEstadísticasMonth();
            recogerHistorial();
            recogerRanking();
            recogerCupones();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else if (sharedPreferences.getString("logged", "false").equals("true2")) {
            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(intent);
            finish();
        }
        textInputEditTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX() >= (textInputEditTextPassword.getRight() -
                            textInputEditTextPassword.getCompoundDrawables()[2].getBounds().width())){
                        // Cambiar la imagen de DrawableRight a "nueva_imagen.png"
                        if (passwordVisible){
                            Drawable newDrawable = getResources().getDrawable(R.drawable.not_visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            textInputEditTextPassword.setCompoundDrawables(null, null, newDrawable, null);
                            textInputEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordVisible = false;
                        } else {
                            Drawable newDrawable = getResources().getDrawable(R.drawable.visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            textInputEditTextPassword.setCompoundDrawables(null, null, newDrawable, null);
                            textInputEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            passwordVisible = true;
                        }


                        return true;
                    }
                }
                return false;
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url =urlServidor + "/login.php";

                LoadingDialog loadingDialog = new LoadingDialog(Login.this);
                loadingDialog.startLoadingDialog();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");

                                    if (status.equals("success")){
                                        name = jsonObject.getString("name");
                                        email = jsonObject.getString("email");
                                        apiKey = jsonObject.getString("apiKey");
                                        puntuacion = jsonObject.getString("puntuacion");
                                        id = jsonObject.getString("id");
                                        String prefijo = jsonObject.getString("prefix");
                                        String numeroTelef = jsonObject.getString("phone_number");
                                        String password = jsonObject.getString("password");
                                        String fecha_creación = jsonObject.getString("fecha_creación");
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("logged", "true");
                                        editor.putInt("id", Integer.valueOf(id));
                                        editor.putString("name", name);
                                        editor.putString("email", email);
                                        editor.putString("puntuacion", puntuacion);
                                        editor.putString("apiKey", apiKey);
                                        editor.putString("prefijo", prefijo);
                                        editor.putString("numeroTelef", numeroTelef);
                                        editor.putString("password", password);
                                        editor.putString("fecha_creacion", fecha_creación);
                                        editor.apply();

                                        Usuario usuario = new Usuario(Integer.valueOf(id), name, email, prefijo,
                                                numeroTelef, puntuacion, password, fecha_creación);

                                        ((MyApplication)getApplication()).setUsuario(usuario);

                                        recogerRanking();
                                        recogerEstadísticas();
                                        recogerEstadísticasMonth();
                                        recogerHistorial();
                                        recogerCupones();

                                        Thread.sleep(2000);

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (status.equals("successPatrocinador")){
                                        name = jsonObject.getString("name");
                                        email = jsonObject.getString("email");
                                        id = jsonObject.getString("id");

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("logged", "true2");
                                        editor.putInt("id", Integer.valueOf(id));
                                        editor.putString("name", name);
                                        editor.putString("email", email);
                                        editor.apply();

                                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (status.equals("successAdmin")){
                                        Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        textViewError.setText(message);
                                        textViewError.setVisibility(View.VISIBLE);
                                    }
                                    loadingDialog.dismissDialog();

                                } catch (JSONException e) {
                                    loadingDialog.dismissDialog();
                                    throw new RuntimeException(e);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dismissDialog();
                        progressBar.setVisibility(View.GONE);
                        textViewError.setText(error.getLocalizedMessage());
                        textViewError.setVisibility(View.VISIBLE);
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("email", email);
                        paramV.put("password", password);
                        return paramV;
                    }
                };
                queue.add(stringRequest);
            }
        });

        binding.forgotPassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
            }
        });

    }

    public void recogerEstadísticas(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/estadisticas.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {
                        JSONObject datos = jsonObject.getJSONObject("0");

                        Statistics statistics = new Statistics(datos.getString("andando"), datos.getString("bici"),
                                datos.getString("bici_electrica"), datos.getString("patinete_electrico"),
                                datos.getString("bus"), datos.getString("total"));

                        ((MyApplication) getApplication()).setStatistics(statistics);

                    } else {
                        Toast.makeText(Login.this, jsonObject.getString("response"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "error.getLocalizedMessage()", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", sharedPreferences.getString("email", ""));
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    public void recogerEstadísticasMonth(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/estadisticas-mes.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.contains("success")) {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject datos = jsonObject.getJSONObject("0");

                        Statistics statistics = new Statistics(datos.getString("andando"), datos.getString("bici"),
                                datos.getString("bici_electrica"), datos.getString("patinete_electrico"),
                                datos.getString("bus"), datos.getString("total"));


                        ((MyApplication) getApplication()).setStatisticsMonth(statistics);

                    } else {
                        //Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "error.getLocalizedMessage()", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", sharedPreferences.getString("email", ""));
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    public void recogerHistorial(){
        ArrayList<RecordItem> recordItems = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/select-record.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(BonusPackHelper.LOG_TAG, response);
                    if (response.equals("Error")) {
                        //Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray viaje = jsonArray.getJSONArray(i);
                            int tipoViajeImagen = Integer.parseInt(viaje.getString(10));

                            if (tipoViajeImagen == 1) {
                                tipoViajeImagen = R.drawable.baseline_directions_walk_24;
                            } else if (tipoViajeImagen == 2) {
                                tipoViajeImagen = R.drawable.baseline_pedal_bike_24;
                            } else if (tipoViajeImagen == 3) {
                                tipoViajeImagen = R.drawable.baseline_electric_bike_24;
                            } else if (tipoViajeImagen == 4) {
                                tipoViajeImagen = R.drawable.baseline_electric_scooter_24;
                            } else if (tipoViajeImagen == 5) {
                                tipoViajeImagen = R.drawable.baseline_directions_bus_24;
                            }

                            RecordItem recordItem = new RecordItem(
                                    viaje.getInt(0),
                                    viaje.getInt(1),
                                    viaje.getString(8),
                                    viaje.getString(6),
                                    viaje.getString(2),
                                    viaje.getString(3),
                                    viaje.getInt(9),
                                    tipoViajeImagen,
                                    viaje.getInt(11),
                                    viaje.getString(12),
                                    Double.valueOf(viaje.getString(13)));
                            recordItems.add(recordItem);
                        }
                        ((MyApplication) getApplication()).setRecordItems(recordItems);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "error.getLocalizedMessage()", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", sharedPreferences.getString("email", ""));
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    public void recogerRanking(){
        ArrayList<RankingAdapter> rankingAdapters = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/select-ranking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.equals("Error")) {
                        Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<RankingItem> rankingItems;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray innerArray = jsonArray.getJSONArray(i);
                            rankingItems = new ArrayList<>();
                            for (int j = 0; j < innerArray.length(); j++) {
                                JSONArray ranking = innerArray.getJSONArray(j);
                                rankingItems.add(new RankingItem(ranking.getString(0), ranking.getString(1)));
                            }
                            if (rankingItems.size() > 0) {
                                rankingAdapters.add(new RankingAdapter(rankingItems));
                            } else {
                                rankingItems.add(new RankingItem("No hay ranking", "No hay ranking"));
                                rankingAdapters.add(new RankingAdapter(rankingItems));
                            }
                        }
                        ((MyApplication)getApplication()).setRankingAdapters(rankingAdapters);
                    }
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "error.getLocalizedMessage()", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("boolean", "true");
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    public void recogerCupones(){
        ArrayList<CouponItem> couponItems = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/select-coupons.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.equals("Error")) {
                        //Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray coupon = jsonArray.getJSONArray(i);

                            String patrocinador = coupon.getString(0);
                            String descripcion = coupon.getString(1);
                            String descuento = coupon.getString(2);
                            String puntos = coupon.getString(3);
                            int id = Integer.parseInt(coupon.getString(4));

                            couponItems.add(new CouponItem(patrocinador, descripcion, Integer.valueOf(descuento), Integer.valueOf(puntos), id));

                        }
                        ((MyApplication)getApplication()).setCouponItems(couponItems);
                    }

                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("boolean", "true");
                return paramV;
            }
        };

        queue.add(stringRequest);
    }
}