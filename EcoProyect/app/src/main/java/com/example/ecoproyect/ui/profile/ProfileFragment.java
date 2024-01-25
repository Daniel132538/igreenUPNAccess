package com.example.ecoproyect.ui.profile;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Login;
import com.example.ecoproyect.Model.Statistics;
import com.example.ecoproyect.Model.Usuario;
import com.example.ecoproyect.R;
import com.example.ecoproyect.databinding.FragmentProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    boolean passwordVisible = false;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        sharedPreferences = getActivity().getSharedPreferences(nameSharedPreferences, Context.MODE_PRIVATE);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Usuario usuario = ((MyApplication)getActivity().getApplication()).getUsuario();

        if (usuario != null){
            binding.email.setText(usuario.getEmail());
            binding.email.setEnabled(false);
            binding.username.setText(usuario.getName());
            binding.telephoneNumber.setText(usuario.getPhoneNumber());
            //binding.password.setText(usuario.getPassword());
            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.puntuacion.setText(usuario.getPuntuacion());
            binding.puntuacion.setEnabled(false);
            binding.spinner.setText(usuario.getPrefixNumber());
        }

        binding.guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usuario.getName().equals(binding.username.getText().toString()) ||
                        !usuario.getPassword().equals(binding.password.getText().toString()) ||
                        !usuario.getPhoneNumber().equals(binding.telephoneNumber.getText().toString())){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", binding.username.getText().toString());
                    editor.putString("numeroTelef", binding.telephoneNumber.getText().toString());
                    editor.putString("password", binding.password.getText().toString());
                    editor.apply();

                    usuario.setName(binding.username.getText().toString());
                    usuario.setPassword(binding.password.getText().toString());
                    usuario.setPhoneNumber(binding.telephoneNumber.getText().toString());
                    actualizarPerfil();
                } else {
                    Toast.makeText(getActivity(), "Debe modificar al menos una característica.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX() >= (binding.password.getRight() -
                            binding.password.getCompoundDrawables()[2].getBounds().width())){
                        // Cambiar la imagen de DrawableRight a "nueva_imagen.png"
                        if (passwordVisible){
                            Drawable newDrawable = getResources().getDrawable(R.drawable.not_visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            binding.password.setCompoundDrawables(null, null, newDrawable, null);
                            binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordVisible = false;
                        } else {
                            Drawable newDrawable = getResources().getDrawable(R.drawable.visible, null);
                            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
                            binding.password.setCompoundDrawables(null, null, newDrawable, null);
                            binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            passwordVisible = true;
                        }


                        return true;
                    }
                }
                return false;
            }
        });

        /*final TextView textView = binding.textGallery;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    private void actualizarPerfil() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = urlServidor + "/modificar-perfil.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                if (response.equals("success")){
                    Toast.makeText(getActivity(), "Modificado correctamente.", Toast.LENGTH_SHORT).show();
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
                paramV.put("email", binding.email.getText().toString());
                paramV.put("phone_number", binding.telephoneNumber.getText().toString());
                paramV.put("password", binding.password.getText().toString());
                paramV.put("name", binding.username.getText().toString());

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