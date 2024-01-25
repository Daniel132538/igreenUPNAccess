package com.example.ecoproyect.Adapter;

import android.app.Activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecoproyect.R;

public class BusAdapter extends ArrayAdapter<String> {
    private static final int POSITION_A_OCULTAR = 12;
    private final Activity context;
    private final String[] maintitle;
    private final Integer[] imgid;

    public BusAdapter(Activity context, String[] maintitle, Integer[] imgid) {
        super(context, R.layout.listbus_item, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {

        if (position == POSITION_A_OCULTAR) {
            // Devuelve una vista vacía para ocultar el elemento
            return new View(context);
        }

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listbus_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView iconText = (TextView) rowView.findViewById(R.id.icon);

        titleText.setText(maintitle[position]);
        iconText.setText(String.valueOf(position+1));
        //iconText.setBackgroundColor(imgid[position]);
        // Obtiene el fondo del TextView (debería ser un GradientDrawable)
        GradientDrawable backgroundDrawable = (GradientDrawable) iconText.getBackground().mutate();

        // Cambia el color del solid
        backgroundDrawable.setColor(imgid[position]);

        return rowView;

    };
}
