package com.example.ecoproyect.Adapter;

import android.graphics.PorterDuff;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoproyect.Model.RecordItem;
import com.example.ecoproyect.R;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordItemViewHolder> {
    private ArrayList<RecordItem> recordItems;

    public RecordAdapter(ArrayList<RecordItem> recordItems) {
        this.recordItems = recordItems;
    }

    public class RecordItemViewHolder extends  RecyclerView.ViewHolder{
        private TextView textViewIdViaje, textViewFechaViaje, textViewPuntosViaje;
        private TextInputEditText textInputEditTextOrigen, textInputEditTextDestino,
        textInputEditTextDuracion, textInputEditTextDistancia, textInputEditTextVelocidadMedia;
        private LinearLayout linearLayoutCaracteristicas;
        private ImageView imageModeTravel;
        private CardView cardView;
        public RecordItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            linearLayoutCaracteristicas = itemView.findViewById(R.id.caracteristicasLayout);

            textViewIdViaje = itemView.findViewById(R.id.idViaje);
            textViewFechaViaje = itemView.findViewById(R.id.fecha_viaje);
            textViewPuntosViaje = itemView.findViewById(R.id.puntos);
            textInputEditTextOrigen = itemView.findViewById(R.id.origen);
            textInputEditTextDestino = itemView.findViewById(R.id.destino);
            textInputEditTextDuracion = itemView.findViewById(R.id.duracion);
            textInputEditTextDistancia = itemView.findViewById(R.id.distancia);
            textInputEditTextVelocidadMedia = itemView.findViewById(R.id.velocidadMedia);
            imageModeTravel = itemView.findViewById(R.id.recordImage);
        }
    }

    @NonNull
    @Override
    public RecordItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_list, parent, false);
        return new RecordItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordItemViewHolder holder, int position) {
        RecordItem currentItem = recordItems.get(position);
        holder.textInputEditTextDuracion.setText(currentItem.getDuracion());
        holder.textInputEditTextDistancia.setText(currentItem.getDistancia() + " km");
        holder.textInputEditTextDestino.setText(currentItem.getDestino());
        holder.textInputEditTextOrigen.setText(currentItem.getOrigen());
        holder.textInputEditTextVelocidadMedia.setText(currentItem.getVelocidad_media() + " km/H");
        holder.textViewIdViaje.setText("IdViaje: " + currentItem.getIdViaje());
        holder.textViewFechaViaje.setText("Fecha: " + currentItem.getFecha_creacion());
        holder.textViewPuntosViaje.setText("Puntos: " + currentItem.getPuntos());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.linearLayoutCaracteristicas.getVisibility() == View.GONE){
                    holder.linearLayoutCaracteristicas.setVisibility(View.VISIBLE);
                } else {
                    holder.linearLayoutCaracteristicas.setVisibility(View.GONE);
                }
            }
        });

        int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        holder.imageModeTravel.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        holder.imageModeTravel.setImageResource(currentItem.getImageModeTravel());

    }

    @Override
    public int getItemCount() {
        return recordItems.size();
    }
}
