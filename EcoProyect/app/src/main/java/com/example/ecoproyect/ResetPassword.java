package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.AllConst.AllConst;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class ResetPassword extends AppCompatActivity {

    EditText editText;
    Button btnSubmit;
    GifImageView progressBar;
    private String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        /*binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        editText = findViewById(R.id.email);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressBar = findViewById(R.id.progress);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = urlServidor + "/reset-password.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.contains("success")){
                                    String[] auxResponse = response.split(",");
                                    otp = auxResponse[1];
                                    Log.d("otp",auxResponse[1]);
                                    sendEmail();
                                } else {
                                    Toast.makeText(ResetPassword.this,response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("email", editText.getText().toString());
                        return paramV;
                    }
                };
                queue.add(stringRequest);
            }
        });

    }

    private void sendEmail(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/reset_password_mail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.equals("success")){
                            Intent intent = new Intent(getApplicationContext(), NewPassword.class);
                            intent.putExtra("email", editText.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPassword.this,response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", editText.getText().toString());
                paramV.put("otp", otp);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
}