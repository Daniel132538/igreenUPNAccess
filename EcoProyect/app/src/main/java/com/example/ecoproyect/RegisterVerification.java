package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.telephony.SmsManager;
import com.example.ecoproyect.databinding.ActivityRegisterVerificationBinding;

public class RegisterVerification extends AppCompatActivity {

    private static final int REQ_USER_CONSENT = 200;
    TextInputEditText etOTP;
    ActivityRegisterVerificationBinding binding;
    private int intentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentos = 0;

        binding.btnVeriicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("otp", getIntent().getExtras().getString("otp"));
                Log.d("etotp", binding.etOTP.getText().toString());
                if (getIntent().getExtras().getString("otp").equals(binding.etOTP.getText().toString())){
                    insertarUsuario();
                    Toast.makeText(RegisterVerification.this, "Verificación correcta.", Toast.LENGTH_SHORT).show();
                } else if (intentos < 2){
                    intentos++;
                    Toast.makeText(RegisterVerification.this, "Verificación incorrecta. Prueba otra vez", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterVerification.this, "Máximo de intentos.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        Toast.makeText(this, "VerificationLogin", Toast.LENGTH_SHORT).show();

    }

    private void insertarUsuario(){
        String[] datos = getIntent().getStringArrayExtra("datos");
        String name = datos[0];
        String email = datos[1];
        String phoneNumber = datos[2];
        String password = datos[3];
        String repeat_password = datos[4];
        String prefix = datos[5];
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url =urlServidor + "/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")){
                            Toast.makeText(RegisterVerification.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterVerification.this, response, Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                        loadingDialog.dismissDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(RegisterVerification.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("name", name);
                paramV.put("email", email);
                paramV.put("phone", phoneNumber);
                paramV.put("password", password);
                paramV.put("repeat_password", repeat_password);
                paramV.put("prefix", prefix);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

}