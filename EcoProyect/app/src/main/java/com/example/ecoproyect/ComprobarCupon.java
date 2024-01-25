package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.databinding.ActivityComprobarCuponBinding;

import java.util.HashMap;
import java.util.Map;

public class ComprobarCupon extends AppCompatActivity {

    ActivityComprobarCuponBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComprobarCuponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        binding.comprobarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("et1: ", binding.et1.getText().toString());
                if (!binding.et1.getText().toString().equals("") &&  !binding.et2.getText().toString().equals("")
                        &&  !binding.et3.getText().toString().equals("") &&  !binding.et5.getText().toString().equals("")
                        &&  !binding.et4.getText().toString().equals("") &&  !binding.et6.getText().toString().equals("")) {
                    String et1 = binding.et1.getText().toString();
                    String et2 = binding.et2.getText().toString();
                    String et3 = binding.et3.getText().toString();
                    String et4 = binding.et4.getText().toString();
                    String et5 = binding.et5.getText().toString();
                    String et6 = binding.et6.getText().toString();
                    String codigo = et1 + et2 + et3 + et4 + et5 + et6;

                    comprobarCodigo(codigo);
                } else showAlertDialog(ComprobarCupon.this, "Rellena todos los huecos");
            }
        });


        //setContentView(R.layout.activity_comprobar_cupon);
    }

    private void comprobarCodigo(String codigo) {
        LoadingDialog loadingDialog = new LoadingDialog(ComprobarCupon.this);
        loadingDialog.startLoadingDialog();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url =urlServidor + "/comprobarCupon.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            showAlertDialog(ComprobarCupon.this, "Codigo correcto");
                            loadingDialog.dismissDialog();
                            finish();
                        } else {
                            loadingDialog.dismissDialog();
                            showAlertDialog(ComprobarCupon.this, "Error, no existe ningun descuento con ese código. Pruebe otra vez.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                error.printStackTrace();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("codigo", codigo);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public void showAlertDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acciones que quieres realizar al hacer clic en Aceptar
                        dialog.dismiss(); // Cierra el diálogo
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}