package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.generarCodigoUnico;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.ecoproyect.databinding.ActivityModificarCuponBinding;

import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.util.HashMap;
import java.util.Map;

public class ModificarCuponActivity extends AppCompatActivity {

    ActivityModificarCuponBinding binding;

    String nombre, description, descuento, puntos;
    CouponItem couponItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModificarCuponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_modificar_cupon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        couponItem = (CouponItem) getIntent().getSerializableExtra("CouponItem");

        binding.nombreTienda.setText(couponItem.getTienda());
        binding.descripcion.setText(couponItem.getDescripcion());
        binding.descuento.setText(String.valueOf(couponItem.getDescuento()));
        binding.puntos.setText(String.valueOf(couponItem.getPuntos()));

        /*Log.d(BonusPackHelper.LOG_TAG, couponItem.getTienda());
        Log.d(BonusPackHelper.LOG_TAG, couponItem.getDescripcion());
        Log.d(BonusPackHelper.LOG_TAG, String.valueOf(couponItem.getId()));
        Log.d(BonusPackHelper.LOG_TAG, String.valueOf(couponItem.getDescuento()));
        Log.d(BonusPackHelper.LOG_TAG, String.valueOf(couponItem.getImagen()));
        Log.d(BonusPackHelper.LOG_TAG, String.valueOf(couponItem.getPuntos()));*/

        binding.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(ModificarCuponActivity.this);
                loadingDialog.startLoadingDialog();

                RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
                String url = urlServidor + "/modify-coupon.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(ModificarCuponActivity.this, "Modificado con éxito.", Toast.LENGTH_SHORT).show();
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
                        paramV.put("id", String.valueOf(couponItem.getId()));

                        return paramV;
                    }
                };

                queue.add(stringRequest);
            }
        });
    }
}