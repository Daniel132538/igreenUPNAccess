package com.example.ecoproyect.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoproyect.Model.RankingItem;
import com.example.ecoproyect.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingItemViewHolder> {

    private ArrayList<RankingItem> rankingItems;

    public RankingAdapter(ArrayList<RankingItem> rankingItems){
        this.rankingItems = rankingItems;
    }

    public static class RankingItemViewHolder extends RecyclerView.ViewHolder {
        public TextView txtPosicion;
        public TextView txtUsuario;
        public TextView txtNumeroViajes;

        public RankingItemViewHolder(View itemView) {
            super(itemView);
            txtPosicion = itemView.findViewById(R.id.txtPosicion);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtNumeroViajes = itemView.findViewById(R.id.txtNumViajes);
        }
    }

    @NonNull
    @Override
    public RankingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item_list, parent, false);
        return new RankingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingItemViewHolder holder, int position) {
        RankingItem currentItem = rankingItems.get(position);
        holder.txtPosicion.setText(String.valueOf(position + 1));
        holder.txtUsuario.setText(currentItem.getNombreUsuario());
        holder.txtNumeroViajes.setText("Numero de viajes: " + currentItem.getNumeroViajes());
    }

    @Override
    public int getItemCount() {
        return rankingItems.size()  ;
    }
}
