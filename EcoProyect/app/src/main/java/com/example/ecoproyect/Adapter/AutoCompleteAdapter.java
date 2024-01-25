package com.example.ecoproyect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecoproyect.Model.SpinnerFlagItem;
import com.example.ecoproyect.R;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<SpinnerFlagItem> {

    public AutoCompleteAdapter(Context context, int resource, List<SpinnerFlagItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ? convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);

        SpinnerFlagItem item = getItem(position);
        ImageView imageView = view.findViewById(R.id.flag);
        TextView textView = view.findViewById(R.id.country);

        // Configura la vista con los datos del item
        if (item != null) {
            imageView.setImageResource(item.getSpinnerItemImage());
            textView.setText(item.getTelephonePrefix());
        }

        return view;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
