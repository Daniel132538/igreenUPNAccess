package com.example.ecoproyect.ui.incidencia;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Login;
import com.example.ecoproyect.Model.RecordItem;
import com.example.ecoproyect.Model.Usuario;
import com.example.ecoproyect.R;
import com.example.ecoproyect.databinding.FragmentIncidenciaBinding;
import com.example.ecoproyect.databinding.FragmentProfileBinding;
import com.example.ecoproyect.ui.profile.ProfileViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IncidenciaFragment extends Fragment {

    private IncidenciaViewModel incidenciaViewModel;
    FragmentIncidenciaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        incidenciaViewModel = new ViewModelProvider(this).get(IncidenciaViewModel.class);

        binding = FragmentIncidenciaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<RecordItem> recordItems = ((MyApplication)getActivity().getApplication()).getRecordItems();
        Usuario usuario = ((MyApplication) getActivity().getApplication()).getUsuario();

        String[] idViajes;
        if (recordItems != null) {
            idViajes = new String[recordItems.size() + 1];
            idViajes[0] = "Otra causa";
            for (int i = 1; i < recordItems.size() + 1; i++) {
                idViajes[i] = String.valueOf(recordItems.get(i - 1).getIdViaje());
            }
        } else {
            idViajes = new String[1];
            idViajes[0] = "Otra causa";
        }

        if (usuario != null){
            binding.email.setText(usuario.getEmail());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, idViajes);


        AutoCompleteTextView autoCompleteTextView = binding.autoCompleteTextView;
        autoCompleteTextView.setText("Otra causa");
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setClickable(true);
        autoCompleteTextView.setCursorVisible(false); 

        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                autoCompleteTextView.showDropDown();
            }
        });

        binding.envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.asunto.getText().toString().equals("") || binding.comentario.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Debes rellenar todo.", Toast.LENGTH_SHORT).show();
                } else {
                    enviarIncidencia();
                }
            }
        });

        return root;
    }

    private void enviarIncidencia(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = urlServidor + "/send-incidencia.php";
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(getActivity(), "Enviado correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(getActivity(), "error.getLocalizedMessage()", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("comentario", binding.comentario.getText().toString());
                paramV.put("asunto", binding.email.getText().toString() + " : " + binding.asunto.getText().toString());
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    private void enviarCorreo() {
        String[] destinatarios = {"danimendi2001@gmail.com"};
        String asunto = binding.email.getText().toString() + " : " + binding.asunto.getText().toString();
        String mensaje = binding.comentario.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, destinatarios);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo electrónico"));
        } catch (android.content.ActivityNotFoundException ex) {
            // Manejar la excepción si no hay ninguna aplicación de correo instalada
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}