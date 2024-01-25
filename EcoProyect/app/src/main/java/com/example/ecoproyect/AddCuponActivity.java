package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.databinding.ActivityAddCuponBinding;

import java.util.HashMap;
import java.util.Map;

public class AddCuponActivity extends AppCompatActivity {
    ActivityAddCuponBinding binding;
    CouponItem couponItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCuponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setContentView(R.layout.activity_add_cupon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.addCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(AddCuponActivity.this);
                loadingDialog.startLoadingDialog();

                RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
                String url = urlServidor + "/add-coupon.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("success")) {
                            String aux[] = response.split(",");
                            Toast.makeText(AddCuponActivity.this, "Añadido con éxito.", Toast.LENGTH_SHORT).show();
                            couponItem = new CouponItem(binding.nombreTienda.getText().toString(), binding.descripcion.getText().toString(),
                                    Integer.valueOf(binding.descuento.getText().toString()),Integer.valueOf(binding.puntos.getText().toString()),
                                    Integer.valueOf(aux[1]));
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("couponItem", couponItem);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(view.getContext(), response, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismissDialog();
                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(view.getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("nombre", binding.nombreTienda.getText().toString());
                        paramV.put("description", binding.descripcion.getText().toString());
                        paramV.put("descuento", binding.descuento.getText().toString());
                        paramV.put("puntos", binding.puntos.getText().toString());
                        paramV.put("idpatrocinador", getIntent().getStringExtra("idpatrocinador"));

                        Log.d("nombre", binding.nombreTienda.getText().toString());
                        Log.d("description", binding.nombreTienda.getText().toString());
                        Log.d("descuento", binding.nombreTienda.getText().toString());
                        Log.d("puntos", binding.nombreTienda.getText().toString());
                        Log.d("idpatrocinador", getIntent().getStringExtra("idpatrocinador"));

                        return paramV;
                    }
                };

                queue.add(stringRequest);
            }
        });
    }
}