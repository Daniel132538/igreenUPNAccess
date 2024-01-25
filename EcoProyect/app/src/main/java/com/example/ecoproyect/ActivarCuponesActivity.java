package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
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
import com.example.ecoproyect.Adapter.ActivateCouponAdapter;
import com.example.ecoproyect.Adapter.CouponAdminAdapter;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.ActivateCouponItem;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.databinding.ActivityActivarCuponesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivarCuponesActivity extends AppCompatActivity {
    ArrayList<ActivateCouponItem> couponItems;
    ActivateCouponAdapter couponAdapter;
    ActivityActivarCuponesBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivarCuponesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.couponRecyclerView.setHasFixedSize(true);
        binding.couponRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sharedPreferences = getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        couponItems = new ArrayList<>();
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/activate-coupons.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.equals("Error")) {
                        Toast.makeText(ActivarCuponesActivity.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray coupon = jsonArray.getJSONArray(i);

                            int id = Integer.parseInt(coupon.getString(0));
                            int id_cupon = Integer.parseInt(coupon.getString(1));
                            String id_usuario = coupon.getString(2);
                            String codigo = coupon.getString(3);
                            int canjeable = Integer.parseInt(coupon.getString(4));
                            String nombreTienda = coupon.getString(6);
                            String descripcion = coupon.getString(7);
                            int descuento = Integer.parseInt(coupon.getString(8));
                            int puntos = Integer.parseInt(coupon.getString(9));
                            String nombrePatrocinador = coupon.getString(10);
                            String nombreUsuario = coupon.getString(11);
                            String email = coupon.getString(12);

                            ActivateCouponItem activateCouponItem = new ActivateCouponItem(
                                    id, codigo, canjeable, nombreTienda, descripcion, puntos, nombreUsuario, email,
                                    descuento, id_cupon
                            );

                            couponItems.add(activateCouponItem);
                            Log.d(BonusPackHelper.LOG_TAG, String.valueOf(i));
                        }
                    }
                    couponAdapter = new ActivateCouponAdapter(ActivarCuponesActivity.this, couponItems);
                    binding.couponRecyclerView.setAdapter(couponAdapter);
                    loadingDialog.dismissDialog();
                } catch (JSONException e) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(ActivarCuponesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(ActivarCuponesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("id", String.valueOf(sharedPreferences.getInt("id", 0)));
                Log.d("BONUSPACK: id", String.valueOf(sharedPreferences.getInt("id", 0)));
                return paramV;
            }
        };

        queue.add(stringRequest);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //setContentView(R.layout.activity_activar_cupones);
    }
}