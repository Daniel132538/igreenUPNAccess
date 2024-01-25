package com.example.ecoproyect.Adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecoproyect.AllConst.AllConst.generarCodigoUnico;
import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.cardview.widget.CardView;
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
import com.example.ecoproyect.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CouponAdapter extends  RecyclerView.Adapter<CouponAdapter.CouponItemViewHolder>{
    private ArrayList<CouponItem> couponItems;
    private Activity activity;
    public CouponAdapter(Activity activity, ArrayList<CouponItem> couponItems){
        this.activity = activity;
        this.couponItems = couponItems;
    }
    public static class CouponItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtDescuento;
        private TextView txtTienda;
        private ImageView couponImage;
        private Button btnCanjear;
        private TextInputLayout textInputLayout;
        private TextInputEditText textInputEditText;
        private CardView cardView;
        public CouponItemViewHolder(@NonNull View itemView) {
            super(itemView);
            couponImage = itemView.findViewById(R.id.couponImage);
            txtTienda = itemView.findViewById(R.id.txtTienda);
            txtDescuento = itemView.findViewById(R.id.txtDescuento);
            btnCanjear = itemView.findViewById(R.id.btnCanjear);
            textInputLayout = itemView.findViewById(R.id.textInputLayout);
            textInputLayout.setEnabled(false);
            textInputEditText = itemView.findViewById(R.id.descripcion);
            cardView = itemView.findViewById(R.id.card);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textInputLayout.getVisibility() == View.GONE){
                        textInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        textInputLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CouponItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item_list, parent, false);
        return new CouponItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponItemViewHolder holder, int position) {
        CouponItem couponItem = couponItems.get(position);
        holder.txtDescuento.setText(String.valueOf(couponItem.getDescuento()) + "%");
        holder.txtTienda.setText(couponItem.getTienda());
        holder.btnCanjear.setText("Canjear por " + couponItem.getPuntos() + " puntos");
        holder.textInputEditText.setText(couponItem.getDescripcion());

        holder.btnCanjear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

                if (Integer.valueOf(sharedPreferences.getString("puntuacion", "0")) >= couponItem.getPuntos()){
                    mostrarDialogoConfirmacion(holder, couponItem, view, sharedPreferences);
                    //Toast.makeText(view.getContext(), "Optas al descuento", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "No optas al descuento", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(view.getContext(), couponItem.getDescripcion(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponItems.size();
    }

    public void mostrarDialogoConfirmacion(CouponItemViewHolder holder, CouponItem couponItem , View view, SharedPreferences sharedPreferences) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Confirmacion de cupon")
                .setCancelable(true)
                .setMessage("¿Estás seguro de que quieres canjear el cupón?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TextView txtPuntuacion = view.findViewById(R.id.puntuacion);

                        int nuevosPuntos = Integer.parseInt(sharedPreferences.getString("puntuacion", "")) - couponItem.getPuntos();
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("puntuacion", String.valueOf(nuevosPuntos));
                        edit.apply();
                        //txtPuntuacion.setText("Puntuacion: " +  nuevosPuntos);
                        
                        //activarCupon();
                        activarCupon(view, couponItem.getId(), sharedPreferences.getInt("id", 0));
                        //Toast.makeText(view.getContext(), "Has confirmado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hagas nada o muestra un mensaje de cancelación
                    }
                }).show();

        //AlertDialog dialog = builder.create();
        //dialog.show();
    }

    private void activarCupon(View view, int idcupon, int idusuario) {
        if (idcupon > 0) {
            LoadingDialog loadingDialog = new LoadingDialog(activity);
            loadingDialog.startLoadingDialog();

            //Log.d("idviaje", idViaje);
            String codigo = generarCodigoUnico();

            RequestQueue queue = Volley.newRequestQueue(view.getContext().getApplicationContext());
            String url = urlServidor + "/save-activate-coupon.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        mostrarCodigo(codigo);
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
                    paramV.put("id_usuario", String.valueOf(idusuario));
                    paramV.put("id_cupon", String.valueOf(idcupon));
                    paramV.put("codigo", codigo);
                    paramV.put("canjeable", "1");
                    return paramV;
                }
            };

            queue.add(stringRequest);
        } else Toast.makeText(activity, "Error de usuario.", Toast.LENGTH_SHORT).show();
    }

    private void mostrarCodigo(String codigo) {
        // Crear una instancia de Dialog
        Dialog customDialog = new Dialog(activity);
        customDialog.setContentView(R.layout.dialog_coupon);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Crear una instancia de CouponDialog y mostrar el diálogo
        CouponDialog couponDialog = new CouponDialog(customDialog, codigo);
        customDialog.show();

    }
}
