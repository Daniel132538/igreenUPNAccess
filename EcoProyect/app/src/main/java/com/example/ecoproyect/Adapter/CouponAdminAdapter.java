package com.example.ecoproyect.Adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecoproyect.AllConst.AllConst.generarCodigoUnico;
import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Dialog.CouponDialog;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.ModificarCuponActivity;
import com.example.ecoproyect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CouponAdminAdapter extends  RecyclerView.Adapter<CouponAdminAdapter.CouponItemViewHolder>{
    private ArrayList<CouponItem> couponItems;
    private Activity activity;
    public CouponAdminAdapter(Activity activity, ArrayList<CouponItem> couponItems){
        this.activity = activity;
        this.couponItems = couponItems;
    }
    public static class CouponItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNombreTienda;
        private TextView textViewDescripcion;
        private Button buttonModificar, buttonEliminar;
        public CouponItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreTienda = itemView.findViewById(R.id.nombreTienda);
            textViewDescripcion = itemView.findViewById(R.id.descripcion);
            buttonModificar = itemView.findViewById(R.id.modificarCupon);
            buttonEliminar = itemView.findViewById(R.id.eliminarCupon);
        }
    }

    @NonNull
    @Override
    public CouponItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item_admin, parent, false);
        return new CouponItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponItemViewHolder holder, int position) {
        CouponItem couponItem = couponItems.get(position);
        holder.textViewNombreTienda.setText(couponItem.getTienda());
        holder.textViewDescripcion.setText(couponItem.getDescripcion());
        int i = position;
        holder.buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), ModificarCuponActivity.class);
                intent.putExtra("CouponItem", couponItem);
                activity.startActivity(intent);
            }
        });

        holder.buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Eliminación de cupon")
                    .setCancelable(true)
                    .setMessage("¿Estás seguro de que quieres eliminar el cupón?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            eliminarCupon(view, i, couponItem.getId());
                        }
                    })
                    .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // No hagas nada o muestra un mensaje de cancelación
                        }
                    }).show();
            }
        });
    }

    private void eliminarCupon(View view, int position, int id) {
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
        String url = urlServidor + "/delete-coupon.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    removeItem(position);
                    Toast.makeText(activity, "Eliminado correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), response, Toast.LENGTH_SHORT).show();
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
                paramV.put("id", String.valueOf(id));

                return paramV;
            }
        };

        queue.add(stringRequest);
    }

    public void removeItem(int position) {
        couponItems.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return couponItems.size();
    }


}
