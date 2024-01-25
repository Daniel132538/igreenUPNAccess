package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ecoproyect.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_admin);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        sharedPreferences = getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        binding.btnComprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComprobarCupon.class);
                startActivity(intent);
            }
        });

        binding.btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsultarCuponesActivity.class);
                startActivity(intent);
            }
        });

        binding.btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivarCuponesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Verifica si el elemento de menú seleccionado coincide con el ID deseado
        if (id == R.id.logout) {
            // Llama a la función que deseas ejecutar
            logout();
            return true; // Indica que la selección del menú ha sido manejada
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("logged", "");
        editor.putInt("id", 0);
        editor.putString("name", "");
        editor.putString("email", "");
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}