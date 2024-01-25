package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.ecoproyect.Adapter.AutoCompleteAdapter;
import com.example.ecoproyect.Adapter.SpinnerFlagAdapter;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Model.SpinnerFlagItem;
import com.example.ecoproyect.databinding.ActivityRegisterVerificationBinding;
import com.example.ecoproyect.databinding.ActivityRegistrationBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Registration extends AppCompatActivity{
    AutoCompleteTextView spinner;
    ArrayList<SpinnerFlagItem> customList;
    //SpinnerFlagAdapter adapter;
    TextInputEditText textInputEditTextPassword, textInputEditTextRepeatPassword,
    textInputEditTextUsername, textInputEditTextEmail, textInputEditTextPhoneNumber, txtLogin;
    TextView textViewError;
    GifImageView progressBar;
    boolean passwordVisible, repeatPasswordVisible;
    Button buttonRegister;
    String email, password, repeat_password, name, phoneNumber, prefix = "";
    ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        passwordVisible = false;
        repeatPasswordVisible = false;

        buttonRegister = findViewById(R.id.register);

        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputEditTextRepeatPassword = findViewById(R.id.repeat_password);
        textInputEditTextRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputEditTextPhoneNumber = findViewById(R.id.telephoneNumber);

        textViewError = findViewById(R.id.error);

        progressBar = findViewById(R.id.progressBar);

        spinner = findViewById(R.id.spinner);

        customList = getCustomList();

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, R.layout.custom_spinner_layout, customList);
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener((parent, view, position, id) -> {
            SpinnerFlagItem selectedItem = adapter.getItem(position);
            if (selectedItem != null) {
                spinner.setText(selectedItem.getTelephonePrefix());
                prefix = selectedItem.getTelephonePrefix();
            }
        });

        spinner.setFocusable(false);
        spinner.setClickable(true);
        spinner.setCursorVisible(false);
        spinner.setKeyListener(null);


        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                spinner.showDropDown();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

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

        textInputEditTextRepeatPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX() >= (textInputEditTextRepeatPassword.getRight() -
                            textInputEditTextRepeatPassword.getCompoundDrawables()[2].getBounds().width())){
                        // Cambiar la imagen de DrawableRight a "nueva_imagen.png"
                        if (repeatPasswordVisible){
                            Drawable newDrawable = getResources().getDrawable(R.drawable.not_visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            textInputEditTextRepeatPassword.setCompoundDrawables(null, null, newDrawable, null);
                            textInputEditTextRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            repeatPasswordVisible = false;
                        } else {
                            Drawable newDrawable = getResources().getDrawable(R.drawable.visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            textInputEditTextRepeatPassword.setCompoundDrawables(null, null, newDrawable, null);
                            textInputEditTextRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            repeatPasswordVisible = true;
                        }


                        return true;
                    }
                }
                return false;
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textViewError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                name = String.valueOf(textInputEditTextUsername.getText());
                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                repeat_password = String.valueOf(textInputEditTextRepeatPassword.getText());
                phoneNumber = String.valueOf(textInputEditTextPhoneNumber.getText());
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url =urlServidor + "/register-before.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.equals("success")){
                                    sendEmail();
                                } else {
                                    textViewError.setText(response);
                                    textViewError.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        textViewError.setText(error.getLocalizedMessage());
                        Log.d("Error", error.getLocalizedMessage());
                        textViewError.setVisibility(View.VISIBLE);
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
        });


    }

    private void sendEmail(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/send_mail.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.contains("success")){
                            String[] auxResponse = response.split(",");
                            String[] datos = {name, email, phoneNumber, password, repeat_password, prefix};
                            Intent intent = new Intent(getApplicationContext(), RegisterVerification.class);
                            intent.putExtra("otp", auxResponse[1]);
                            intent.putExtra("datos", datos);
                            startActivity(intent);
                            finish();
                        } else {
                            textViewError.setText(response);
                            textViewError.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                textViewError.setText(error.getLocalizedMessage());
                textViewError.setVisibility(View.VISIBLE);
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", email);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private ArrayList<SpinnerFlagItem> getCustomList() {
        customList = new ArrayList<>();
        customList.add(new SpinnerFlagItem("+34",R.drawable.spain));
        customList.add(new SpinnerFlagItem("+49",R.drawable.german));
        customList.add(new SpinnerFlagItem("+33",R.drawable.france));
        customList.add(new SpinnerFlagItem("+39",R.drawable.italy));
        customList.add(new SpinnerFlagItem("+44",R.drawable.united_kingdom));
        customList.add(new SpinnerFlagItem("+7",R.drawable.rusian));

        return customList;
    }
}