package com.example.ecoproyect.ui.settings;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Login;
import com.example.ecoproyect.MainActivity;
import com.example.ecoproyect.databinding.FragmentSettingsBinding;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        sharedPreferences = getActivity().getSharedPreferences(nameSharedPreferences, Context.MODE_PRIVATE);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        /*final TextView textView = binding.textSlideshow;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    private void logout() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url =urlServidor + "/logout.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("logged", "");
                            editor.putInt("id", 0);
                            editor.putString("name", "");
                            editor.putString("email", "");
                            editor.putString("puntuacion", "");
                            editor.putString("apiKey", "");
                            editor.apply();
                            Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loadingDialog.dismissDialog();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("email", sharedPreferences.getString("email",""));
                paramV.put("apiKey", sharedPreferences.getString("apiKey",""));
                Log.d("apiKey", sharedPreferences.getString("apiKey",""));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}