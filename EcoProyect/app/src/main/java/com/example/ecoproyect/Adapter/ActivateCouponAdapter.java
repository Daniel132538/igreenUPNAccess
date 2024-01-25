package com.example.ecoproyect.Adapter;

import static com.example.ecoproyect.AllConst.AllConst.generarCodigoUnico;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.ActivateCouponItem;
import com.example.ecoproyect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivateCouponAdapter extends RecyclerView.Adapter<ActivateCouponAdapter.ActivateCouponItemViewHolder> {
    private ArrayList<ActivateCouponItem> arrayList;
    private Activity activity;

    public ActivateCouponAdapter(Activity activity, ArrayList<ActivateCouponItem> arrayList) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    public static class ActivateCouponItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNombre, textViewEmail, textViewNombreTienda, textViewCodigo;
        private Button btnActivar;
        public ActivateCouponItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.txtNombreUsuario);
            textViewEmail = itemView.findViewById(R.id.txtEmail);
            textViewNombreTienda = itemView.findViewById(R.id.txtNombreTienda);
            textViewCodigo = itemView.findViewById(R.id.txtCodigo);
            btnActivar = itemView.findViewById(R.id.btnActivar);
        }

    }

    @NonNull
    @Override
    public ActivateCouponItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activate_coupon_item, parent, false);
        return new ActivateCouponAdapter.ActivateCouponItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivateCouponItemViewHolder holder, int position) {
        ActivateCouponItem activateCouponItem = arrayList.get(position);
        holder.textViewNombre.setText(activateCouponItem.getNombreUsuario());
        holder.textViewEmail.setText(activateCouponItem.getEmail());
        holder.textViewNombreTienda.setText(activateCouponItem.getTienda());
        holder.textViewCodigo.setText(activateCouponItem.getCodigo());

        if (activateCouponItem.getCanjeable() == 0){
            holder.btnActivar.setBackgroundResource(R.drawable.btn_desactivado);
            holder.btnActivar.setText("DESACTIVADO");
        } else {
            holder.btnActivar.setBackgroundResource(R.drawable.btn_activado);
            holder.btnActivar.setText("ACTIVADO");
        }


        holder.btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnActivar.getBackground().getConstantState().equals(
                        ContextCompat.getDrawable(view.getContext(), R.drawable.btn_activado).getConstantState())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Desactivar cupon")
                            .setCancelable(true)
                            .setMessage("¿Estás seguro de que deseas desactivar el cupón?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    desactivarCupon(holder, view, activateCouponItem);
                                }
                            })
                            .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Activacion cupon")
                            .setCancelable(true)
                            .setMessage("¿Estás seguro de que deseas activar el cupón?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    activarCupon(holder, view, activateCouponItem);
                                }
                            })
                            .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).show();
                }

            }
        });
    }

    private void activarCupon(@NonNull ActivateCouponItemViewHolder holder, View view, ActivateCouponItem activateCouponItem) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        //Log.d("idviaje", idViaje);
        String codigo = generarCodigoUnico();

        RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
        String url = urlServidor + "/update-activate-coupon.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    holder.btnActivar.setBackgroundResource(R.drawable.btn_activado);
                    holder.btnActivar.setText("ACTIVADO");
                    //Toast.makeText(view.getContext(), "Cupon activado guardado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Error al guardar cupón activado", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(view.getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("id", String.valueOf(activateCouponItem.getId()));
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    private void desactivarCupon(@NonNull ActivateCouponItemViewHolder holder, View view, ActivateCouponItem activateCouponItem) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        //Log.d("idviaje", idViaje);
        String codigo = generarCodigoUnico();

        RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
        String url = urlServidor + "/update-desactivate-coupon.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    holder.btnActivar.setBackgroundResource(R.drawable.btn_desactivado);
                    holder.btnActivar.setText("DESACTIVADO");
                    //Toast.makeText(view.getContext(), "Cupon activado guardado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Error al guardar cupón activado", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                Toast.makeText(view.getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("id", String.valueOf(activateCouponItem.getId()));
                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
