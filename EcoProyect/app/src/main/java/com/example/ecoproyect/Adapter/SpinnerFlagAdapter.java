package com.example.ecoproyect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecoproyect.Model.SpinnerFlagItem;
import com.example.ecoproyect.R;

import java.util.ArrayList;

public class SpinnerFlagAdapter extends ArrayAdapter<SpinnerFlagItem> {
    public SpinnerFlagAdapter(@NonNull Context context, ArrayList<SpinnerFlagItem> customList) {
        super(context, 0, customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }
        SpinnerFlagItem item = getItem(position);


        ImageView spinnerImageView = convertView.findViewById(R.id.flag);
        TextView spinnerTV = convertView.findViewById(R.id.country);

        if (!item.equals(null)){
            spinnerImageView.setImageResource(item.getSpinnerItemImage());
            spinnerTV.setText(item.getTelephonePrefix());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }

        SpinnerFlagItem item = getItem(position);
        ImageView dropDownImageView = convertView.findViewById(R.id.flag);
        TextView dropDownTV = convertView.findViewById(R.id.country);

        if (!item.equals(null)){
            dropDownImageView.setImageResource(item.getSpinnerItemImage());
            dropDownTV.setText(item.getTelephonePrefix());
        }
        return convertView;
    }

}
