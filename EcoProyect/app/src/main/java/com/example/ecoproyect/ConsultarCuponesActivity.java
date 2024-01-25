package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
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
import com.example.ecoproyect.Adapter.CouponAdapter;
import com.example.ecoproyect.Adapter.CouponAdminAdapter;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.databinding.ActivityConsultarCuponesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultarCuponesActivity extends AppCompatActivity {
    ActivityConsultarCuponesBinding binding;
    ArrayList<CouponItem> couponItems;
    CouponAdminAdapter couponAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultarCuponesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_modify_coupons_admin);

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
        String url = urlServidor + "/consultar-cupones.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", response);
                    if (response.equals("Error")) {
                        Toast.makeText(ConsultarCuponesActivity.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray coupon = jsonArray.getJSONArray(i);

                            int id = Integer.parseInt(coupon.getString(0));
                            String patrocinador = coupon.getString(1);
                            String descripcion = coupon.getString(2);
                            String descuento = coupon.getString(3);
                            String puntos = coupon.getString(4);

                            couponItems.add(new CouponItem(patrocinador, descripcion, Integer.valueOf(descuento), Integer.valueOf(puntos), id));
                            Log.d(BonusPackHelper.LOG_TAG, String.valueOf(i));
                        }
                    }
                    couponAdapter = new CouponAdminAdapter(ConsultarCuponesActivity.this, couponItems);
                    binding.couponRecyclerView.setAdapter(couponAdapter);
                    loadingDialog.dismissDialog();
                } catch (JSONException e) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(ConsultarCuponesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(ConsultarCuponesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

        binding.addCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCuponActivity.class);
                intent.putExtra("idpatrocinador", String.valueOf(sharedPreferences.getInt("id", 0)));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Este código debe coincidir con el que usaste al iniciar AddCuponActivity
            if (resultCode == RESULT_OK) {
                CouponItem couponItem = (CouponItem)data.getSerializableExtra("couponItem");
                couponItems.add(couponItem);
                couponAdapter.notifyDataSetChanged();
            }
        }
    }

}