package com.example.ecoproyect;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.Model.Umbral;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministradorActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;
    AppCompatButton button;
    Umbral selectedUmbral;
    private List<Umbral> umbrales; // Lista de objetos Umbral

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        // Inicializa los elementos de la interfaz de usuario
        spinner = findViewById(R.id.Spinner);
        editText = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Inicializa la lista de umbrales con datos de prueba (puedes cargarlos desde tu fuente de datos)
        umbrales = ((MyApplication)getApplicationContext()).getUmbrals();

        // Crea un adaptador personalizado para el Spinner
        ArrayAdapter<Umbral> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, umbrales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configura el adaptador y el listener para el Spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Maneja el cambio de selección
                selectedUmbral = umbrales.get(position);
                // Actualiza el EditText con el valor correspondiente
                editText.setText(String.valueOf(selectedUmbral.getValor()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No es necesario hacer nada aquí
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyUmbral();
            }
        });
    }

    private void modifyUmbral() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlServidor + "/modify-umbral.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                if (response.equals("success")) {
                    Toast.makeText(AdministradorActivity.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdministradorActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdministradorActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("nombre", selectedUmbral.getNombre());
                paramV.put("valor", editText.getText().toString());
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
}
