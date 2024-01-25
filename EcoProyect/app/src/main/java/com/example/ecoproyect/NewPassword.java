package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class NewPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        String email = getIntent().getExtras().getString("email");
        EditText editTextNewPassword = findViewById(R.id.new_password);
        EditText editTextOtp = findViewById(R.id.otp);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        GifImageView progressBar = findViewById(R.id.progress);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = urlServidor + "/new-password.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.equals("success")){
                                    Toast.makeText(NewPassword.this,"New password set.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    //Intent intent = new Intent(getApplicationContext(), Login.class);
                                    //startActivity(intent);
                                } else {
                                    Toast.makeText(NewPassword.this,response, Toast.LENGTH_SHORT).show();
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

                        paramV.put("email", email);
                        paramV.put("otp", editTextOtp.getText().toString());
                        paramV.put("new_password", editTextNewPassword.getText().toString());

                        return paramV;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }
}